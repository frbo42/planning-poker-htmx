package poker

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*


@RestController("/")
class PokerController(
    val game: Game,
    val htmx: Htmx
) {

    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun getHome(): String {
        return home()
    }

    @PostMapping("/setGameId")
    fun gameId(@RequestParam("gameId") gameId: String, response: HttpServletResponse): String {
        return loginFragment(gameId)
    }

    @PostMapping("{gameId}/user")
    fun login(@PathVariable("gameId") gameId: String, @RequestParam(name = "userName") userName: String): String {
        game.addUser(userName)
        return mainPage(gameId, userName)
    }

    @GetMapping("/{gameId}/score")
    fun score(@PathVariable("gameId")gameId: String, @RequestParam(name = "userName") userName: String): String {
        return htmx.gameFragment(userName)
    }

    @PostMapping("/selectCard")
    fun selectCard(
        @RequestParam(name = "selectedCard") selectedCard: String,
        @RequestParam(name = "userName") userName: String
    ): String {
        game.selectCard(userName, selectedCard)
        return htmx.cards(userName)
    }

    @PostMapping("/{gameId}/show")
    fun show(@PathVariable("gameId")gameId: String, @RequestParam(name = "userName") userName: String): String {
        game.show = true
        return htmx.gameFragment(userName)
    }

    @PostMapping("/{gameId}/reset")
    fun reset(@PathVariable("gameId")gameId: String, @RequestParam(name = "userName") userName: String): String {
        game.reset()
        return htmx.gameFragment(userName)
    }
}