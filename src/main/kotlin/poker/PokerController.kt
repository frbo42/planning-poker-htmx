package poker

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(BASE_URL)
class PokerController(
    val service: GameService,
) {

    @GetMapping(value = ["", "/"], produces = [MediaType.TEXT_HTML_VALUE])
    fun getHome(): String {
        return home()
    }

    @GetMapping(value = ["/{gameId}", "/{gameId}/"], produces = [MediaType.TEXT_HTML_VALUE])
    fun getHomeWithGame(@PathVariable("gameId") gameId: String): String {
        return homeUserFragment(gameId)
    }

    @PostMapping(SET_GAME_ID)
    fun setGameId(@RequestParam("gameId") gameId: String, response: HttpServletResponse): String {
        service.createGame(gameId)
        return inputUserFragment(gameId)
    }

    @PostMapping("{gameId}/$SET_USER")
    fun setUser(@PathVariable("gameId") gameId: String, @RequestParam(name = "userName") userName: String): String {
        service.addUser(gameId, userName)
        return mainPage(gameId, userName)
    }

    @GetMapping("/{gameId}/$SCORE")
    fun getScore(@PathVariable("gameId") gameId: String, @RequestParam(name = "userName") userName: String): String {
        val game = service.getGame(gameId)
        return gameFragment(userName, game)
    }

    @PostMapping("/{gameId}$SELECT_CARD")
    fun selectCard(
        @PathVariable("gameId") gameId: String,
        @RequestParam(name = "selectedCard") selectedCard: String,
        @RequestParam(name = "userName") userName: String
    ): String {
        val game = service.getGame(gameId)
        game.selectCard(userName, selectedCard)
        return cards(userName, game)
    }

    @PostMapping("/{gameId}/show")
    fun show(@PathVariable("gameId")gameId: String, @RequestParam(name = "userName") userName: String): String {
        val game = service.getGame(gameId)
        game.show = true
        return gameFragment(userName, game)
    }

    @PostMapping("/{gameId}/reset")
    fun reset(@PathVariable("gameId")gameId: String, @RequestParam(name = "userName") userName: String): String {
        val game = service.getGame(gameId)
        game.reset()
        return gameFragment(userName, game)
    }
}