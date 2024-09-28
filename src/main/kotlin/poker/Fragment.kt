package poker

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.stream.createHTML


private const val TITLE = "Planning Poker"

fun HTML.header() {
    head {
        title { +TITLE }
        htmxScript()
        metas()
        styles()
    }
}

fun document(): String {
    return createHTMLDocument().html {
        lang = "en"
        header()
        body {
            id = "body"
            h1 { +TITLE }
            section {
                id = "poker"
                gameId()
            }
        }
    }.serialize()
}

private fun SECTION.gameId() {
    section {
        id = "gameId-input"
        form {
            hxPost("/game")
            hxTrigger("submit")
            hxTarget("#poker")
            hxSwap("innerHTML")
            hxReplaceUrl(true)

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


fun loginFragment( gameId:String): String {
    return createHTML().section {
        id = "user"
        form {
            hxPost("/${gameId}/user")
            hxTrigger("submit")
            hxTarget("#poker")
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

fun mainPage(gameId: String, userName: String): String {
    return createHTML().section {
        div {
            classes = setOf("top-right")
            p {
                +userName
            }
        }
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
                hxTarget("#game")
                hxSwap("outerHTML")
                +"Show Cards"
            }
            button {
                id = "reset"
                hxPost("/${gameId}/reset?userName=${userName}")
                hxTrigger("click")
                hxTarget("#game")
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