package `in`.bajajtech.apps.logbook.ui.adapters

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.models.UserListViewModel
import `in`.bajajtech.apps.logbook.ui.models.UserModel
import `in`.bajajtech.apps.logbook.ui.users.AddUser
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
import androidx.recyclerview.widget.RecyclerView

class UserListAdapter(ctx: Context, app: Application, parent: Fragment) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userId: TextView = itemView.findViewById(R.id.usr_userid)
        val userName: TextView = itemView.findViewById(R.id.usr_username)
        val userProfile: TextView = itemView.findViewById(R.id.usr_profile)
        val editButton: ImageView = itemView.findViewById(R.id.usr_edit_user)
    }

    private val mInflater = LayoutInflater.from(ctx)
    private val mUsers = UserListViewModel(app)
    private val mParent = parent

    fun addUser(mNewUser: UserModel, mNotifyChange: Boolean=false){
        mUsers.userList.add(mNewUser)
        if(mNotifyChange) notifyDataSetChanged()
    }

    fun clear(){
        mUsers.userList.clear()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserListAdapter.UserViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_user,parent,false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int = mUsers.userList.size

    override fun onBindViewHolder(holder: UserListAdapter.UserViewHolder, position: Int) {
        with(mUsers.userList[position]){
            holder.userId.text = getUserId()
            holder.userName.text = getUserName()
            holder.userProfile.text = when(getAccessId()){
                Constants.ADMIN_ACCESS_ID->Constants.ADMIN_ACCESS_DESCRIPTION
                Constants.STAFF_ACCESS_ID->Constants.STAFF_ACCESS_DESCRIPTION
                else->Constants.INVALID_DESCRIPTION
            }
            holder.editButton.setOnClickListener {
                val intent = Intent(it.context, AddUser::class.java)
                intent.putExtra(Constants.USER_ID, this)
                intent.putExtra(Constants.SUB_ACTIVITY_KEY, Constants.ActivityIds.EDIT_USER)
                mParent.startActivityForResult(intent, Constants.ActivityIds.EDIT_USER)
            }
            if(position%2==0){
                holder.itemView.setBackgroundColor(Constants.ColorCodes.GRAY)
            }else{
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}

