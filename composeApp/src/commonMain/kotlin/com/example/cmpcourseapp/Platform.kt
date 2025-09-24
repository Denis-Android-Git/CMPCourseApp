package com.example.cmpcourseapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform