package com.example.pdfrendercompose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface UserEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(user : UserEntity)
}