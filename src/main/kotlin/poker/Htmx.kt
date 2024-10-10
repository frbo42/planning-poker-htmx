package poker

import org.springframework.stereotype.Component

@Component
class Htmx(val gameService: GameService) {

//    private fun userState(userName: String): String {
//        return if (gameService.show) {
//            "$userName played: ${gameService.userCards[userName]}"
//        } else {
//            if (gameService.userCards[userName] == null) {
//                "$userName still thinking"
//            } else {
//                "$userName ready"
//            }
//        }
//    }
//
//    fun gameFragment(userName: String): String {
//        return createHTML().div {
//            id = "game"
//            section {
//                id = "users"
//                h2 { +"Users" }
//                gameService.userCards.keys.forEach {
//                    p {
//                        +userState(it)
//                    }
//                }
//            }
//            section {
//                h2 {
//                    +"Cards"
//                }
//                div {
//                    id = "cards"
//                    buildCards(userName)
//                }
//            }
//        }
//    }
//
//    fun cards(userName: String): String {
//        return createHTML().div {
//            id = "cards"
//            buildCards(userName)
//        }
//    }
//
//
//    private fun DIV.buildCards(userName: String) {
//        GameService.cards.forEach {
//            button {
//                classes = setOf(gameService.selectionState(userName, it))
//                hxPost("/selectCard?selectedCard=${it}&userName=${userName}")
//                hxTrigger("click")
//                hxTarget("#cards")
//                hxSwap("innerHTML")
//                +it
//            }
//        }
//    }
}