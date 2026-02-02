package com.example.database.my_database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DbFactory {
    actual fun create(): RoomDatabase.Builder<MyDataBase> {
        val dbFile = documentDir() + MyDataBase.DATABASE_NAME
        return Room.databaseBuilder(
            dbFile
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDir(): String {
        val docDir = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(docDir?.path)
    }
}