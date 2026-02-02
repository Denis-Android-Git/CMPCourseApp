package com.example.database.my_database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.ChatDao
import com.example.database.dao.ChatMessageDao
import com.example.database.dao.ChatParticipantCrossRefDao
import com.example.database.dao.ChatParticipantDao
import com.example.database.entities.ChatEntity
import com.example.database.entities.ChatMessageEntity
import com.example.database.entities.ChatParticipantCrossRef
import com.example.database.entities.ChatParticipantEntity
import com.example.database.view.LastMessageView

@Database(
    entities = [
        ChatEntity::class,
        ChatParticipantEntity::class,
        ChatMessageEntity::class,
        ChatParticipantCrossRef::class,
    ],
    views = [
        LastMessageView::class
    ],
    version = 1
)
@ConstructedBy(MyDbConstructor::class)
abstract class MyDataBase : RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantCrossRefDao: ChatParticipantCrossRefDao

    companion object {
        const val DATABASE_NAME = "my_database.db"
    }
}