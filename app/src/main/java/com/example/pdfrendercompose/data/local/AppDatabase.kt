package com.example.pdfrendercompose.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DeviceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}