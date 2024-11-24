package poker.domain

import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.get

private const val WAIT_TIME = 20_000


data class Game(
    val gamId: GameId,
    private var show: Boolean = false,
    private val players: MutableMap<UserName, Hand> = ConcurrentHashMap(),
    private val observers: MutableMap<UserName, Observer> = ConcurrentHashMap(),
    private var lastAccess: Long = System.currentTimeMillis()
) {

    fun addUser(userName: UserName, observer: Boolean) {
        if (observer) {
            players.remove(userName)
            observers.putIfAbsent(userName, Observer())
        } else {
            observers.remove(userName)
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
        lastAccess = System.currentTimeMillis()
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
        return players.isEmpty() && observers.isEmpty() && inactiveFor20s(lastAccess)
    }

    fun isPlayer(userName: UserName): Boolean {
       return players.keys.contains(userName)
    }

    fun clean() {
        observers.entries.removeIf { inactiveFor20s(it.value.lastAccess) }
        players.entries.removeIf { inactiveFor20s(it.value.lastAccess) }
    }

    companion object {
        val cards = listOf("?", "1", "2", "3", "5", "8", "13", "21")
    }
}

private fun inactiveFor20s( timeStamp: Long) = System.currentTimeMillis() - timeStamp >  WAIT_TIME
