package poker

import org.springframework.stereotype.Service

@Service
class Game {

    var show: Boolean = false
    val userCards: MutableMap<String, String?> = mutableMapOf()

    fun selectionState(userName: String, card: String): String {
        return if (card == this.userCards[userName]) "primary" else "secondary"
    }

    fun addUser(userName: String) {
        userCards.putIfAbsent(userName, null)
    }

    fun selectCard(userName: String, card: String) {
        userCards[userName] = card
    }

    fun reset() {
        show = false
        userCards.keys.forEach {
            userCards[it] = null
        }
    }

    companion object {
        val cards = listOf("?", "1", "2", "3", "5", "8", "13", "21")
    }
}