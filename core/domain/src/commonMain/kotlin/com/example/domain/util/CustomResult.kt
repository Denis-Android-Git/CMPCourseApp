package com.example.domain.util

sealed interface CustomResult<out D, out E : Error> {
    data class Success<out D>(val data: D) : CustomResult<D, Nothing>
    data class Failure<out E : Error>(val error: E) : CustomResult<Nothing, E>
}

inline fun <T, E : Error, R> CustomResult<T, E>.map(map: (T) -> R): CustomResult<R, E> {
    return when (this) {
        is CustomResult.Failure -> CustomResult.Failure(error)
        is CustomResult.Success -> CustomResult.Success(map(data))
    }
}

inline fun <T, E : Error> CustomResult<T, E>.onSuccess(action: (T) -> Unit): CustomResult<T, E> {
    return when (this) {
        is CustomResult.Failure -> this
        is CustomResult.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, E : Error> CustomResult<T, E>.onFailure(action: (E) -> Unit): CustomResult<T, E> {
    return when (this) {
        is CustomResult.Failure -> {
            action(error)
            this
        }

        is CustomResult.Success -> this
    }
}

fun <T, E : Error> CustomResult<T, E>.asEmptyResult(): EmptyResult<E> {
    return map { }
}
typealias EmptyResult<E> = CustomResult<Unit, E>