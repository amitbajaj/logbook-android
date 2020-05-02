package `in`.bajajtech.apps.logbook.ui.users

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.adapters.UserListAdapter
import `in`.bajajtech.apps.logbook.ui.models.UserModel
import `in`.bajajtech.apps.utils.HTTPPostHelper
import `in`.bajajtech.apps.utils.JSONHelper
import `in`.bajajtech.apps.utils.PreferenceStore
import `in`.bajajtech.apps.utils.UIHelper
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.util.concurrent.CompletableFuture

class Users:Fragment() {
    private lateinit var preferenceStore: PreferenceStore
    private lateinit var adapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_users,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceStore= PreferenceStore(view.context)

        val recyclerView: RecyclerView = view.findViewById(R.id.viewUserList)
        adapter = UserListAdapter(
            this.context!!,
            activity?.application!!,
            this
        )
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(view.context)

        loadUsers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_users,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_user->{
                val intent = Intent(this.context, AddUser::class.java)
                intent.putExtra(Constants.SUB_ACTIVITY_KEY,Constants.ActivityIds.ADD_USER)
                startActivityForResult(intent,Constants.ActivityIds.ADD_USER)
            }
        }
        return true
    }

    private fun loadUsers(){
        showProgress(true)
        CompletableFuture.runAsync {
            val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
            val dataString = "mode=QRY"
            try{
                val result = HTTPPostHelper.doHTTPPost(Constants.UsersCodeURL, sessionId,dataString)
                if(result!=null){
                    if(result.second.isNotEmpty()){
                        val (status,dataObject) = JSONHelper.parseResponse(result.second,"list","code")
                        if(status){
                            try{
                                val dataArray = (dataObject as JSONArray)
                                var userModel: UserModel
                                var itemObject: JSONObject
                                adapter.clear()
                                dataArray.forEach {
                                    itemObject = it as JSONObject
                                    userModel = UserModel(
                                        itemObject["id"].toString().toInt(),
                                        itemObject["uid"].toString(),
                                        itemObject["uname"].toString(),
                                        itemObject["pid"].toString().toInt()
                                    )
                                    adapter.addUser(userModel)
                                }
                                activity?.runOnUiThread{ processMessage(true,"")}
                            }catch(ex: Exception){
                                activity?.runOnUiThread{ processMessage(false,getString(R.string.unable_to_process_data))}
                            }
                        }else{
                            activity?.runOnUiThread{ processMessage(false,getString(R.string.unable_to_read_from_server))}
                        }
                    }else{
                        activity?.runOnUiThread{ processMessage(false,getString(R.string.no_data_from_server))}
                    }
                }else{
                    activity?.runOnUiThread{ processMessage(false,getString(R.string.no_communication))}
                }
            }catch(ex: Exception){
                activity?.runOnUiThread{ processMessage(false,getString(R.string.no_download))}
            }
        }
    }

    private fun processMessage(status: Boolean, message: String){
        val adapter = view!!.findViewById<RecyclerView>(R.id.viewUserList).adapter as UserListAdapter

        if(status){
            adapter.notifyDataSetChanged()
        }else{
            UIHelper.showAlert(this.context!!,getString(R.string.menu_user_maintenance),message)
        }
        showProgress(false)
    }
    private fun showProgress(visibility: Boolean){
        view!!.findViewById<ProgressBar>(R.id.userProgressBar).visibility = if(visibility) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Constants.ActivityIds.ADD_USER->{
                if(resultCode == Activity.RESULT_OK){
                    val status = data!!.getBooleanExtra(Constants.ACTIVITY_RESULT_KEY,false)
                    if(status){
                        UIHelper.showAlert(view!!.context,getString(R.string.add_user),getString(R.string.message_user_saved))
                        loadUsers()
                    }else{
                        UIHelper.showAlert(view!!.context,getString(R.string.add_user),getString(R.string.message_user_not_saved))
                    }
                }
            }
            Constants.ActivityIds.EDIT_USER->{
                if(resultCode == Activity.RESULT_OK){
                    val status = data!!.getBooleanExtra(Constants.ACTIVITY_RESULT_KEY,false)
                    if(status){
                        UIHelper.showAlert(view!!.context,getString(R.string.edit_user),getString(R.string.message_user_updated))
                        loadUsers()
                    }else{
                        UIHelper.showAlert(view!!.context,getString(R.string.edit_user),getString(R.string.message_user_not_updated))
                    }
                }
            }

        }
    }
}