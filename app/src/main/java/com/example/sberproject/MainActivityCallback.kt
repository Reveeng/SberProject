package com.example.sberproject

interface MainActivityCallback {
    fun setActionBarTitle(title: String)
    fun login()
    fun logout()
}