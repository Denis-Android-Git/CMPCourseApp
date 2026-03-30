package com.example.domain.util

class DataErrorException(
    val error: DataError
) : Exception()