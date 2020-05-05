package `in`.bajajtech.apps.logbook.ui.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class GroupListViewModel(application: Application) : AndroidViewModel(application) {
    val groupList = mutableListOf<GroupModel>()
}