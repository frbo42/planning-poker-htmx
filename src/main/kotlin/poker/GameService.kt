package poker

import org.springframework.stereotype.Service

@Service
class GameService {

    val games = mutableMapOf<String, Game>()

    fun addUser(gameId: String, userName: String) {
        val game = getGame(gameId)

        game.addUser(userName)
    }

    fun getGame(gameId: String): Game {
        return games.getOrPut(gameId) { Game(gameId) }
    }

    fun selectCard(gameId: String, userName: String, selectedCard: String): Game {
        val game = getGame(gameId)

        game.selectCard(userName, selectedCard)

        return game;
    }

    fun show(gameId: String): Game {
        val game = getGame(gameId)

        game.show()

        return game
    }

    fun reset(gameId: String): Game {
        val game = getGame(gameId)

        game.reset()

        return game
    }

    fun getScore(gameId: String, userName: String): Game {
        val game = getGame(gameId)

        game.ping(userName)

        return game
    }
}