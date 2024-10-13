package poker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

data class Hand(val card: String?, val lastAccess: Long = System.currentTimeMillis())

data class Game(
    val gamId: String,
    var show: Boolean = false,
    val cards: MutableMap<String, Hand> = ConcurrentHashMap()
) {
    private val userCleaner = AsyncCleaner(cards)

    fun addUser(userName: String) {
        println(userName + cards)
        cards.putIfAbsent(userName, Hand(null))
    }

    fun selectionState(userName: String, card: String): String {
        return if (card == this.cards[userName]?.card) "contrast" else "outline contrast"
    }

    fun selectCard(userName: String, card: String) {
        cards[userName] = Hand(card)
    }

    fun show() {
        show = true
    }

    fun reset() {
        show = false
        cards.keys.forEach {
            cards[it] = Hand(null)
        }
    }

    fun users(): List<String> {
        return cards.keys.sorted()
    }

    fun ping(userName: String) {
        println("ping: " + cards)
        val hand = cards[userName]
        cards[userName] = Hand(hand?.card)
        userCleaner.cleanUsers()
    }

    companion object {
        val cards = listOf("?", "1", "2", "3", "5", "8", "13", "21")
    }
}


class AsyncCleaner(private val cards: MutableMap<String, Hand>) {
    private val isRunning = AtomicBoolean(false)
    private val WAIT_TIME = 10_000
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private var lastCall: Long = 0

    fun cleanUsers() {
        val currentTime = System.currentTimeMillis()

        // Check if the process has been started in the last 10 seconds
        if (currentTime - lastCall < WAIT_TIME) {
            println("Process is already running, skipping this trigger." + LocalDateTime.now().format(formatter));
            return
        }

        // Mark the time of the current call
        lastCall = currentTime

        if (isRunning.compareAndSet(false, true)) {
            println("Starting the process..." + cards)

            // Start an asynchronous process (simulate by delay)
            CoroutineScope(Dispatchers.Default).launch {

                cards.entries.removeIf { currentTime - it.value.lastAccess > 2 * WAIT_TIME }

//                delay(2000L) // Simulating the process taking 2 seconds to complete
                println("Process finished." + cards)
                isRunning.set(false) // Allow future processes to start
            }
        } else {
            println("Process is already running, skipping this trigger.")
        }
    }
}
