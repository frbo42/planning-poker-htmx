package poker.infrastructure.ui

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.stream.createHTML
import poker.domain.Display
import poker.domain.Game
import poker.domain.GameId
import poker.domain.UserName

const val SCORE = "score"
const val SET_USER = "setUser"
const val SELECT_CARD = "/selectCard"
const val SET_GAME_ID = "/setGameId"
const val USER_NAME = "userName"

private const val CARDS = "cards"
private const val GAME = "game"
private const val GAME_ID = "gameId"
private const val MAIN_ID = "main"
private const val BODY = "body"
private const val TITLE = "Planning Poker"

private const val CSS_ALIGN_HEADER_RIGHT = "align-header-right"
private const val CSS_CARD_STACK = "card-stack"
private const val CSS_BUTTON_BAR = "button-bar"
private const val CSS_TEXT_RIGHT = "text-right"

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
            id = BODY
            headerBody()
            mainBody {
                inputGameIdFragment()
            }
        }
    }.serialize()
}

private fun BODY.mainBody(content: SECTION.() -> Unit) {
    main {
        classes = setOf("container")
        section {
            id = MAIN_ID
            content()
        }
    }
}

private fun SECTION.inputGameIdFragment() {
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

private fun BODY.headerBody(gameId: GameId? = null, userName: UserName? = null, observer:Boolean=false) {
    header {
        classes = setOf("container")
        h1 { +TITLE }
        userDetailHGroup(gameId, userName, observer )
    }
}

fun homeUserFragment(gameId: GameId): String {
    return createHTMLDocument().html {
        lang = "en"
        htmlHeader()
        body {
            id = BODY
            headerBody(gameId)
            mainBody {
                userInputFragment(gameId)
            }
        }
    }.serialize()
}

private fun HEADER.userDetailHGroup(gameId: GameId?, userName: UserName?, observer: Boolean ) {
    hGroup {
        classes = setOf(CSS_ALIGN_HEADER_RIGHT)
        div {
            gameId?.let {
                p {
                    a {
                        href = "/poker"
                        +"Game: $gameId"
                    }
                }
            }
            userName?.let {
                p {
                    a {
                        href = "/poker/$gameId"
                        +when {
                            observer -> "Observer: $userName"
                            else -> "Player: $userName"
                        }
                    }
                }
            }
        }
    }
}

private fun SECTION.userInputFragment(gameId: GameId) {
    form {
        hxPost("/${gameId}/${SET_USER}")
        hxTrigger("submit")
        hxTarget(BODY)
        hxSwap("innerHTML")
        hxPushUrl("/poker/${gameId}")
        fieldSet {
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
            label {
                input {
                    id = "observer"
                    type = InputType.checkBox
                    name = "observer"
                    role = "switch"
                }
                +"Observer"
            }
        }
        button {
            type = ButtonType.submit
            +"submit"
        }
    }
}

fun inputUserFragment(gameId: GameId): String {
    return createHTML().body {
        id = BODY
        headerBody(gameId)
        mainBody {
            userInputFragment(gameId)
        }
    }
}

fun gameFragment(userName: UserName, game: Game): String {
    return createHTML().div {
        id = GAME
        section {
            h2 { +"Users" }

            table {
                thead {
                    tr {
                        th { +"Users" }
                        th {}
                        th {
                            classes = setOf(CSS_TEXT_RIGHT)
                            +"Observers"
                        }
                    }
                }

                tbody {
                    game.userDisplay().forEach {
                        displayRow(it)
                    }
                }
            }

        }
        if (game.isPlayer(userName)) {
            section {
                h2 {
                    +"Cards"
                }
                div {
                    buildCards(userName, game)
                }
            }
        }
    }
}

private fun TBODY.displayRow(display: Display) {
    tr {
        td {
            when {
                display.hasPlayer() -> {
                    +"${display.playerName}"
                }
            }
        }
        td {
            when {
                display.hasPlayer() -> {
                    button {
                        classes = setOf(display.state)
                        +display.card
                    }
                }
            }
        }
        td {
            classes = setOf(CSS_TEXT_RIGHT)
            when {
                display.hasObserver() -> {
                    +"${display.observerName}"
                }
            }
        }
    }
}

private fun DIV.buildCards(userName: UserName, game: Game) {
    id = CARDS
    classes = setOf(CSS_CARD_STACK)
    Game.cards.forEach { card ->
        button {
            classes = setOf(game.selectionState(userName, card))
            hxPost("/${game.gamId}${SELECT_CARD}?selectedCard=${card}&userName=${userName}")
            hxTrigger("click")
            hxTargetId(CARDS)
            hxSwap("innerHTML")
            +card
        }
    }
}

fun cards(userName: UserName, game: Game): String {
    return createHTML().div {
        buildCards(userName, game)
    }
}

fun mainPage(gameId: GameId, userName: UserName, observer: Boolean): String {
    return createHTML().body {
        id = BODY
        headerBody(gameId, userName, observer )
        mainBody {
            section {
                id = SCORE
                hxGet("/${gameId}/${SCORE}?userName=${userName}")
                hxTrigger("load, every 2s")
                hxSwap("innerHTML")
            }
            section {
                classes = setOf(CSS_BUTTON_BAR)
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
                    classes = setOf("secondary")
                    +"Reset"
                }
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
        href = "/css/pico.min.css"
//        href = "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css"
    }
    style {
        unsafe {
            +"""
                .$CSS_ALIGN_HEADER_RIGHT {
                    display: flex;
                    justify-content: flex-end;
                }
                .$CSS_BUTTON_BAR {
                    display: flex;
                    gap: 1rem;
                }
                .$CSS_CARD_STACK {
                    display: flex;
                    gap: 1rem;
                }
                .$CSS_TEXT_RIGHT {
                    text-align: right;
                }
                """.trimIndent()
        }
    }
}

private fun HEAD.htmxScript() {
    script {
        src = "/js/htmx.min.js"
//        src = "https://unpkg.com/htmx.org@2.0.4"
    }
}