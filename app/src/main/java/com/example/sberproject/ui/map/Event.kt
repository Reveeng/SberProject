package com.example.sberproject.ui.map

open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        if (hasBeenHandled)
            return null
        hasBeenHandled = true
        return content
    }

    fun peekContent(): T = content
}