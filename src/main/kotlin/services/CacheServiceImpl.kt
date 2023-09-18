package services

import Action
import Store
import arrow.core.Either
import arrow.core.raise.catch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import logger.LoggerDelegate
import model.Message
import model.OnlineUser
import model.Sender
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

class CacheServiceImpl(override val store: Store, val fileSystem: FileSystem = FileSystem.SYSTEM) : CacheService {
    private val logger by LoggerDelegate()
    private lateinit var cacheDir: Path
    private val serializer = Json

    private var initalized = false

    override fun cache(user: OnlineUser, messages: List<Message>) {
        initializeIfNeeded()
        val file = userFile(user)

        logger.info("Writing messages for $user to $file")
        catch({
            fileSystem.write(file) {
                for (message in messages) {
                    val string = serializer.encodeToString(message)
                    writeUtf8(string)
                    writeUtf8("\n")
                }
            }
        }) {
            logger.error("Failed to write messages for $user to $file", it)
        }
    }

    override fun load(user: OnlineUser) {
        initializeIfNeeded()
        val messages : MutableList<Message> = mutableListOf()
        val file = userFile(user)

        logger.info("Loading messages for ${user.name} from $file")
        catch({
            fileSystem.read(file) {
                while (true) {
                    val line = readUtf8Line() ?: break
                    val message: Message = serializer.decodeFromString(line)
                    messages.add(message)
                }
            }
        }) {
            logger.error("Failed to load messages for ${user.name} from $file", it)
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
    }

    fun initializeIfNeeded() {
        if (! initalized) {
            initialize()
        }
    }

    fun initialize(): Path? {
        val cacheDir: Path? = cacheDir()

        cacheDir?.let { dir ->
            Either.catch {
                fileSystem.createDirectories(dir)
            }.onRight {
                this.cacheDir = dir
            }.onLeft {
                throw it
            }
        }

        initalized = true
        return cacheDir
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

    fun userFile(user: OnlineUser): Path {
        val dir = cacheDir / "user"
        fileSystem.createDirectories(dir)
        return dir / user.name
    }
}