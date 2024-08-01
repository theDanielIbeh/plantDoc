package com.example.plantdoctor.data.entities.history

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.plantdoctor.data.entities.plant.Plant
import com.google.gson.annotations.SerializedName
import javax.annotation.Nonnull

@Entity(tableName = "history", primaryKeys = ["user_id", "date"])
data class History(
    @ColumnInfo(name = "user_id")
    @Nonnull
    @SerializedName("user_id")
    var userId: Int,

    @ColumnInfo(name = "predicted_class_id")
    @SerializedName("predicted_class_id")
    var predictedClassId: Int,

    @ColumnInfo(name = "local_url")
    @SerializedName("local_url")
    var localUrl: String,

    @ColumnInfo(name = "remote_url")
    @SerializedName("remote_url")
    var remoteUrl: String,

    @ColumnInfo(name = "date")
    @SerializedName("date")
    @Nonnull
    var date: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
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

