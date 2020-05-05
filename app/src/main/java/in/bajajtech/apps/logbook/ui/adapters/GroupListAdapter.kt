package `in`.bajajtech.apps.logbook.ui.adapters

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.groupList.AddGroup
import `in`.bajajtech.apps.logbook.ui.models.GroupListViewModel
import `in`.bajajtech.apps.logbook.ui.models.GroupModel
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GroupListAdapter(ctx: Context, app: Application, parent: Fragment) :
    RecyclerView.Adapter<GroupListAdapter.GroupListViewHolder>() {
    class GroupListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.group_name)
        val editButton: ImageView = itemView.findViewById(R.id.img_edit_group)
        val partyList: RecyclerView = itemView.findViewById(R.id.group_parties)
    }

    private val mApp = app
    private val mInflater: LayoutInflater = LayoutInflater.from(ctx)
    private val mGroups: GroupListViewModel = GroupListViewModel(app)
    private val mParent = parent

    fun addGroup(mNewGroup: GroupModel, mNotifyChange: Boolean = false) {
        mGroups.groupList.add(mNewGroup)
        if (mNotifyChange) notifyDataSetChanged()
    }

    fun removeAllGroups() {
        mGroups.groupList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListViewHolder {
        val itemView: View = mInflater.inflate(R.layout.list_item_group, parent, false)
        return GroupListViewHolder(itemView)
    }

    override fun getItemCount(): Int = mGroups.groupList.size

    override fun onBindViewHolder(holder: GroupListViewHolder, position: Int) {
        if (mGroups.groupList.isNotEmpty()) {
            with(mGroups.groupList[position]) {
                holder.groupName.text = getGroupName()
                holder.groupName.setOnClickListener {
                    holder.partyList.visibility =
                        if (holder.partyList.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
                holder.editButton.setOnClickListener {
                    val intent = Intent(it.context, AddGroup::class.java)
                    intent.putExtra(Constants.GroupObject.ID, this.getGroupId())
                    intent.putExtra(Constants.GroupObject.NAME, this.getGroupName())
                    mParent.startActivityForResult(intent, Constants.ActivityIds.EDIT_GROUP)
                }

                val mPartyListAdapter =
                    PartyListAdapter(holder.partyList.context, mApp, mParent, false)
                parties.forEach { mPartyListAdapter.addParty(it) }
                holder.partyList.apply {
                    layoutManager = LinearLayoutManager(
                        holder.partyList.context,
                        RecyclerView.VERTICAL,
                        false
                    )
                    adapter = mPartyListAdapter
                    setRecycledViewPool(RecyclerView.RecycledViewPool())
                }
                holder.partyList.visibility = View.GONE
            }
        }
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Constants.ColorCodes.GRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}