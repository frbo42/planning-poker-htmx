package poker.application

import org.springframework.stereotype.Service
import poker.domain.Game
import poker.domain.GameId
import poker.domain.UserName
import java.util.concurrent.ConcurrentHashMap

@Service
class GameService {

    private final val games = ConcurrentHashMap<GameId, Game>()

    fun addUser(gameId: GameId, userName: UserName, observer: Boolean) {
        val game = getGame(gameId)

        game.addUser(userName, observer)
    }

    fun getGame(gameId: GameId): Game {
        return games.getOrPut(gameId) { Game(gameId) }
    }

    fun selectCard(gameId: GameId, userName: UserName, selectedCard: String): Game {
        val game = getGame(gameId)

        game.selectCard(userName, selectedCard)

        return game
    }

    fun show(gameId: GameId): Game {
        val game = getGame(gameId)

        game.show()

        return game
    }

    fun reset(gameId: GameId): Game {
        val game = getGame(gameId)

        game.hide()

        return game
    }

    fun getScore(gameId: GameId, userName: UserName): Game {
        val game = getGame(gameId)
        game.ping(userName)

        return game
    }

    fun clean() {
        games.entries.forEach { it.value.clean() }
        games.entries.removeIf {
            it.value.canBeRemoved()
        }
    }
}