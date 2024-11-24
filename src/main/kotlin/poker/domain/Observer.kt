package poker.domain

data class Observer(var lastAccess: Long = System.currentTimeMillis()) {
    fun ping() {
        lastAccess = System.currentTimeMillis()
    }
}