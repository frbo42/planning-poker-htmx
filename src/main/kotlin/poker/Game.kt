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
        return if (card == this.cards[userName]) "primary" else "secondary"
    }

    fun selectCard(userName: String, card: String) {
        cards[userName] = card
    }

    fun reset() {
        show = false
        cards.keys.forEach {
            cards[it] = null
        }
    }
}
