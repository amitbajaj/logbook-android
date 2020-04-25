package `in`.bajajtech.apps.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object UIHelper {
    fun showAlert(ctx: Context, title: String, message: String){
        val builder = AlertDialog.Builder(ctx)
        with(builder){
            setTitle(title)
            setMessage(message)
            setPositiveButton("Ok"){ _: DialogInterface?, _: Int ->  }
            create().show()
        }
    }
}