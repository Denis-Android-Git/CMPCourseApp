package com.example.data.database

import androidx.sqlite.SQLiteException
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError

suspend inline fun <T> safeDbUpdate(update: suspend () -> T): CustomResult<T, DataError.Local> {
    return try {
        CustomResult.Success(update())
    } catch (_: SQLiteException) {
        CustomResult.Failure(DataError.Local.DISC_FULL)
    }
}