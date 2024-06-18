package com.example.plantdoc.data.entities.history

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.plantdoc.data.entities.plant.Plant

@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    var userId: Int,

    @ColumnInfo(name = "predicted_class_id")
    var predictedClassId: Int,

    @ColumnInfo(name = "local_url")
    var localUrl: String,

    @ColumnInfo(name = "remote_url")
    var remoteUrl: String,

    @ColumnInfo(name = "date")
    var date: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(userId)
        parcel.writeInt(predictedClassId)
        parcel.writeString(localUrl)
        parcel.writeString(remoteUrl)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Plant> {
        override fun createFromParcel(parcel: Parcel): Plant {
            return Plant(parcel)
        }

        override fun newArray(size: Int): Array<Plant?> {
            return arrayOfNulls(size)
        }
    }
}

