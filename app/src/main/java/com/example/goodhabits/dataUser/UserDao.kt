package com.example.goodhabits.dataUser

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    // Inserta un usuario. Si el nombre de usuario ya existe, lo ignora.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    // Busca un usuario por su nombre de usuario.
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    // Actualiza un usuario existente.
    @Update
    suspend fun updateUser(user: User)
}
