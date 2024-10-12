package poker

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.stream.createHTML

const val SCORE = "score"
const val SET_USER = "setUser"
const val SELECT_CARD = "/selectCard"
const val SET_GAME_ID = "/setGameId"

private const val CARDS = "cards"
private const val GAME = "game"
private const val GAME_ID = "gameId"
private const val MAIN = "main"
private const val BODY = "body"
private const val TITLE = "Planning Poker"


private const val USER_NAME = "userName"

fun HTML.htmlHeader() {
    head {
        title { +TITLE }
        htmxScript()
        metas()
        styles()
    }
}

fun home(): String {
    return createHTMLDocument().html {
        lang = "en"
        htmlHeader()
        body {
            header {
                h1 { +TITLE }
                userDetailHGroup(null, null)
            }
            section {
                id = MAIN
                inputGameIdFragment()
            }
        }
    }.serialize()
}

fun homeUserFragment(gameId: String): String {
    return createHTMLDocument().html {
        lang = "en"
        htmlHeader()
        body {
            id = BODY
            header {
                h1 { +TITLE }
                userDetailHGroup(gameId, null)
            }
            section {
                id = MAIN
                inputUser(gameId)
            }
        }
    }.serialize()
}

private fun HEADER.userDetailHGroup(gameId: String?, userName: String?) {
    hGroup {
        id = "userDetails"
        div {
            classes = setOf("top-right")
            gameId?.let {
                p {
                    +"Game: $gameId"
                }
            }
            userName?.let {
                p {
                    +"User: $userName"
                }
            }
        }
    }
}

private fun SECTION.inputGameIdFragment() {
    section {
        form {
            hxPost(SET_GAME_ID)
            hxTrigger("submit")
            hxTarget(BODY)
            hxSwap("innerHTML")
            label {
                htmlFor = GAME_ID
                +"Game Id"
            }
            input {
                id = GAME_ID
                type = InputType.text
                name = GAME_ID
                placeholder = "Enter game id"
                required = true
            }
            button {
                type = ButtonType.submit
                +"Submit Game"
            }
        }
    }
}

fun SECTION.inputUser(gameId: String) {
    section {
        userInputFragment(gameId)
    }
}

private fun SECTION.userInputFragment(gameId: String) {
    form {
        hxPost("/${gameId}/${SET_USER}")
        hxTrigger("submit")
        hxTarget(BODY)
        hxSwap("innerHTML")
        hxPushUrl("/poker/${gameId}")
        label {
            htmlFor = USER_NAME
            +"User name"
        }
        input {
            id = USER_NAME
            type = InputType.text
            name = "userName"
            placeholder = "Enter username"
        }
        button {
            type = ButtonType.submit
            +"submit"
        }
    }
}

fun inputUserFragment(gameId: String): String {
    return createHTML().body {
        id = BODY
        header {
            h1 { +TITLE }
            userDetailHGroup(gameId, null)
        }
        section {
            id = MAIN
            userInputFragment(gameId)
        }
    }
}

private fun userState(userName: String, game: Game): String {
    return if (game.show) {
        "$userName played: ${game.cards[userName]}"
    } else {
        if (game.cards[userName] == null) {
            "$userName still thinking"
        } else {
            "$userName ready"
        }
    }
}

fun gameFragment(userName: String, game: Game): String {
    return createHTML().div {
        id = GAME
        section {
            h2 { +"Users" }
            game.cards.keys.forEach {
                p {
                    +userState(it, game)
                }
            }
        }
        section {
            h2 {
                +"Cards"
            }
            div {
                id = CARDS
                buildCards(userName, game)
            }
        }
    }
}

private fun DIV.buildCards(userName: String, game: Game) {
    Game.cards.forEach {
        button {
            classes = setOf(game.selectionState(userName, it))
            hxPost("/${game.gamId}${SELECT_CARD}?selectedCard=${it}&userName=${userName}")
            hxTrigger("click")
            hxTargetId(CARDS)
            hxSwap("innerHTML")
            +it
        }
    }
}

fun cards(userName: String, game: Game): String {
    return createHTML().div {
        id = CARDS
        buildCards(userName, game)
    }
}

fun mainPage(gameId: String, userName: String): String {
    return createHTML().body {
        id = BODY
        header {
            h1 { +TITLE }
            userDetailHGroup(gameId, userName)
        }
        section {
            id = SCORE
            hxGet("/${gameId}/${SCORE}?userName=${userName}")
            hxTrigger("load, every 2s")
            hxSwap("innerHTML")
        }
        section {
            button {
                hxPost("/${gameId}/show?userName=${userName}")
                hxTrigger("click")
                hxTargetId(GAME)
                hxSwap("outerHTML")
                +"Show Cards"
            }
            button {
                hxPost("/${gameId}/reset?userName=${userName}")
                hxTrigger("click")
                hxTargetId(GAME)
                hxSwap("innerHTML")
                +"Reset"
            }
        }
    }
}

private fun HEAD.metas() {
    meta {
        httpEquiv = "Content-Type"
        content = "text/html; charset=UTF-8"
    }
    meta {
        name = "viewport"
        content = "width=device-width, initial-scale=1"
    }
}

private fun HEAD.styles() {
    link {
        rel = "stylesheet"
        href = "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css"
//            href = "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.classless.min.css"
    }
    style {
        unsafe {
            +"""
                .top-right {
                    position: absolute;
                    top: 10px;
                    right: 10px;
                }
                """.trimIndent()
        }
    }
}

private fun HEAD.htmxScript() {
    script {
        src = "https://unpkg.com/htmx.org@2.0.1"
    }
}