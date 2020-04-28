package `in`.bajajtech.apps.logbook.ui.controls

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePicker(private var sourceButton: Button): DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val sourceDate = sourceButton.tag as DateObject?
        val year: Int
        val month: Int
        val day: Int
        if(sourceDate==null){
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }else{
            year = sourceDate.getYear()
            month = sourceDate.getMonth()
            day = sourceDate.getDay()
        }
        return DatePickerDialog(sourceButton.context,this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val dtObject = DateObject(dayOfMonth,month,year)
        sourceButton.tag = dtObject
        sourceButton.text=dtObject.getDate()
    }

}