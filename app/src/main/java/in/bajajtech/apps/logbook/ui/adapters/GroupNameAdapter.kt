package `in`.bajajtech.apps.logbook.ui.adapters

import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.models.GroupModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class GroupNameAdapter(
    ctx: Context,
    res: Int,
    private val groupList: List<GroupModel>
) : ArrayAdapter<GroupModel>(ctx, res, groupList.toTypedArray()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return groupNameView(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return groupNameView(position)
    }

    private fun groupNameView(position: Int): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.spinner_item_groupname, null)
        val groupName = rowView.findViewById<TextView>(R.id.text_spinner_group_name)
        groupName.text = groupList[position].getGroupName()
        return rowView
    }
}