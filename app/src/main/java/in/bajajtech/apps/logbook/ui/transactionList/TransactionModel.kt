package `in`.bajajtech.apps.logbook.ui.transactionList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.utils.CurrencyHelper
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateFormat
import kotlinx.android.parcel.Parcelize
import java.text.NumberFormat
import java.util.*

class TransactionModel : Parcelable {
    private var transactionId: Int = -1
    private var partyName: String = ""
    private var partyId: Int = 0
    private var transactionDate: Date = Date()
    private var inrAmount: Double = 0.0
    private var usdAmount: Double = 0.0
    private var aedAmount: Double = 0.0
    private var exchangeRate: Double = 0.0
    private var exchangeDirection: Int = 0
    private var exchangeCurrency: Int = 0
    private var comments: String = ""
    private var transactionType: Int = 0

    fun setTransactionData(
        mTiD: Int, mPartyName: String, mPtyId: Int,mTDate: Date, mINRAmt: Double, mUSDAmt: Double, mAEDAmt: Double,
        mExchangeRate: Double, mExchangeDir: Int, mExchangeCurrency: Int, mComments: String, mTxnType: Int){
        transactionId = mTiD
        partyName = mPartyName
        partyId  = mPtyId
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
        mTiD: Int, mPartyName: String, mPtyId: Int, mTDate: Date, mINRAmt: Double, mUSDAmt: Double, mAEDAmt: Double,
        mExchangeRate: Double, mExchangeDir: Int, mExchangeCurrency: Int, mComments: String, mTxnType: String){
        transactionId = mTiD
        partyName = mPartyName
        partyId = mPtyId
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
    fun getPartyId(): Int = this.partyId
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
    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest){
            writeInt(this@TransactionModel.transactionId)
            writeString(this@TransactionModel.partyName)
            writeInt(this@TransactionModel.partyId)
            writeString(this@TransactionModel.transactionDate.toString())
            writeDouble(this@TransactionModel.inrAmount)
            writeDouble(this@TransactionModel.usdAmount)
            writeDouble(this@TransactionModel.aedAmount)
            writeDouble(this@TransactionModel.exchangeRate)
            writeInt(this@TransactionModel.exchangeDirection)
            writeInt(this@TransactionModel.exchangeCurrency)
            writeString(this@TransactionModel.comments)
            writeInt(this@TransactionModel.transactionType)
        }

    }

    override fun describeContents(): Int = Parcelable.CONTENTS_FILE_DESCRIPTOR

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<TransactionModel> {
            override fun createFromParcel(source: Parcel): TransactionModel {
                with(source){
                    val mTxnId = readInt()
                    val mPtyName = readString()
                    val mPtyId = readInt()
                    val dtString = readString()
                    val mTxnDate = try{
                        java.sql.Date.valueOf(dtString)
                    }catch(ex: Exception){
                        Date()
                    }
                    val mInrAmt = readDouble()
                    val mUsdAmt = readDouble()
                    val mAedAmt = readDouble()
                    val mExhRate = readDouble()
                    val mExhDir = readInt()
                    val mExhCur = readInt()
                    val mCmt = readString()
                    val mType = readInt()
                    val txnModel = TransactionModel()
                    txnModel.setTransactionData(mTxnId,mPtyName!!,mPtyId,mTxnDate,mInrAmt,mUsdAmt,mAedAmt,mExhRate,mExhDir,mExhCur,mCmt!!,mType)
                    return txnModel
                }
            }
            override fun newArray(size: Int) = arrayOfNulls<TransactionModel>(size)
        }
    }
}