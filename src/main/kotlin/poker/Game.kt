package poker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

data class Hand(val card: String?, val lastAccess: Long = System.currentTimeMillis())

data class Game(
    val gamId: String,
    var show: Boolean = false,
    val cards: MutableMap<String, Hand> = ConcurrentHashMap()
) {
    private val userCleaner = AsyncCleaner(cards)

    fun addUser(userName: String) {
        println(userName + cards)
        cards.putIfAbsent(userName, Hand(null))
    }

    fun selectionState(userName: String, card: String): String {
        return if (card == this.cards[userName]?.card) "contrast" else "outline contrast"
    }

    fun selectCard(userName: String, card: String) {
        cards[userName] = Hand(card)
    }

    fun show() {
        show = true
    }

    fun reset() {
        show = false
        cards.keys.forEach {
            cards[it] = Hand(null)
        }
    }

    fun users(): List<String> {
        return cards.keys.sorted()
    }

    fun ping(userName: String) {
        cards[userName] = Hand(cards[userName]?.card)
        userCleaner.cleanUsers()
    }

    companion object {
        val cards = listOf("?", "1", "2", "3", "5", "8", "13", "21")
    }
}


class AsyncCleaner(private val cards: MutableMap<String, Hand>) {
    private val isRunning = AtomicBoolean(false)
    private val WAIT_TIME = 10_000
    private var lastCall: Long = 0

    fun cleanUsers() {
        val currentTime = System.currentTimeMillis()

        if (checkedInLast10Seconds(currentTime)) {
            return
        }

        lastCall = currentTime

        if (isRunning.compareAndSet(false, true)) {
            startAsyncCheck(currentTime)
        }
    }

    private fun startAsyncCheck(currentTime: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            cards.entries.removeIf { currentTime - it.value.lastAccess > 2 * WAIT_TIME }
            isRunning.set(false) // Allow future processes to start
        }
    }

    private fun checkedInLast10Seconds(currentTime: Long) = currentTime - lastCall < WAIT_TIME
}
