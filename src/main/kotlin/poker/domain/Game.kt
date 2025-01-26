package poker.domain

private const val WAIT_TIME = 20_000


data class Game(
    val gamId: GameId,
    private var show: Boolean = false,
    private val players: Players = Players(),
    private var lastAccess: Long = System.currentTimeMillis()
) {

    fun addUser(userName: UserName, observer: Boolean) {
        players.addUser(userName, observer)
    }

    fun selectionState(userName: UserName, card: String): String {
        return if(players.userHasCard(userName, card))  "contrast" else "outline contrast"
    }

    fun selectCard(userName: UserName, card: String) {
        players.selectCard(userName, card)
    }

    fun show() {
        show = true
    }

    fun hide() {
        show = false
        players.hide()
    }

    fun userDisplay(): List<Display> {
        val size = players.coerceSize()

        val displays: MutableList<Display> = mutableListOf<Display>()
        for (i in 0 until size) {
            val playerName = players.activePlayer(i)
            val observerName = players.observer(i)
            displays.add( Display(
                playerName,
                userState(playerName),
                cardValue(playerName),
                observerName
            ))
        }
        return displays
    }

    fun ping(userName: UserName) {
        players.ping(userName)
        lastAccess = System.currentTimeMillis()
    }

    fun cardValue(userName: UserName?): String {
        if (!show) {
            return "\uD83C\uDCA0"
        }

        return players.cardValue(userName) ?: return "X"
    }

    fun userState(userName: UserName?): String {
        if(userName == null) {
            return ""
        }

        if (show) {
            return "Contrast outline"
        }
        if (players.cardValue(userName) == null) {
            return "secondary"
        }
        return "contrast"
    }

    fun canBeRemoved(): Boolean {
        return inactiveFor20s(lastAccess)
    }

    fun isPlayer(userName: UserName): Boolean {
       return players.isPlayer(userName)
    }

    companion object {
        val cards = listOf("?", "1", "2", "3", "5", "8", "13", "21")
    }
}

private fun inactiveFor20s( timeStamp: Long) = System.currentTimeMillis() - timeStamp >  WAIT_TIME
