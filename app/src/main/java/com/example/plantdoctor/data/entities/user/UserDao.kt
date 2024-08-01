package com.example.plantdoctor.data.entities.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Query(
        "SELECT * FROM user"
    )
    fun getAllUsers(): LiveData<List<User>>

    @Query(
        "SELECT * FROM user where email = :email"
    )
    fun getUser(email: String): User?

    @Query(
        "SELECT * FROM user where id = :userId"
    )
    suspend fun getUserById(userId: Int): User?
}