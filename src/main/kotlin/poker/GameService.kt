package poker

import org.springframework.stereotype.Service

@Service
class GameService {

    val games = mutableMapOf<String, Game>()

    fun createGame(gameId: String) {
        if (!games.containsKey(gameId)) {
            games[gameId] = Game(gameId)
        }
    }

    fun addUser(gameId: String, userName: String) {
        if (!games.containsKey(gameId)) {
            createGame(gameId)
        }

        games[gameId]?.addUser(userName)
    }

    fun getGame(gameId: String): Game {
        if (!games.containsKey(gameId)) {
            createGame(gameId)
        }
        return games[gameId]!!
    }
}