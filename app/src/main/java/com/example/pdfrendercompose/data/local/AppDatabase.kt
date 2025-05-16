package com.example.pdfrendercompose.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DeviceEntity::class,UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun userDao(): UserEntityDao
}