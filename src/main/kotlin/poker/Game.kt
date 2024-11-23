package poker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

private val collator = Collator.getInstance(Locale.GERMAN)
private const val WAIT_TIME = 10_000

data class Hand(
    var card: String?,
    var lastAccess: Long = System.currentTimeMillis(),
) {
    fun reset() {
        card = null
    }

    fun ping() {
        lastAccess = System.currentTimeMillis()
    }
}


@JvmInline
value class UserName(private val name: String) : Comparable<UserName> {

    override fun compareTo(other: UserName): Int {
        return collator.compare(name, other.name)
    }

    override fun toString(): String {
        return name
    }
}

@JvmInline
value class ProjectId(private val id: String) {
    override fun toString(): String {
        return id
    }
}

data class Game(
    val gamId: ProjectId,
    private var show: Boolean = false,
    private val cards: MutableMap<UserName, Hand> = ConcurrentHashMap(),
    private val observers: MutableList<UserName> = CopyOnWriteArrayList(),
) {

    private val userCleaner = AsyncUserCleaner(cards)

    fun addUser(userName: UserName, observer: Boolean) {
        if (observer) {
            observers.add(userName)
        } else {
            cards.putIfAbsent(userName, Hand(null))
        }
    }

    fun selectionState(userName: UserName, card: String): String {
        return if (card == this.cards[userName]?.card) "contrast" else "outline contrast"
    }

    fun selectCard(userName: UserName, card: String) {
        cards[userName]?.card = card
    }

    fun show() {
        show = true
    }

    fun reset() {
        show = false
        cards.values.forEach { it.reset() }
    }

    fun userDisplay(): List<Display> {
        var sortedHand = cards.keys.sorted()
        var sortedObservers = observers.sorted()

        val size = Math.max(sortedHand.size, sortedObservers.size)

        val displays: MutableList<Display> = mutableListOf<Display>()
        for (i in 0 until size) {
            val hand = sortedHand.getOrNull(i)
            val observer = sortedObservers.getOrNull(i)
            displays.add( Display(
                hand,
                userState(hand),
                cardValue(hand),
                observer
            ))
        }
        println(displays)
        return displays
    }

    fun ping(userName: UserName) {
        cards[userName]?.ping()
        userCleaner.cleanUsers()
    }

    fun cardValue(userName: UserName?): String {
        if (!show) {
            return "\uD83C\uDCA0"
        }

        return cards[userName]?.card ?: return "X"
    }

    fun userState(userName: UserName?): String {
        if(userName == null) {
            return ""
        }

        if (show) {
            return "Contrast outline"
        }
        if (cards[userName]?.card == null) {
            return "secondary"
        }
        return "contrast"
    }

    fun canBeRemoved(): Boolean {
        return cards.all { inactiveFor20s(System.currentTimeMillis(), it.value) }
    }

    fun isPlayer(userName: UserName): Boolean {
       return cards.keys.contains(userName)
    }

    companion object {
        val cards = listOf("?", "1", "2", "3", "5", "8", "13", "21")
    }
}

data class Display(
    val playerName:UserName?,
    val state: String,
    val card: String,
    val observerName:UserName?
) {
    fun hasPlayer(): Boolean {
        return playerName != null
    }

    fun hasObserver(): Boolean {
        return observerName != null
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
            cards.entries.removeIf { inactiveFor20s(currentTime, it.value) }
            isRunning.set(false)
        }
    }


    private fun checkedInLast10Seconds(currentTime: Long) = currentTime - lastCall < WAIT_TIME
}

private fun inactiveFor20s(currentTime: Long, hand: Hand) = currentTime - hand.lastAccess > 2 * WAIT_TIME
