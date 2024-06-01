package com.timur.mychat.model

import android.os.Parcel
import android.os.Parcelable

data class Users(
    val userid: String? = "",
    val status: String? = "",
    val imageUrl: String? = "",
    val username: String? = "",
    val nickname: String? = "",
    val useremail: String? = "",
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val user = other as Users

        return userid == user.userid
    }

    override fun hashCode(): Int {
        return userid.hashCode()
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userid)
        parcel.writeString(status)
        parcel.writeString(imageUrl)
        parcel.writeString(username)
        parcel.writeString(nickname)
        parcel.writeString(useremail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }

}