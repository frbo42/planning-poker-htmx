package poker

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.springframework.stereotype.Component

@Component
class Htmx(val game: Game) {

    private fun userState(userName: String): String {
        return if (game.show) {
            "$userName played: ${game.userCards[userName]}"
        } else {
            if (game.userCards[userName] == null) {
                "$userName still thinking"
            } else {
                "$userName ready"
            }
        }
    }

    fun gameFragment(userName: String): String {
        return createHTML().div {
            id = "game"
            section {
                id = "users"
                h2 { +"Users" }
                game.userCards.keys.forEach {
                    p {
                        +userState(it)
                    }
                }
            }
            section {
                h2 {
                    +"Cards"
                }
                div {
                    id = "cards"
                    buildCards(userName)
                }
            }
        }
    }

    fun cards(userName: String): String {
        return createHTML().div {
            id = "cards"
            buildCards(userName)
        }
    }


    private fun DIV.buildCards(userName: String) {
        Game.cards.forEach {
            button {
                classes = setOf(game.selectionState(userName, it))
                hxPost("/selectCard?selectedCard=${it}&userName=${userName}")
                hxTrigger("click")
                hxTarget("#cards")
                hxSwap("innerHTML")
                +it
            }
        }
    }
}