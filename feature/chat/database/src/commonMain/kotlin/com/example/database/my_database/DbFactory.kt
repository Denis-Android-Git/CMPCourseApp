package com.example.database.my_database

import androidx.room.RoomDatabase

expect class DbFactory {
    fun create(): RoomDatabase.Builder<MyDataBase>
}