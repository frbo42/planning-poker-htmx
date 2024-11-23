package poker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.*
import java.util.concurrent.ConcurrentHashMap
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
value class GameId(private val id: String) {
    override fun toString(): String {
        return id
    }
}

data class Observer(var lastAccess: Long = System.currentTimeMillis()) {
    fun ping() {
        lastAccess = System.currentTimeMillis()
    }
}

data class Game(
    val gamId: GameId,
    private var show: Boolean = false,
    private val players: MutableMap<UserName, Hand> = ConcurrentHashMap(),
    private val observers: MutableMap<UserName, Observer> = ConcurrentHashMap(),
) {

    private val userCleaner = AsyncUserCleaner(players,observers)

    fun addUser(userName: UserName, observer: Boolean) {
        if (observer) {
            observers.putIfAbsent(userName, Observer())
        } else {
            players.putIfAbsent(userName, Hand(null))
        }
    }

    fun selectionState(userName: UserName, card: String): String {
        return if (card == this.players[userName]?.card) "contrast" else "outline contrast"
    }

    fun selectCard(userName: UserName, card: String) {
        players[userName]?.card = card
    }

    fun show() {
        show = true
    }

    fun reset() {
        show = false
        players.values.forEach { it.reset() }
    }

    fun userDisplay(): List<Display> {
        var sortedHand = players.keys.sorted()
        var sortedObservers = observers.keys.sorted()

        val size = sortedHand.size.coerceAtLeast(sortedObservers.size)

        val displays: MutableList<Display> = mutableListOf<Display>()
        for (i in 0 until size) {
            val playerName = sortedHand.getOrNull(i)
            val observerName = sortedObservers.getOrNull(i)
            displays.add( Display(
                playerName,
                userState(playerName),
                cardValue(playerName),
                observerName
            ))
        }
        return displays
    }

    fun ping(userName: UserName) {
        players[userName]?.ping()
        observers[userName]?.ping()
        userCleaner.cleanUsers()
    }

    fun cardValue(userName: UserName?): String {
        if (!show) {
            return "\uD83C\uDCA0"
        }

        return players[userName]?.card ?: return "X"
    }

    fun userState(userName: UserName?): String {
        if(userName == null) {
            return ""
        }

        if (show) {
            return "Contrast outline"
        }
        if (players[userName]?.card == null) {
            return "secondary"
        }
        return "contrast"
    }

    fun canBeRemoved(): Boolean {
        var playersInactive = players.all { inactiveFor20s(System.currentTimeMillis(), it.value.lastAccess) }
        var observersInactive = observers.all { inactiveFor20s(System.currentTimeMillis(), it.value.lastAccess) }
        return playersInactive && observersInactive
    }

    fun isPlayer(userName: UserName): Boolean {
       return players.keys.contains(userName)
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

class AsyncUserCleaner(private val cards: MutableMap<UserName, Hand>,private val observers: MutableMap<UserName, Observer>) {
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
            cards.entries.removeIf { inactiveFor20s(currentTime, it.value.lastAccess) }
            observers.entries.removeIf { inactiveFor20s(currentTime, it.value.lastAccess) }
            isRunning.set(false)
        }
    }


    private fun checkedInLast10Seconds(currentTime: Long) = currentTime - lastCall < WAIT_TIME
}

private fun inactiveFor20s(currentTime: Long, hand: Long) = currentTime - hand > 2 * WAIT_TIME
