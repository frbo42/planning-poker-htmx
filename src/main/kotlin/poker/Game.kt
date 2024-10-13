package poker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

data class Hand(val card: String?, val lastAccess: Long = System.currentTimeMillis())

private val collator = Collator.getInstance(Locale.GERMANY)

@JvmInline
value class UserName(val name: String) : Comparable<UserName> {

    override fun compareTo(other: UserName): Int {
        return collator.compare(name, other.name)
    }

    override fun toString(): String {
        return name
    }

}

data class Game(
    val gamId: String,
    var show: Boolean = false,
    val cards: MutableMap<UserName, Hand> = ConcurrentHashMap()
) {
    private val userCleaner = AsyncUserCleaner(cards)

    fun addUser(userName: UserName) {
        cards.putIfAbsent(userName, Hand(null))
    }

    fun selectionState(userName: UserName, card: String): String {
        return if (card == this.cards[userName]?.card) "contrast" else "outline contrast"
    }

    fun selectCard(userName: UserName, card: String) {
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

    fun users(): List<UserName> {
        return cards.keys.sorted()
    }

    fun ping(userName: UserName) {
        cards[userName] = Hand(cards[userName]?.card)
        userCleaner.cleanUsers()
    }

    fun cardValue(userName: UserName): String {
        if (!show) {
            return "\uD83C\uDCA0"
        }

        return cards[userName]?.card ?: return "X"
    }

    fun userState(userName: UserName): String {
        if (show) {
            return "Contrast outline"
        }
        if (cards[userName]?.card == null) {
            return "secondary"
        }
        return "contrast"
    }

    companion object {
        val cards = listOf("?", "1", "2", "3", "5", "8", "13", "21")
    }
}


class AsyncUserCleaner(private val cards: MutableMap<UserName, Hand>) {
    private val isRunning = AtomicBoolean(false)
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
            isRunning.set(false)
        }
    }

    private fun checkedInLast10Seconds(currentTime: Long) = currentTime - lastCall < WAIT_TIME

    companion object {
        private const val WAIT_TIME = 10_000
    }
}
