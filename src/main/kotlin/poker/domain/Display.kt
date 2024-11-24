package poker.domain

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