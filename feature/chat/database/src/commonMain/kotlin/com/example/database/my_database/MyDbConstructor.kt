package com.example.database.my_database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MyDbConstructor : RoomDatabaseConstructor<MyDataBase> {
    override fun initialize(): MyDataBase

}