package com.example.plantdoctor.data.entities.user

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.plantdoctor.data.PlantDocPreferencesRepository
import com.example.plantdoctor.data.network.PlantDocApiService
import com.example.plantdoctor.utils.Constants
import javax.inject.Inject

interface UserRepository {
    suspend fun insert(user: User)
    suspend fun login(email: String, password: String)
    suspend fun insertUsers(users: List<User>)
    suspend fun getAllUsers(): LiveData<List<User>>
    suspend fun getUser(email: String): User?
    suspend fun getUserById(userId: Int): User?
}

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: PlantDocApiService,
    private val preferencesRepository: PlantDocPreferencesRepository
) : UserRepository {
    override suspend fun insert(user: User) {
        val response = apiService.insertUser(user = user)
        Log.d("Initial", response.body().toString())
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                Log.d("Insert User", "Success")
                Log.d("Insert User", responseBody.toString())
                // Handle the user data
            } ?: run {
                throw Exception("Response body is null")
            }
        } else {
            when (response.code()) {
                409 -> {
                    // Email already exists
                    throw Exception("Email already exists")
                }

                else -> {
                    throw Exception("Unsuccessful response: ${response.code()} - ${response.message()}")
                }
            }
        }
    }

    override suspend fun login(email: String, password: String) {
        val response = apiService.login(email, password)
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                Log.d("Login", "Success: ${responseBody.message}")
                val user = responseBody.data.first()
                savePreferences(user)
            } ?: run {
                throw Exception("Response body is null")
            }
        } else {
            when (response.code()) {
                401 -> {
                    // Wrong password
                    throw Exception("Wrong password")
                }
                404 -> {
                    // Unregistered email
                    throw Exception("Unregistered email")
                }
                else -> {
                    throw Exception("Unsuccessful response: ${response.code()} - ${response.message()}")
                }
            }
        }
    }

    private suspend fun savePreferences(it: User) {
        preferencesRepository.savePreference(Constants.USER, it)
        preferencesRepository.savePreference(Constants.IS_LOGGED_IN, true)
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