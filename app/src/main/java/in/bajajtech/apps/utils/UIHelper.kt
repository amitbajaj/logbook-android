package `in`.bajajtech.apps.utils

import android.content.Context
import android.content.DialogInterface
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService

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

    fun hideKeyboard(ctx: Context, token: IBinder){
        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(token,0)

    }

    fun showConfirmationDialog(
        ctx: Context, title: String, message: String,
        positiveText: String, positiveHandler:()->Unit,
        negativeText: String, negativeHandler:()->Unit
    ){
        val builder = AlertDialog.Builder(ctx)
        with(builder){
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveText){ _: DialogInterface?, _: Int -> positiveHandler() }
            setNegativeButton(negativeText){ _: DialogInterface?, _: Int -> negativeHandler() }
            create().show()
        }
    }
}