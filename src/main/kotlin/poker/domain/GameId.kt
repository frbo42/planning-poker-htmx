package poker.domain

@JvmInline
value class GameId(private val id: String) {
    override fun toString(): String {
        return id
    }
}

