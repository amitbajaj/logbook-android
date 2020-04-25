package `in`.bajajtech.apps.logbook.ui.partyList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.utils.CurrencyHelper
import android.graphics.Color

class PartyModel {
    private var partyId: Int = -1
    private var partyName: String = ""
    private var partyINRBalance: Double = 0.0
    private var partyUSDBalance: Double = 0.0
    private var partyAEDBalance: Double = 0.0

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
}