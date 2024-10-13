package poker

data class Game(
    val gamId: String,
    var show: Boolean = false,
    val cards: MutableMap<String, String?> = mutableMapOf()
) {

    fun addUser(userName: String) {
        cards.putIfAbsent(userName, null)
    }

    fun selectionState(userName: String, card: String): String {
        return if (card == this.cards[userName]) "contrast" else "outline contrast"
    }

    fun selectCard(userName: String, card: String) {
        cards[userName] = card
    }

    fun show() {
        show = true
    }

    fun reset() {
        show = false
        cards.keys.forEach {
            cards[it] = null
        }
    }

    fun users(): List<String> {
        return cards.keys.sorted()
    }

    companion object {
        val cards = listOf("?", "1", "2", "3", "5", "8", "13", "21")
    }
}
