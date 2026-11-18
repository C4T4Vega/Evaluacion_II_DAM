package com.dinamite.lexlector.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ApunteDao {
    @Query("SELECT * FROM apuntes")
    suspend fun getAll(): List<Apunte>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apunte: Apunte)

    @Update
    suspend fun update(apunte: Apunte)

    @Query("DELETE FROM apuntes WHERE id = :id")
    suspend fun deleteById(id: Int)
}