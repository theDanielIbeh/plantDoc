package com.example.plantdoc.data.entities.user

import androidx.lifecycle.LiveData
import javax.inject.Inject

interface UserRepository {
    suspend fun insert(user: User)
    suspend fun insertUsers(users: List<User>)
    suspend fun getAllUsers(): LiveData<List<User>>
    suspend fun getUser(email: String): User?
    suspend fun getUserById(userId: Int): User?
}

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun insert(user: User) {
        userDao.insert(user = user)
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