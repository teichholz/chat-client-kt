import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.toKotlinLocalDateTime
import model.Message
import model.OnlineUser
import model.Sender
import okio.fakefilesystem.FakeFileSystem
import services.CacheService
import services.CacheServiceImpl
import java.time.LocalDateTime.now

class CacheServiceTest : FunSpec() {

    class MockStore : Store {
        var sendCalls: Int = 0
        var receiveCalls: Int = 0

        override fun send(action: Action) {
            when (action) {
                is Action.ConfirmSentMessage -> TODO()
                is Action.ReceiveMessage -> sendCalls++
                is Action.SendMessage -> receiveCalls++
            }
        }

        override val stateFlow: StateFlow<State>
            get() = TODO("Not yet implemented")
    }

    lateinit var mockStore: MockStore
    var fakeFileSystem = FakeFileSystem()
    lateinit var cacheService: CacheService

    init {
        beforeTest {
            mockStore = MockStore()
            cacheService = CacheServiceImpl(mockStore, fileSystem = fakeFileSystem)
        }


        test("cache and load") {
            val user = OnlineUser("test", "")
            val messages = listOf(
                Message("test", now().toKotlinLocalDateTime(), Sender.Self),
                Message("test", now().toKotlinLocalDateTime(), Sender.Other)
            )

            cacheService.cache(user, messages)
            cacheService.load(user)

            mockStore.sendCalls shouldBe 1
            mockStore.receiveCalls shouldBe 1
        }
    }
}