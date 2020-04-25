package `in`.bajajtech.apps.utils

import `in`.bajajtech.apps.logbook.Constants
import android.content.Context
import android.content.SharedPreferences

class PreferenceStore(ctx: Context) {
    private var sharedPreferences: SharedPreferences =
        ctx.getSharedPreferences(
            Constants.STORE_NAME,
            Constants.PRIVATE_MODE
        )

    fun setValue(key: String, value: String){
        val editor = sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun getValue(key:String): String{
        return sharedPreferences.getString(key,"") as String
    }
}