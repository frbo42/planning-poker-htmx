package poker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

@Service
class GameService {

    val games = ConcurrentHashMap<String, Game>()
    val asyncCleaner = AsyncGameCleaner(games)

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

        asyncCleaner.cleanGames()
        return game
    }
}

class AsyncGameCleaner(private val games: MutableMap<String, Game>) {
    private val isRunning = AtomicBoolean(false)
    private val WAIT_TIME = 10_000
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
            games.entries.removeIf { it.value.cards.isEmpty() }
            isRunning.set(false)
        }
    }

    private fun checkedInLast10Seconds(currentTime: Long) = currentTime - lastCall < WAIT_TIME
}