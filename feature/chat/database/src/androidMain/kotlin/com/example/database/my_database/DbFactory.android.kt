package com.example.database.my_database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DbFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<MyDataBase> {
        val dbFile = context.applicationContext.getDatabasePath(MyDataBase.DATABASE_NAME)
        return Room.databaseBuilder(
            context.applicationContext,
            dbFile.absolutePath
        )
    }
}