package poker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import poker.domain.Game
import poker.domain.GameId
import poker.domain.UserName
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

@Service
class GameService {

    private final val games = ConcurrentHashMap<GameId, Game>()
    private final val asyncCleaner = AsyncGameCleaner(games)

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

        game.reset()

        return game
    }

    fun getScore(gameId: GameId, userName: UserName): Game {
        val game = getGame(gameId)
        game.ping(userName)

        asyncCleaner.cleanGames()
        return game
    }
}

class AsyncGameCleaner(private val games: MutableMap<GameId, Game>) {
    private val isRunning = AtomicBoolean(false)
    private var lastCall: Long = 0

    fun cleanGames() {
        val currentTime = System.currentTimeMillis()

        if (checkedInLast10Seconds(currentTime)) {
            return
        }

        lastCall = currentTime

        if (isRunning.compareAndSet(false, true)) {
            startAsyncCheck()
        }
    }

    private fun startAsyncCheck() {
        CoroutineScope(Dispatchers.Default).launch {
            games.entries.removeIf { it.value.canBeRemoved() }
            isRunning.set(false)
        }
    }

    private fun checkedInLast10Seconds(currentTime: Long) = currentTime - lastCall < WAIT_TIME

    companion object {
        private const val WAIT_TIME = 10_000
    }
}