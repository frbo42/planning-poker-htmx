package poker

import kotlinx.html.dom.serialize
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController("/")
class PokerController(
    val game: Game,
    val htmx: Htmx
) {

    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun home(): String {
        return document().serialize()
    }

    @PostMapping("/user")
    fun login(@RequestParam(name = "userName") userName: String): String {
        game.addUser(userName)
        return mainPage(userName)
    }

    @GetMapping("/game")
    fun game(@RequestParam(name = "userName") userName: String): String {
        return htmx.gameScreen(userName)
    }

    @PostMapping("/selectCard")
    fun selectCard(
        @RequestParam(name = "selectedCard") selectedCard: String,
        @RequestParam(name = "userName") userName: String
    ): String {
        game.selectCard(userName, selectedCard)
        return htmx.cards(userName)
    }

    @PostMapping("/show")
    fun show(@RequestParam(name = "userName") userName: String): String {
        game.show = true
        return htmx.gameScreen(userName)
    }

    @PostMapping("/reset")
    fun reset(@RequestParam(name = "userName") userName: String): String {
        game.reset()
        return htmx.gameScreen(userName)
    }
}