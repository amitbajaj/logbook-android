package `in`.bajajtech.apps.logbook.ui.transactionList

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class TransactionListViewModel(app: Application): AndroidViewModel(app) {
    val mTransactionList = mutableListOf<TransactionModel>()
}