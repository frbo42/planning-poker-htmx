package poker.infrastructure.ui

import kotlinx.html.HTMLTag

const val BASE_URL = "/poker"

fun HTMLTag.hxGet(value: String) {
    attributes["hx-get"] = "${BASE_URL}${value}"
}

fun HTMLTag.hxPost(value: String) {
    attributes["hx-post"] = "${BASE_URL}${value}"
}

fun HTMLTag.hxTrigger(value: String) {
    attributes["hx-trigger"] = value
}

fun HTMLTag.hxTarget(value: String) {
    attributes["hx-target"] = value
}

fun HTMLTag.hxTargetId(value: String) {
    attributes["hx-target"] = "#$value"
}

fun HTMLTag.hxSwap(value: String) {
    attributes["hx-swap"] = value
}

fun HTMLTag.hxReplaceUrl(value: Boolean) {
    attributes["hx-replace-url"] = value.toString()
}

fun HTMLTag.hxInclude(value: String) {
    attributes["hx-include"] = value
}

fun HTMLTag.hxPushUrl(value: Boolean) {
    attributes["hx-push-url"] = value.toString()
}

fun HTMLTag.hxPushUrl(value: String) {
    attributes["hx-push-url"] = value
}