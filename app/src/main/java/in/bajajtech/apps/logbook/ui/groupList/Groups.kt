package `in`.bajajtech.apps.logbook.ui.groupList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.adapters.GroupListAdapter
import `in`.bajajtech.apps.logbook.ui.models.GroupModel
import `in`.bajajtech.apps.logbook.ui.models.PartyModel
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

class Groups : Fragment() {
    private lateinit var preferenceStore: PreferenceStore
    private lateinit var adapter: GroupListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceStore = PreferenceStore(view.context)

        val recyclerView: RecyclerView = view.findViewById(R.id.viewGroupList)
        adapter = GroupListAdapter(
            this.context!!,
            activity?.application!!,
            this
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        loadGroups()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_groups, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_group -> {
                val intent = Intent(this.context, AddGroup::class.java)
                startActivityForResult(intent, Constants.ActivityIds.ADD_GROUP)
            }
            else -> return false
        }
        return true
    }

    private fun loadGroups() {
        showProgress(true)
        CompletableFuture.runAsync {
            val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
            val dataString = "mode=QRYPTY"
            try {
                val result =
                    HTTPPostHelper.doHTTPPost(Constants.GroupsCodeURL, sessionId, dataString)
                if (result != null) {
                    println(result.second)
                    if (result.second.isNotEmpty()) {
                        val (status, dataObject) = JSONHelper.parseResponse(
                            result.second,
                            "list",
                            "code"
                        )
                        if (status) {
                            try {
                                val dataArray = (dataObject as JSONArray)
                                var groupModel: GroupModel
                                var partyModel: PartyModel
                                var itemObject: JSONObject
                                var childList: JSONArray
                                var childObject: JSONObject

                                adapter.removeAllGroups()
                                dataArray.forEach { groupObject ->
                                    itemObject = groupObject as JSONObject
                                    groupModel = GroupModel()
                                    groupModel.setGroupId(itemObject["gid"].toString().toInt())
                                    groupModel.setGroupName(itemObject["name"].toString())
                                    childList = itemObject["plist"] as JSONArray
                                    childList.forEach { partyObject ->
                                        childObject = partyObject as JSONObject
                                        partyModel = PartyModel()
                                        partyModel.setPartyData(
                                            childObject["pid"].toString().toInt(),
                                            childObject["pname"].toString(),
                                            childObject["inrbal"].toString().toDouble(),
                                            childObject["usdbal"].toString().toDouble(),
                                            childObject["aedbal"].toString().toDouble()
                                        )
                                        groupModel.parties.add(partyModel)
                                    }
                                    adapter.addGroup(groupModel, false)
                                }
                                activity?.runOnUiThread { processMessage(true, "") }
                            } catch (ex: Exception) {
                                println(ex)
                                activity?.runOnUiThread {
                                    processMessage(
                                        false,
                                        getString(R.string.unable_to_process_data)
                                    )
                                }
                            }

                        } else {
                            activity?.runOnUiThread {
                                processMessage(
                                    false,
                                    getString(R.string.unable_to_read_from_server)
                                )
                            }
                        }

                    } else {
                        activity?.runOnUiThread {
                            processMessage(
                                false,
                                getString(R.string.no_data_from_server)
                            )
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        processMessage(
                            false,
                            getString(R.string.no_communication)
                        )
                    }
                }
            } catch (ex: Exception) {
                activity?.runOnUiThread { processMessage(false, getString(R.string.no_download)) }
            }
        }
    }

    private fun processMessage(status: Boolean, message: String) {
        val adapter =
            view!!.findViewById<RecyclerView>(R.id.viewGroupList).adapter as GroupListAdapter

        if (status) {
            adapter.notifyDataSetChanged()
        } else {
            UIHelper.showAlert(this.context!!, getString(R.string.groups_screen_title), message)
        }
        showProgress(false)
    }

    private fun showProgress(visibility: Boolean) {
        view!!.findViewById<ProgressBar>(R.id.groupsProgressBar).visibility =
            if (visibility) View.VISIBLE else View.GONE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.ActivityIds.ADD_GROUP -> {
                if (resultCode == Activity.RESULT_OK) {
                    val status = data!!.getBooleanExtra(Constants.ACTIVITY_RESULT_KEY, false)
                    if (status) {
                        UIHelper.showAlert(
                            view!!.context,
                            getString(R.string.title_save_group_new),
                            getString(R.string.message_group_saved)
                        )
                        loadGroups()
                    } else {
                        UIHelper.showAlert(
                            view!!.context,
                            getString(R.string.title_save_group_new),
                            getString(R.string.message_group_not_saved)
                        )
                    }
                }
            }
            Constants.ActivityIds.EDIT_GROUP -> {
                if (resultCode == Activity.RESULT_OK) {
                    val status = data!!.getBooleanExtra(Constants.ACTIVITY_RESULT_KEY, false)
                    if (status) {
                        UIHelper.showAlert(
                            view!!.context,
                            getString(R.string.title_save_group_edit),
                            getString(R.string.message_group_updated)
                        )
                        loadGroups()
                    } else {
                        UIHelper.showAlert(
                            view!!.context,
                            getString(R.string.title_save_group_edit),
                            getString(R.string.message_group_not_updated)
                        )
                    }
                }
            }

        }
    }


}
