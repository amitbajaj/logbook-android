package `in`.bajajtech.apps.utils

import `in`.bajajtech.apps.logbook.Constants
import java.util.*

object CurrencyHelper {
    fun formatAmount(amt: Double, currency: Int, symbolOnLeft: Boolean=true) = when(currency){
        Constants.Currencies.INR-> formatAmountAsINR(amt,symbolOnLeft)
        Constants.Currencies.USD-> formatAmountAsUSD(amt,symbolOnLeft)
        Constants.Currencies.AED-> formatAmountAsAED(amt,symbolOnLeft)
        else-> "Invalid Currency"
    }

    fun roundAsDouble(num: Double, decimals: Int = 2): Double = "%.${decimals}f".format(num).toDouble()
    fun roundAsString(num: Double, decimals: Int = 2): String = "%.${decimals}f".format(num)

    private fun formatAmountAsINR(amt: Double, symbolOnLeft: Boolean): String{
        var retVal = ""
        val amtParts = roundAsString(amt,2).split(".")
        val wholePart = amtParts[0]
        var fractionPart = ""
        if(amtParts.size>1) fractionPart=amtParts[1]
        for((digitCounter, i) in (wholePart.length-1 downTo 0).withIndex()){
            if((digitCounter==3 || (digitCounter>3 && digitCounter%2==1)) && wholePart[i]!='-'){
                retVal = ",".plus(retVal)
            }
            retVal = wholePart[i].plus(retVal)
        }
        retVal = retVal.plus('.')
        for(i in 0..1){
            retVal = if(fractionPart.length>i){
                retVal.plus(fractionPart[i])
            }else {
                retVal.plus('0')
            }
        }
        return if(symbolOnLeft){
            "${Currency.getInstance("INR").symbol} $retVal"
        }else{
            "$retVal ${Currency.getInstance("INR").symbol}"
        }
    }

    private fun formatAmountAsUSD(amt: Double, symbolOnLeft: Boolean): String {
        var retVal = ""
        val amtParts = roundAsString(amt,2).split(".")
        val wholePart = amtParts[0]
        var fractionPart = ""
        if(amtParts.size>1) fractionPart=amtParts[1]
        for((digitCounter, i) in (wholePart.length-1 downTo 0).withIndex()){
            if(digitCounter>0 && digitCounter%3==0 && wholePart[i]!='-'){
                retVal = ",".plus(retVal)
            }
            retVal = wholePart[i].plus(retVal)
        }
        retVal = retVal.plus('.')
        for(i in 0..1){
            retVal = if(fractionPart.length>i){
                retVal.plus(fractionPart[i])
            }else {
                retVal.plus('0')
            }
        }
        return if(symbolOnLeft){
            "${Currency.getInstance("USD").symbol} $retVal"
        }else{
            "$retVal ${Currency.getInstance("USD").symbol}"
        }
    }

    private fun formatAmountAsAED(amt: Double,symbolOnLeft: Boolean): String{
        var retVal = ""
        val amtParts = roundAsString(amt,3).split(".")
        val wholePart = amtParts[0]
        var fractionPart = ""
        if(amtParts.size>1) fractionPart=amtParts[1]
        for((digitCounter, i) in (wholePart.length-1 downTo 0).withIndex()){
            if(digitCounter>0 && digitCounter%3==0 && wholePart[i]!='-'){
                retVal = ",".plus(retVal)
            }
            retVal = wholePart[i].plus(retVal)
        }
        retVal = retVal.plus('.')
        for(i in 0..2){
            retVal = if(fractionPart.length>i){
                retVal.plus(fractionPart[i])
            }else {
                retVal.plus('0')
            }
        }
        return if(symbolOnLeft){
            "${Currency.getInstance("AED").symbol} $retVal"
        }else{
            "$retVal ${Currency.getInstance("AED").symbol}"
        }
    }
}