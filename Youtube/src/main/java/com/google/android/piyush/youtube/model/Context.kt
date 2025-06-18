package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Context(
    val client : Client
) {
    @Serializable
    data class Client(
        val clientName : String,
        val clientVersion : String
    )

    companion object{
        val WEB = Context(
            client = Client(
                clientName = "WEB",
                clientVersion = "2.20250219.07.00"
            )
        )

        val HTML5 = Context(
            client = Client(
                clientName = "TVHTML5_SIMPLY_EMBEDDED_PLAYER",
                clientVersion = "2.0"
            )
        )
    }
}