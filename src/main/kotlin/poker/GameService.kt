package poker

import org.springframework.stereotype.Service

@Service
class GameService {

    val games = mutableMapOf<String, Game>()

    fun addUser(gameId: String, userName: String) {
        require(games.containsKey(gameId))

        games[gameId]?.addUser(userName)
    }

    fun createGame(gameId: String) {
        if (!games.containsKey(gameId)) {
            games[gameId] = Game(gameId)
        }
    }

    fun getGame(gameId: String): Game {
        if (!games.containsKey(gameId)) {
            createGame(gameId)
        }
        return games[gameId]!!
    }
}