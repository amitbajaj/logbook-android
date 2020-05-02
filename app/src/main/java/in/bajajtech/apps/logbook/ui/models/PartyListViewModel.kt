package `in`.bajajtech.apps.logbook.ui.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class PartyListViewModel(application: Application) : AndroidViewModel(application) {
    val partyList = mutableListOf<PartyModel>()
}
