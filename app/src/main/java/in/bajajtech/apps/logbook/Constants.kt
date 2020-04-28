package `in`.bajajtech.apps.logbook

import android.graphics.Color

object Constants {
    private const val BaseDomain = "dev.bajajtech.in"
    private const val BaseURL = "https://".plus(BaseDomain).plus("/logbook")
    const val LoginURL = BaseURL.plus("/code/login.php")
    const val PartiesCodeURL = BaseURL.plus("/code/actions/parties.php")
    const val TransactionsCodeURL = BaseURL.plus("/code/actions/txns.php")
    const val SessionCookieName = "PHPSESSID"
    const val PrefKeySessionId = "sessionId"

    const val PRIVATE_MODE=0
    const val STORE_NAME="MyPrefs"
    private const val INVALID_VALUE = -1

    object ColorCodes{
        const val RED = Color.RED
        var GREEN = Color.rgb(0,102,51)
        var GRAY = Color.rgb(224,224,224)
    }

    object Currencies{
        const val INR = 1
        const val INR_NAME="INR"
        const val USD = 2
        const val USD_NAME="USD"
        const val AED = 3
        const val AED_NAME="AED"
    }

    fun getCurrencyId(currencyCode: String): Int = when(currencyCode){
        Currencies.INR_NAME->Currencies.INR
        Currencies.USD_NAME->Currencies.USD
        Currencies.AED_NAME->Currencies.AED
        else-> INVALID_VALUE
    }

    fun getCurrencyName(currencyCode: Int): String = when(currencyCode){
        Currencies.INR->"INR"
        Currencies.USD->"USD"
        Currencies.AED->"AED"
        else->"Invalid"
    }

    object ActivityIds{
        const val PARTY_MAINTENANCE = 100
        const val ADD_PARTY = 101
        const val EDIT_PARTY = 102
        const val TRANSACTION_MAINTENANCE = 200
        const val ADD_DIRECT_TRANSACTION = 201
        const val ADD_MULTI_DIRECT_TRANSACTION = 202
        const val ADD_CURRENCY_TRANSACTION = 203
        const val ADD_PARTY_TRANSACTION = 204
        const val EDIT_TRANSACTION = 205
        const val EDIT_TRANSACTION_EDIT_ACTION = 2051
        const val EDIT_TRANSACTION_DELETE_ACTION = 2052
    }

    const val ACTIVITY_RESULT_KEY = "status"
    const val SUB_ACTIVITY_KEY = "action"
    const val TRANSACTION_ID = "txnid"
    const val PARTY_ID = "ptyId"

    object TransactionTypes{
        const val DIRECT = 1
        const val DIRECT_DESCRIPTION = "Direct"
        const val CURRENCY_TRANSFER = 2
        const val CURRENCY_TRANSFER_DESCRIPTION = "Currency Transfer"
        const val PARTY_TRANSFER = 3
        const val PARTY_TRANSFER_DESCRIPTION = "Party Transfer"

    }

    object ExchangeDirections{
        const val FORWARD = 1
        const val FORWARD_DESCRIPTION = "Forward"
        const val REVERSE = 2
        const val REVERSE_DESCRIPTION = "Reverse"
    }

    fun getExchangeDirection(direction: String): Int = when(direction.toLowerCase()){
        ExchangeDirections.FORWARD_DESCRIPTION.toLowerCase()->ExchangeDirections.FORWARD
        ExchangeDirections.REVERSE_DESCRIPTION.toLowerCase()->ExchangeDirections.REVERSE
        else-> INVALID_VALUE
    }

    object TransactionSubTypes{
        const val CREDIT = 1
        const val CREDIT_DESCRIPTION = "Credit"
        const val DEBIT = 2
        const val DEBIT_DESCRIPTION = "Debit"
    }

    fun getTransactionSubType(subType: String): Int= when(subType.toLowerCase()){
        TransactionSubTypes.CREDIT_DESCRIPTION.toLowerCase()->TransactionSubTypes.CREDIT
        TransactionSubTypes.DEBIT_DESCRIPTION.toLowerCase()->TransactionSubTypes.DEBIT
        else-> INVALID_VALUE
    }

    object Strings{
        private const val INT_TICK_MARK = 0X2611
        const val TICK_MARK = INT_TICK_MARK.toChar().toString()

        private const val INT_EMPTY_BOX = 0X2610
        const val EMPTY_BOX = INT_EMPTY_BOX.toChar().toString()
    }

}