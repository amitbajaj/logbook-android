package `in`.bajajtech.apps.logbook.ui.models

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.utils.CurrencyHelper
import android.graphics.Color
import android.provider.SyncStateContract

class ReportModel {
    private var mPartyName: String = ""
    private var mPartyId: Int = 0
    private var mOpenBalanceINR = 0.0
    private var mOpenBalanceUSD = 0.0
    private var mOpenBalanceAED = 0.0

    private var mCloseBalanceINR = 0.0
    private var mCloseBalanceUSD = 0.0
    private var mCloseBalanceAED = 0.0

    var transactions = mutableListOf<TransactionModel>()

    constructor(pname: String, pid: Int, oinr: Double, cinr: Double, ousd: Double, cusd: Double, oaed: Double, caed: Double){
        mPartyName=pname
        mPartyId=pid
        mOpenBalanceINR=oinr
        mOpenBalanceUSD=ousd
        mOpenBalanceAED=oaed
        mCloseBalanceINR=cinr
        mCloseBalanceUSD=cusd
        mCloseBalanceAED=caed
    }

    fun getPartyName(): String = mPartyName
    fun setPartyName(partyName: String) {mPartyName=partyName}

    fun getPartyId(): Int = mPartyId
    fun setPartyId(partyId: Int) {mPartyId=partyId}

    fun getOpeningBalance(currency: Int, isFormatted: Boolean=true):String =
        if(isFormatted){
            when(currency){
                Constants.Currencies.INR -> CurrencyHelper.formatAmount(mOpenBalanceINR,Constants.Currencies.INR,false)
                Constants.Currencies.USD -> CurrencyHelper.formatAmount(mOpenBalanceUSD,Constants.Currencies.USD,false)
                Constants.Currencies.AED -> CurrencyHelper.formatAmount(mOpenBalanceAED,Constants.Currencies.AED,false)
                else->""
            }
        }else{
            when(currency){
                Constants.Currencies.INR -> CurrencyHelper.roundAsString(mOpenBalanceINR,2)
                Constants.Currencies.USD -> CurrencyHelper.roundAsString(mOpenBalanceUSD,2)
                Constants.Currencies.AED -> CurrencyHelper.roundAsString(mOpenBalanceAED,3)
                else->"0"
            }
        }

    fun getClosingBalance(currency: Int, isFormatted: Boolean=true):String =
        if(isFormatted){
            when(currency){
                Constants.Currencies.INR -> CurrencyHelper.formatAmount(mCloseBalanceINR,Constants.Currencies.INR,true)
                Constants.Currencies.USD -> CurrencyHelper.formatAmount(mCloseBalanceUSD,Constants.Currencies.USD,true)
                Constants.Currencies.AED -> CurrencyHelper.formatAmount(mCloseBalanceAED,Constants.Currencies.AED,true)
                else->""
            }
        }else{
            when(currency){
                Constants.Currencies.INR -> CurrencyHelper.roundAsString(mCloseBalanceINR,2)
                Constants.Currencies.USD -> CurrencyHelper.roundAsString(mCloseBalanceUSD,2)
                Constants.Currencies.AED -> CurrencyHelper.roundAsString(mCloseBalanceAED,3)
                else->"0"
            }
        }

    fun getBalanceColorCode(balanceType: Int, currency: Int): Int = if(balanceType==Constants.OPENING_BALANCE) {
        when (currency) {
            Constants.Currencies.INR -> if (mOpenBalanceINR < 0) {
                Constants.ColorCodes.RED
            } else {
                Constants.ColorCodes.GREEN
            }

            Constants.Currencies.USD -> if (mOpenBalanceUSD < 0) {
                Constants.ColorCodes.RED
            } else {
                Constants.ColorCodes.GREEN
            }

            Constants.Currencies.AED -> if (mOpenBalanceAED < 0) {
                Constants.ColorCodes.RED
            } else {
                Constants.ColorCodes.GREEN
            }
            else -> Color.BLACK
        }
    }else{
        when (currency) {
            Constants.Currencies.INR -> if (mCloseBalanceINR < 0) {
                Constants.ColorCodes.RED
            } else {
                Constants.ColorCodes.GREEN
            }

            Constants.Currencies.USD -> if (mCloseBalanceUSD < 0) {
                Constants.ColorCodes.RED
            } else {
                Constants.ColorCodes.GREEN
            }

            Constants.Currencies.AED -> if (mCloseBalanceAED < 0) {
                Constants.ColorCodes.RED
            } else {
                Constants.ColorCodes.GREEN
            }
            else -> Color.BLACK
        }
    }
}