package com.example.antihoroscope

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform