package poker.domain

data class Hand(
    var card: String?,
    var lastAccess: Long = System.currentTimeMillis(),
) {
    fun hide() {
        card = null
    }

    fun ping() {
        lastAccess = System.currentTimeMillis()
    }
}