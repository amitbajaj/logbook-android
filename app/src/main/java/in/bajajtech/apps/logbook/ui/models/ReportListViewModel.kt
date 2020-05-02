package `in`.bajajtech.apps.logbook.ui.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ReportListViewModel(application: Application) : AndroidViewModel(application) {
    val reportList = mutableListOf<ReportModel>()
}