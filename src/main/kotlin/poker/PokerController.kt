package poker

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
    fun getHomeWithGame(@PathVariable("gameId") gameId: ProjectId): String {
        return homeUserFragment(gameId)
    }

    @PostMapping(SET_GAME_ID)
    fun setGameId(@RequestParam("gameId") gameId: ProjectId): String {
        return inputUserFragment(gameId)
    }

    @PostMapping("{gameId}/$SET_USER")
    fun setUser(
        @PathVariable("gameId") gameId: ProjectId,
        @RequestParam(name = "userName") userName: UserName
    ): String {
        service.addUser(gameId, userName)
        return mainPage(gameId, userName)
    }

    @GetMapping("/{gameId}/$SCORE")
    fun getScore(
        @PathVariable("gameId") gameId: ProjectId,
        @RequestParam(name = "userName") userName: UserName
    ): String {
        val game = service.getScore(gameId, userName)
        return gameFragment(userName, game)
    }

    @PostMapping("/{gameId}$SELECT_CARD")
    fun selectCard(
        @PathVariable("gameId") gameId: ProjectId,
        @RequestParam(name = "selectedCard") selectedCard: String,
        @RequestParam(name = "userName") userName: UserName
    ): String {
        val game = service.selectCard(gameId, userName, selectedCard)

        return cards(userName, game)
    }

    @PostMapping("/{gameId}/show")
    fun show(@PathVariable("gameId") gameId: ProjectId, @RequestParam(name = "userName") userName: UserName): String {
        val game = service.show(gameId)

        return gameFragment(userName, game)
    }

    @PostMapping("/{gameId}/reset")
    fun reset(@PathVariable("gameId") gameId: ProjectId, @RequestParam(name = "userName") userName: UserName): String {
        val game = service.reset(gameId)

        return gameFragment(userName, game)
    }
}