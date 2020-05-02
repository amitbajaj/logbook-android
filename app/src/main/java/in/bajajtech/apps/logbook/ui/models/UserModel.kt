package `in`.bajajtech.apps.logbook.ui.models

import `in`.bajajtech.apps.logbook.Constants
import android.os.Parcel
import android.os.Parcelable

class UserModel() : Parcelable {
    private var id: Int=0
    private var userId: String = ""
    private var userName: String = ""
    private var accessId: Int = Constants.STAFF_ACCESS_ID

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        userId = parcel.readString()!!
        userName = parcel.readString()!!
        accessId = parcel.readInt()
    }

    fun getId(): Int = id
    fun getUserId(): String = userId
    fun getUserName(): String = userName
    fun getAccessId(): Int = accessId

    constructor(mId: Int, mUserId: String, mUserName: String, mAccessId: Int) : this() {
        id = mId
        userId=mUserId
        userName=mUserName
        accessId=mAccessId
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        with(dest!!){
            writeInt(id)
            writeString(userId)
            writeString(userName)
            writeInt(accessId)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}