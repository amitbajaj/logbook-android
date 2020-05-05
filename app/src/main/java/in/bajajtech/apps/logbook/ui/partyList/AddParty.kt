package `in`.bajajtech.apps.logbook.ui.partyList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.adapters.GroupNameAdapter
import `in`.bajajtech.apps.logbook.ui.models.GroupModel
import `in`.bajajtech.apps.logbook.ui.models.PartyModel
import `in`.bajajtech.apps.utils.HTTPPostHelper
import `in`.bajajtech.apps.utils.JSONHelper
import `in`.bajajtech.apps.utils.PreferenceStore
import `in`.bajajtech.apps.utils.UIHelper
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture

class AddParty : AppCompatActivity() {
    private lateinit var preferenceStore: PreferenceStore
    private var currentMode: Int = 0
    private var partyObject: PartyModel? = null
    private var groupList = mutableListOf<GroupModel>()
    private var selectedGroupId: Int = 0
    private var selectedGroupPos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        preferenceStore = PreferenceStore(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_party)
        val params = intent.extras
        if(params==null){
            currentMode = Constants.ActivityIds.ADD_PARTY
        }else{
            currentMode = Constants.ActivityIds.EDIT_PARTY
            partyObject = params.get(Constants.PARTY_ID) as PartyModel
            if(partyObject!=null){
                findViewById<EditText>(R.id.txt_party_name).setText(partyObject!!.getPartyName())
                selectedGroupId = partyObject!!.getGroupId()
            }else{
                currentMode = Constants.ActivityIds.ADD_PARTY
            }

        }
        title=if(currentMode==Constants.ActivityIds.ADD_PARTY)
            getString(R.string.title_save_party_new)
        else
            getString(R.string.title_save_party_edit)

        findViewById<Button>(R.id.btn_save_party).setOnClickListener { saveParty() }
        loadGroups()
    }


    private fun loadGroups() {
        enableControls(false)
        CompletableFuture.runAsync {
            val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
            val dataString = "mode=QRY"
            try {
                val result =
                    HTTPPostHelper.doHTTPPost(Constants.GroupsCodeURL, sessionId, dataString)
                if (result != null) {
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
                                var itemObject: JSONObject
                                var groupPos = 0
                                groupList.clear()
                                groupModel = GroupModel()
                                groupModel.setGroupId(-1)
                                groupModel.setGroupName("<No Group>")
                                groupList.add(groupModel)
                                dataArray.forEach {
                                    itemObject = it as JSONObject
                                    groupModel = GroupModel()
                                    groupModel.setGroupId(itemObject["gid"].toString().toInt())
                                    groupModel.setGroupName(itemObject["name"].toString())
                                    groupList.add(groupModel)
                                    if (groupModel.getGroupId() == selectedGroupId) {
                                        selectedGroupPos = groupPos + 1
                                    }
                                    groupPos++
                                }
                                runOnUiThread { processGroupMessage(true, "") }
                            } catch (ex: Exception) {
                                runOnUiThread {
                                    processGroupMessage(
                                        false,
                                        getString(R.string.unable_to_process_data)
                                    )
                                }
                            }

                        } else {
                            runOnUiThread {
                                processGroupMessage(
                                    false,
                                    getString(R.string.unable_to_read_from_server)
                                )
                            }
                        }

                    } else {
                        runOnUiThread {
                            processGroupMessage(
                                false,
                                getString(R.string.no_data_from_server)
                            )
                        }
                    }
                } else {
                    runOnUiThread {
                        processGroupMessage(
                            false,
                            getString(R.string.no_communication)
                        )
                    }
                }
            } catch (ex: Exception) {
                runOnUiThread { processGroupMessage(false, getString(R.string.no_download)) }
            }
        }
    }

    private fun processGroupMessage(status: Boolean, message: String) {
        if (status) {
            val spinner = findViewById<Spinner>(R.id.list_groups)
            val adapter = GroupNameAdapter(this, R.layout.spinner_item_groupname, groupList)
            spinner.adapter = adapter
            spinner.setSelection(selectedGroupPos)
            adapter.notifyDataSetChanged()
        } else {
            UIHelper.showAlert(this, getString(R.string.title_save_party_new), message)
        }
        enableControls(true)
    }


    private fun saveParty() {
        val partyNameControl =findViewById<EditText>(R.id.txt_party_name)
        var groupId =
            (findViewById<Spinner>(R.id.list_groups).selectedItem as GroupModel).getGroupId()
                .toString()
        if (groupId == "-1") groupId = ""
        val partyName = partyNameControl.text.toString()
        if(partyName.isEmpty()){
            if(currentMode == Constants.ActivityIds.ADD_PARTY){
                UIHelper.showAlert(this,getString(R.string.title_save_party_new),getString(R.string.enter_party_name))
            }else{
                UIHelper.showAlert(this,getString(R.string.title_save_party_edit),getString(R.string.enter_party_name))
            }
        }else{
            enableControls(false)
            UIHelper.hideKeyboard(this.applicationContext,partyNameControl.windowToken)
            CompletableFuture.runAsync {
                try{
                    val dataString = if(currentMode == Constants.ActivityIds.EDIT_PARTY && partyObject!=null){
                        "mode=SVEDT&pid=${partyObject!!.getPartyId()}&pname=${URLEncoder.encode(
                            partyName,
                            "utf-8"
                        )}&gid=$groupId"
                    }else{
                        "mode=ADD&ptyname=${URLEncoder.encode(partyName, "utf-8")}&gid=$groupId"
                    }

                    val result = HTTPPostHelper.doHTTPPost(Constants.PartiesCodeURL,preferenceStore.getValue(Constants.PrefKeySessionId),dataString)
                    if(result!=null){
                        if(result.second.isNotEmpty()){
                            val (status, _) = JSONHelper.parseResponse(result.second,"id", "code")
                            if(status){
                                runOnUiThread{closeAndGoBack(true)}

                            }else{
                                runOnUiThread{closeAndGoBack(false)}
                            }
                        }else{
                            runOnUiThread{closeAndGoBack(false)}
                        }
                    }else{
                        runOnUiThread{closeAndGoBack(false)}
                    }
                }catch(ex: Exception){
                    runOnUiThread{closeAndGoBack(false)}
                }
            }
        }
    }

    private fun closeAndGoBack(status: Boolean){
        if(status){
            // Put the String to pass back into an Intent and close this activity
            val intent = Intent()
            intent.putExtra(Constants.ACTIVITY_RESULT_KEY,status)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }else{
            if(currentMode==Constants.ActivityIds.ADD_PARTY){
                UIHelper.showAlert(this, getString(R.string.title_save_party_new), getString(R.string.party_not_saved))
            }else{
                UIHelper.showAlert(this, getString(R.string.title_save_party_edit), getString(R.string.party_not_saved))
            }

            enableControls(true)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        println("I am in options item")
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
                return true
            }
        }
        return false
    }

    private fun enableControls(mode: Boolean){
        val viewModeNegative = if(mode) View.GONE else View.VISIBLE
        findViewById<EditText>(R.id.txt_party_name).isEnabled = mode
        findViewById<Spinner>(R.id.list_groups).isEnabled = mode
        findViewById<Button>(R.id.btn_save_party).isEnabled = mode
        findViewById<ProgressBar>(R.id.addPartyProgressBar).visibility = viewModeNegative
    }
}
