package poker.domain

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