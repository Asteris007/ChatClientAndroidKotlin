package com.example.chatclientandroidkotlin

interface MessageListener {
    fun onMessageReceived(message: String)
}