package com.example.plantdoc.data.entities.user

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.plantdoc.data.network.PlantDocApiService
import javax.inject.Inject

interface UserRepository {
    suspend fun insert(user: User)
    suspend fun insertUsers(users: List<User>)
    suspend fun getAllUsers(): LiveData<List<User>>
    suspend fun getUser(email: String): User?
    suspend fun getUserById(userId: Int): User?
}

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: PlantDocApiService
) : UserRepository {
    override suspend fun insert(user: User) {
//        userDao.insert(user = user)
        try {
            val response = apiService.insertUser(user = user)
            Log.d("Insert User", "Success")
            Log.d("Insert User", response.body().toString())
            // Handle the user data
        } catch (e: Exception) {
            // Handle the error
            Log.d("Insert User", "Failure")
            e.printStackTrace()
        }
    }

    override suspend fun insertUsers(users: List<User>) {
        userDao.insertUsers(users = users)
    }

    override suspend fun getAllUsers(): LiveData<List<User>> =
        userDao.getAllUsers()

    override suspend fun getUser(email: String): User? =
        userDao.getUser(email = email)

    override suspend fun getUserById(userId: Int): User? =
        userDao.getUserById(userId = userId)
}