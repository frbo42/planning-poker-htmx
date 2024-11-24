package poker.infrastructure.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import poker.infrastructure.ui.BASE_URL
import poker.infrastructure.ui.SCORE
import poker.infrastructure.ui.SELECT_CARD
import poker.infrastructure.ui.SET_GAME_ID
import poker.infrastructure.ui.SET_USER
import poker.infrastructure.ui.USER_NAME
import poker.application.GameService
import poker.infrastructure.ui.cards
import poker.domain.GameId
import poker.domain.UserName
import poker.infrastructure.ui.gameFragment
import poker.infrastructure.ui.home
import poker.infrastructure.ui.homeUserFragment
import poker.infrastructure.ui.inputUserFragment
import poker.infrastructure.ui.mainPage

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
    fun getHomeWithGame(@PathVariable("gameId") gameId: GameId): String {
        return homeUserFragment(gameId)
    }

    @PostMapping(SET_GAME_ID)
    fun setGameId(@RequestParam("gameId") gameId: GameId): String {
        return inputUserFragment(gameId)
    }

    @PostMapping("{gameId}/${SET_USER}")
    fun setUser(
        @PathVariable("gameId") gameId: GameId,
        @RequestParam(name = USER_NAME) userName: UserName,
        @RequestParam(name = "observer", required = false) observer: Boolean?
    ): String {
        service.addUser(gameId,  userName, observer == true)
        return mainPage(gameId, userName)
    }

    @GetMapping("/{gameId}/${SCORE}")
    fun getScore(
        @PathVariable("gameId") gameId: GameId,
        @RequestParam(name = USER_NAME) userName: UserName
    ): String {
        val game = service.getScore(gameId, userName)
        return gameFragment(userName, game)
    }

    @PostMapping("/{gameId}${SELECT_CARD}")
    fun selectCard(
        @PathVariable("gameId") gameId: GameId,
        @RequestParam(name = "selectedCard") selectedCard: String,
        @RequestParam(name = USER_NAME) userName: UserName
    ): String {
        val game = service.selectCard(gameId, userName, selectedCard)

        return cards(userName, game)
    }

    @PostMapping("/{gameId}/show")
    fun show(@PathVariable("gameId") gameId: GameId, @RequestParam(name = USER_NAME) userName: UserName): String {
        val game = service.show(gameId)

        return gameFragment(userName, game)
    }

    @PostMapping("/{gameId}/reset")
    fun reset(@PathVariable("gameId") gameId: GameId, @RequestParam(name = USER_NAME) userName: UserName): String {
        val game = service.reset(gameId)

        return gameFragment(userName, game)
    }
}