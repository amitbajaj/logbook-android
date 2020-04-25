package `in`.bajajtech.apps.logbook.ui.transactionList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.utils.CurrencyHelper
import android.text.format.DateFormat
import java.text.NumberFormat
import java.util.*

class TransactionModel {
    private var transactionId: Int = -1
    private var partyName: String = ""
    private var transactionDate: Date = Date()
    private var inrAmount: Double = 0.0
    private var usdAmount: Double = 0.0
    private var aedAmount: Double = 0.0
    private var exchangeRate: Double = 0.0
    private var exchangeDirection: Int = 0
    private var exchangeCurrency: Int = 0
    private var comments: String = ""
    private var transactionType: Int = 0
    private val nf = NumberFormat.getNumberInstance()

    fun setTransactionData(
        mTiD: Int, mPartyName: String, mTDate: Date, mINRAmt: Double, mUSDAmt: Double, mAEDAmt: Double,
        mExchangeRate: Double, mExchangeDir: Int, mExchangeCurrency: Int, mComments: String, mTxnType: Int){
        transactionId = mTiD
        partyName = mPartyName
        transactionDate = mTDate
        inrAmount = mINRAmt
        usdAmount = mUSDAmt
        aedAmount = mAEDAmt
        exchangeRate = mExchangeRate
        exchangeDirection = mExchangeDir
        exchangeCurrency = mExchangeCurrency
        comments = mComments
        transactionType = mTxnType
    }

    fun setTransactionData(
        mTiD: Int, mPartyName: String, mTDate: Date, mINRAmt: Double, mUSDAmt: Double, mAEDAmt: Double,
        mExchangeRate: Double, mExchangeDir: Int, mExchangeCurrency: Int, mComments: String, mTxnType: String){
        transactionId = mTiD
        partyName = mPartyName
        transactionDate = mTDate
        inrAmount = mINRAmt
        usdAmount = mUSDAmt
        aedAmount = mAEDAmt
        exchangeRate = mExchangeRate
        exchangeDirection = mExchangeDir
        exchangeCurrency = mExchangeCurrency
        comments = mComments
        transactionType = when(mTxnType){
            Constants.TransactionTypes.CURRENCY_TRANSFER_DESCRIPTION -> Constants.TransactionTypes.CURRENCY_TRANSFER
            Constants.TransactionTypes.DIRECT_DESCRIPTION -> Constants.TransactionTypes.DIRECT
            Constants.TransactionTypes.PARTY_TRANSFER_DESCRIPTION -> Constants.TransactionTypes.PARTY_TRANSFER
            else->0
        }
    }

    fun getTransactionId(): Int = this.transactionId
    fun getPartyName(): String = this.partyName
    fun getTransactionDate(): Date = this.transactionDate
    fun getTransactionDateText(): String = DateFormat.format("dd-MMM-yyyy", this.transactionDate).toString()

    fun getFormattedAmount(currency: Int): String = when(currency){
        Constants.Currencies.INR -> if(this.inrAmount==0.0) "" else CurrencyHelper.formatAmount(this.inrAmount, Constants.Currencies.INR)
        Constants.Currencies.USD -> if(this.usdAmount==0.0) "" else CurrencyHelper.formatAmount(this.usdAmount, Constants.Currencies.USD)
        Constants.Currencies.AED -> if(this.aedAmount==0.0) "" else CurrencyHelper.formatAmount(this.aedAmount, Constants.Currencies.AED)
        else-> "Invalid Currency"
    }
    fun getAmount(currency: Int): Double = when(currency){
        Constants.Currencies.INR -> this.inrAmount
        Constants.Currencies.USD -> this.usdAmount
        Constants.Currencies.AED -> this.aedAmount
        else-> 0.0
    }
    fun getExchangeRate(): Double = this.exchangeRate
    fun getExchangeRateText(): String {
        return CurrencyHelper.formatAmount(this.exchangeRate, exchangeCurrency)
    }
    fun getExchangeDirection(): Int = this.exchangeDirection
    fun getExchangeDirectionText(): String = when(this.exchangeDirection){
        Constants.ExchangeDirections.FORWARD->Constants.ExchangeDirections.FORWARD_DESCRIPTION
        Constants.ExchangeDirections.REVERSE->Constants.ExchangeDirections.REVERSE_DESCRIPTION
        else->"Unknown Direction"
    }
    fun getTransactionType(): Int = this.transactionType
    fun getTransactionTypeText(): String = when(this.transactionType){
        Constants.TransactionTypes.DIRECT -> Constants.TransactionTypes.DIRECT_DESCRIPTION
        Constants.TransactionTypes.CURRENCY_TRANSFER -> Constants.TransactionTypes.CURRENCY_TRANSFER_DESCRIPTION
        Constants.TransactionTypes.PARTY_TRANSFER -> Constants.TransactionTypes.PARTY_TRANSFER_DESCRIPTION
        else->"Unknown Transaction Type"

    }
    fun getComments(): String = this.comments
}