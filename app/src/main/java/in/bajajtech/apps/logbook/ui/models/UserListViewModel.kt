package `in`.bajajtech.apps.logbook.ui.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class UserListViewModel(application: Application) : AndroidViewModel(application) {
    val userList = mutableListOf<UserModel>()
}