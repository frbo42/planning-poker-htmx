package poker.domain

import java.util.concurrent.ConcurrentHashMap

private const val UNPLAYED_CARD = "X"

class Players() {

    fun addUser(name: UserName, observer: Boolean = false) {
        players.putIfAbsent(name, Hand(null, observer))
        players[name] = Hand(null, observer)
    }

    fun userHasCard(name: UserName, card: String) = card == this.players[name]?.card


    fun selectCard(name: UserName, card: String) {
        players[name]?.card = card
    }

    fun hide() {
        players.values.forEach { it.hide() }
    }

    fun size() = players.size

    fun ping(name: UserName) {
        if (!players.containsKey(name)) {
            addUser(name)
        }

        players[name]?.ping()
    }

    fun cardValue(name: UserName?) = players[name]?.card ?: UNPLAYED_CARD

    fun isPlayer(name: UserName) = players.keys.contains(name)

    fun coerceSize() =
        players.values.filter { it.observer }.size.coerceAtLeast(players.values.filter { !it.observer }.size)


    fun activePlayer(index: Int) = players
        .filter { !it.value.observer }
        .map { it.key }
        .getOrNull(index)

    fun observer(index: Int) = players
        .filter { it.value.observer }
        .map { it.key }
        .getOrNull(index)

    fun hasPlayed(name: UserName) = players[name]?.card != null

    private val players: MutableMap<UserName, Hand> = ConcurrentHashMap()
}