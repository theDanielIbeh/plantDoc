package com.example.plantdoc.data.entities.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    val id: Int = 0,

    @ColumnInfo(name = "first_name")
    @SerializedName("first_name")
    @Expose
    var firstName: String,

    @ColumnInfo(name = "last_name")
    @SerializedName("last_name")
    @Expose
    var lastName: String,

    @ColumnInfo(name = "email")
    @SerializedName("email")
    @Expose
    var email: String,

    @ColumnInfo(name = "password")
    @SerializedName("password")
    @Expose
    var password: String
)
