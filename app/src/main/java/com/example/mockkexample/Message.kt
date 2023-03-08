package com.example.mockkexample

import java.util.UUID

data class Message(
    var uuid: UUID = UUID.randomUUID(),
    val text: String,
    val code: Code
) {

    enum class Code {
        CODE_1,
        CODE_2
    }
}
