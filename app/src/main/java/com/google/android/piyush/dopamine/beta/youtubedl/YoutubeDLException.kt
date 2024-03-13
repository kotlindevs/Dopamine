package com.google.android.piyush.dopamine.beta.youtubedl

class YoutubeDLException : Exception {
    constructor(message: String?) : super(message) {}
    constructor(message: String?, e: Throwable?) : super(message, e) {}
    constructor(e: Throwable?) : super(e) {}
}