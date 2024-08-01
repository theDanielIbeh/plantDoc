package com.example.plantdoctor.data.entities.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: Int = 0,

    @ColumnInfo(name = "first_name")
    @SerializedName("first_name")
    var firstName: String,

    @ColumnInfo(name = "last_name")
    @SerializedName("last_name")
    var lastName: String,

    @ColumnInfo(name = "email")
    @SerializedName("email")
    var email: String,

    @ColumnInfo(name = "password")
    @SerializedName("password")
    var password: String
)
