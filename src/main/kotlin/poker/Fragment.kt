package poker

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.stream.createHTML

const val SET_USER = "setUser"
private const val GAME = "game"
private const val POKER = "poker"

private const val TITLE = "Planning Poker"

private val cards = listOf("?", "1", "2", "3", "5", "8", "13", "21")


fun HTML.header() {
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
        header()
        body {
            id = "body"
            h1 { +TITLE }
            section {
                id = POKER
                inputGameIdFragment()
            }
        }
    }.serialize()
}

private fun SECTION.inputGameIdFragment() {
    section {
        form {
            hxPost("/setGameId")
            hxTrigger("submit")
            hxTargetId(POKER)
            hxSwap("innerHTML")
            label {
                htmlFor = "gameId"
                +"Game Id"
            }
            input {
                id = "gameId"
                type = InputType.text
                name = "gameId"
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

fun inputUserFragment(gameId: String): String {
    return createHTML().section {
        id = "user"
        userDetails(gameId, null)

        form {
            hxPost("/${gameId}/${SET_USER}")
            hxTrigger("submit")
            hxTargetId(POKER)
            hxSwap("innerHTML")

            input {
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
            id = "users"
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
                id = "cards"
                buildCards(userName, game)
            }
        }
    }
}

private fun DIV.buildCards(userName: String, game: Game) {
    cards.forEach {
        button {
            classes = setOf(game.selectionState(userName, it))
            hxPost("/${game.gamId}/selectCard?selectedCard=${it}&userName=${userName}")
            hxTrigger("click")
            hxTarget("#cards")
            hxSwap("innerHTML")
            +it
        }
    }
}

fun cards(userName: String, game: Game): String {
    return createHTML().div {
        id = "cards"
        buildCards(userName, game)
    }
}
fun mainPage(gameId: String, userName: String): String {
    return createHTML().section {
        userDetails(gameId, userName)
        section {
            id = "score"
            hxGet("/${gameId}/score?userName=${userName}")
            hxTrigger("load, every 2s")
            hxSwap("innerHTML")
        }
        section {
            id = "button-bar"
            button {
                id = "show"
                hxPost("/${gameId}/show?userName=${userName}")
                hxTrigger("click")
                hxTargetId(GAME)
                hxSwap("outerHTML")
                +"Show Cards"
            }
            button {
                id = "reset"
                hxPost("/${gameId}/reset?userName=${userName}")
                hxTrigger("click")
                hxTargetId(GAME)
                hxSwap("innerHTML")
                +"Reset"
            }
        }
    }
}

private fun SECTION.userDetails(gameId: String, userName: String?) {
    div {
        classes = setOf("top-right")
        p {
            +"Game: $gameId"
        }
        userName?.let {
            p {
                +"User: $userName"
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
    meta {
        name = "color-scheme"
        content = "light dark"
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