package `in`.bajajtech.apps.logbook.ui.controls

import androidx.core.text.isDigitsOnly
import java.util.*

class DateObject {
    private var day: Int = 1
    private var month: Int = 1
    private var year: Int = 1900

    constructor(mDay: Int, mMonth: Int, mYear: Int){
        year=mYear
        month=mMonth
        day=mDay
    }

    fun getYear() = year
    fun getMonth() = month
    fun getDay() = day

    fun setDate(mDay: Int, mMonth: Int, mYear: Int){
        day=mDay
        month=mMonth
        year=mYear
    }

    fun getDate():String =
        year.toString().padEnd(4,'0').plus('-').plus(month.toString().padStart(2,'0')).plus('-').plus(day.toString().padStart(2,'0'))

    fun setDate(dtString: String){
        val dtParts = dtString.split('-')
        if(dtParts.size==3){
            if(dtParts[0].isDigitsOnly() && dtParts[1].isDigitsOnly() && dtParts[2].isDigitsOnly()){
                year = dtParts[0].toInt()
                month = dtParts[1].toInt()
                day = dtParts[2].toInt()
            }
        }
    }
}