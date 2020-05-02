package `in`.bajajtech.apps.logbook.ui.controls

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.models.PartyModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

class PartyNameAdapter(private val ctx: Context, private val res: Int, private val partyList: List<PartyModel>, private val multiSelect: Boolean, private val holder: Spinner):ArrayAdapter<PartyModel>(ctx,res,partyList.toTypedArray()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return partyNameView(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return partyNameCheckBoxView(position)
    }

    private fun partyNameView(position: Int): View{
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.spinner_item_partyname, null)
        val partyName = rowView.findViewById<TextView>(R.id.text_spinner_party_name)
        if(multiSelect){
            holder.setSelection(-1)
            partyName.text=context.getString(R.string.multi_select_title)
        }else{
            partyName.text=partyList[position].getPartyName()
        }
        return rowView
    }

    private fun partyNameCheckBoxView(position: Int):View{
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View
        if(multiSelect){
            rowView = inflater.inflate(R.layout.spinner_item_partyname, null)
            rowView.tag = position
            val partyName = rowView.findViewById<TextView>(R.id.text_spinner_party_name)
            val currentPartyObject = partyList[position]
            partyName.text = currentPartyObject.getPartyName()
            if(currentPartyObject.isChecked()){
                partyName.text=" ".plus(Constants.Strings.TICK_MARK).plus(" ").plus(currentPartyObject.getPartyName())
            }else{
                partyName.text=" ".plus(Constants.Strings.EMPTY_BOX).plus(" ").plus(currentPartyObject.getPartyName())
            }
            rowView.setOnClickListener{
                this.holder.performClick()
                val partyObject = partyList[it.tag as Int]
                partyObject.setChecked(!partyObject.isChecked())
                notifyDataSetChanged()
            }
        }else{
            rowView = inflater.inflate(R.layout.spinner_item_partyname, null)
            rowView.tag = position
            val partyName = rowView.findViewById<TextView>(R.id.text_spinner_party_name)
            val currentPartyObject = partyList[position]
            partyName.text = currentPartyObject.getPartyName()
        }
        return rowView
    }

    fun getSelectedParties(): List<Int>{
        val selectedPartyList = mutableListOf<Int>()
        partyList.forEach {
            val partyObject = it
            if(partyObject.isChecked()){
                selectedPartyList.add(partyObject.getPartyId())
            }
        }
        return selectedPartyList
    }
}