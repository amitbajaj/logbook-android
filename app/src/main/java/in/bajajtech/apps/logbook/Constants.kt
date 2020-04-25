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

    object ColorCodes{
        const val RED = Color.RED
        var GREEN = Color.rgb(0,102,51)
        var GRAY = Color.rgb(224,224,224)
    }

    object Currencies{
        const val INR = 1
        const val USD = 2
        const val AED = 3
    }

    object ActivityIds{
        const val PARTY_MAINTENANCE = 100
        const val ADD_PARTY = 101
        const val TRANSACTION_MAINTENANCE = 200
        const val ADD_TRANSACTION = 201
    }

    const val ACTIVITY_RESULT_KEY = "status"

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

}