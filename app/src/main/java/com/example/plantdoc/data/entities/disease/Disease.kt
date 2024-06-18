package com.example.plantdoc.data.entities.disease

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disease")
data class Disease(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "class_index")
    var classIndex: Int,

    @ColumnInfo(name = "plant_id")
    var plantId: Int,

    @ColumnInfo(name = "botanical_name")
    var botanicalName: String?,

    @ColumnInfo(name = "symptoms")
    var symptoms: String?,

    @ColumnInfo(name = "cause")
    var cause: String?,

    @ColumnInfo(name = "propagation")
    var propagation: String?,

    @ColumnInfo(name = "control")
    var control: String?,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(classIndex)
        parcel.writeInt(plantId)
        parcel.writeString(botanicalName)
        parcel.writeString(symptoms)
        parcel.writeString(cause)
        parcel.writeString(propagation)
        parcel.writeString(control)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Disease> {
        override fun createFromParcel(parcel: Parcel): Disease {
            return Disease(parcel)
        }

        override fun newArray(size: Int): Array<Disease?> {
            return arrayOfNulls(size)
        }
    }
}
