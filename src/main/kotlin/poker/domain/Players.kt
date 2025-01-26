package poker.domain

import java.util.concurrent.ConcurrentHashMap

class Players() {
    fun remove(name: UserName) {
        players.remove(name)
    }

    fun addUser(name: UserName) {
        players.putIfAbsent(name, Hand(null))
    }

    fun userHasCard(name: UserName, card: String):Boolean {
        return card == this.players[name]?.card
    }

    fun selectCard(name: UserName, card: String) {
        players[name]?.card = card
    }

    fun hide() {
        players.values.forEach { it.hide() }
    }

    fun get(index: Int): UserName? {
        var sortedHand = players.keys.sorted()
       return sortedHand.getOrNull(index)
    }

    fun size(): Int {
return        players.size
    }

    fun ping(name: UserName) {
        if(!players.containsKey(name)) {
            addUser(name)
        }

        players[name]?.ping()
    }

    fun cardValue(name: UserName?): String? {
        return players[name]?.card
    }

    fun isPlayer(name: UserName): Boolean {
return        players.keys.contains(name)
    }

    private val players: MutableMap<UserName, Hand> = ConcurrentHashMap()
}