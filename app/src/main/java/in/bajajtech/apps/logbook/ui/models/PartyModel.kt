package `in`.bajajtech.apps.logbook.ui.models

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.utils.CurrencyHelper
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable

class PartyModel() : Parcelable {
    private var partyId: Int = -1
    private var partyName: String = ""
    private var partyINRBalance: Double = 0.0
    private var partyUSDBalance: Double = 0.0
    private var partyAEDBalance: Double = 0.0
    private var checked: Boolean = false

    constructor(parcel: Parcel) : this() {
        partyId = parcel.readInt()
        partyName = parcel.readString()!!
        partyINRBalance = parcel.readDouble()
        partyUSDBalance = parcel.readDouble()
        partyAEDBalance = parcel.readDouble()
        checked = parcel.readByte() != 0.toByte()
    }

    fun setPartyData(id: Int, name: String, inrBalance: Double, usdBalance: Double, aedBalance: Double){
        partyId = id
        partyName=name
        partyINRBalance=inrBalance
        partyUSDBalance=usdBalance
        partyAEDBalance=aedBalance
    }

    fun getPartyId(): Int = this.partyId
    fun getPartyName(): String = this.partyName
    fun getBalance(currency: Int): Double = when(currency){
        Constants.Currencies.INR->this.partyINRBalance
        Constants.Currencies.USD->this.partyUSDBalance
        Constants.Currencies.AED->this.partyAEDBalance
        else -> 0.0
    }

    fun getFormattedBalance(currency: Int) = when(currency){
        Constants.Currencies.INR -> CurrencyHelper.formatAmount(this.partyINRBalance,Constants.Currencies.INR)
        Constants.Currencies.USD -> CurrencyHelper.formatAmount(this.partyUSDBalance,Constants.Currencies.USD)
        Constants.Currencies.AED -> CurrencyHelper.formatAmount(this.partyAEDBalance,Constants.Currencies.AED)
        else->"Invalid Currency"
    }

    fun getCurrencyColorCode(currency: Int): Int = when(currency){
        Constants.Currencies.INR->if(partyINRBalance<0){
            Constants.ColorCodes.RED
        }else{
            Constants.ColorCodes.GREEN
        }

        Constants.Currencies.USD->if(partyUSDBalance<0){
            Constants.ColorCodes.RED
        }else{
            Constants.ColorCodes.GREEN
        }

        Constants.Currencies.AED->if(partyAEDBalance<0){
            Constants.ColorCodes.RED
        }else{
            Constants.ColorCodes.GREEN
        }

        else-> Color.BLACK
    }

    fun setChecked(status: Boolean) {
        checked = status
    }

    fun isChecked(): Boolean = checked

    override fun toString(): String {
        return partyName
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(partyId)
        parcel.writeString(partyName)
        parcel.writeDouble(partyINRBalance)
        parcel.writeDouble(partyUSDBalance)
        parcel.writeDouble(partyAEDBalance)
        parcel.writeByte(if (checked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PartyModel> {
        override fun createFromParcel(parcel: Parcel): PartyModel {
            return PartyModel(parcel)
        }

        override fun newArray(size: Int): Array<PartyModel?> {
            return arrayOfNulls(size)
        }
    }


}