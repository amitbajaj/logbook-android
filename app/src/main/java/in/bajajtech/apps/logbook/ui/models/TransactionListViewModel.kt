package `in`.bajajtech.apps.logbook.ui.models

import `in`.bajajtech.apps.logbook.ui.models.TransactionModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class TransactionListViewModel(app: Application): AndroidViewModel(app) {
    val mTransactionList = mutableListOf<TransactionModel>()
}