package com.example.trelloclone.models

import android.os.Parcel
import android.os.Parcelable

data class Task(
    var title: String = "",
    var createdBy: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest){
        writeString(title)
        writeString(createdBy)
    }

    override fun describeContents()=0

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}