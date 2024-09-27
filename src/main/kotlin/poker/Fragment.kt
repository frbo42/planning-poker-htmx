package poker

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.stream.createHTML
import org.w3c.dom.Document


private const val TITLE = "Planning Poker"

fun HTML.header() {
    head {
        title { +TITLE }
        htmxScript()
        metas()
        styles()
    }
}

fun document(): Document {
    return createHTMLDocument().html {
        lang = "en"
        header()
        body {
            id = "body"
            h1 { +TITLE }
            login()
        }
    }
}

private fun BODY.login() {
    section {
        id = "user"
        form {
            hxPost("/user")
            hxTrigger("submit")
            hxTarget("#body")
            hxSwap("outerHTML")

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

fun mainPage(userName: String): String {
    return createHTML().body {
        h1 { +TITLE }
        div {
            classes = setOf("top-right")
            p {
                +userName
            }
        }
        section {
            id = "game"
            hxGet("/game?userName=${userName}")
            hxTrigger("load, every 2s")
            hxSwap("innerHTML")
        }
        section {
            id = "button-bar"
            button {
                id = "show"
                hxPost("/show?userName=${userName}")
                hxTrigger("click")
                hxTarget("#game")
                hxSwap("outerHTML")
                +"Show Cards"
            }
            button {
                id = "reset"
                hxPost("/reset?userName=${userName}")
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