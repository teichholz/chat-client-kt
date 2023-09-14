package services

import Action
import arrow.core.Either
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Message
import model.OnlineUser
import model.Sender
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.sink
import okio.source
import store

class CacheServiceImpl : CacheService {
    lateinit var cacheDir: Path

    var serializer = Json

    override fun initialize() {
        val cacheDir: Path? = cacheDir()

        cacheDir?.let { dir ->
            Either.catch {
                FileSystem.SYSTEM.createDirectory(dir)
            }.onRight {
                this.cacheDir = dir
            }
        }
    }

    fun cacheDir(): Path?  {
        var cache = System.getenv("XDG_CACHE_DIR")

        if (cache.isEmpty()) {
            cache = System.getenv("HOME").let {
                if (! it.isEmpty()) {
                    cache = "$it/.cache"
                }
                null
            }
        }

        return cache?.toPath()?.resolve("kt-chat-app")
    }

    override fun cache(user: OnlineUser, messages: List<Message>) {
        val dir = userDir(user)

        dir.toFile().sink().buffer().use { sink ->
            for (message in messages) {
                val string = serializer.encodeToString(message)
                sink.writeUtf8(string)
                sink.writeUtf8("\n")
            }
        }
    }

    override fun load(user: OnlineUser): List<Message> {
        val dir = userDir(user)

        val messages : MutableList<Message> = mutableListOf()
        dir.toFile().source().use {source ->
            source.buffer().use { buffer ->
                while (true) {
                    val line = buffer.readUtf8Line() ?: break
                    val message: Message = serializer.decodeFromString(line)
                    messages.add(message)
                }
            }
        }

        messages.forEach {
            when (it.sender) {
                is Sender.Other -> {
                    store.send(Action.ReceiveMessage(user, it))
                }
                is Sender.Self -> {
                    store.send(Action.SendMessage(user, it))
                }
            }
        }


        return emptyList()
    }

    fun userDir(user: OnlineUser): Path {
        val dir = cacheDir.resolve("user").resolve(user.name)
        FileSystem.SYSTEM.createDirectory(dir)
        return dir
    }
}