package `in`.bajajtech.apps.logbook.ui.groupList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
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
import androidx.appcompat.app.AppCompatActivity
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture

class AddGroup : AppCompatActivity() {
    private lateinit var preferenceStore: PreferenceStore
    private var currentMode: Int = 0
    private var groupId: Int = 0
    private var groupName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceStore = PreferenceStore(this)
        setContentView(R.layout.activity_add_group)
        val params = intent.extras
        if (params == null) {
            currentMode = Constants.ActivityIds.ADD_GROUP
        } else {
            currentMode = Constants.ActivityIds.EDIT_GROUP
            groupId = params.getInt(Constants.GroupObject.ID)
            groupName = params.getString(Constants.GroupObject.NAME).toString()
            findViewById<EditText>(R.id.txt_group_name).setText(groupName)
        }
        title = if (currentMode == Constants.ActivityIds.ADD_GROUP)
            getString(R.string.title_save_group_new)
        else
            "${getString(R.string.title_save_group_edit)} - $groupId"

        findViewById<Button>(R.id.btn_save_group).setOnClickListener { saveGroup() }
    }

    private fun saveGroup() {
        val groupNameControl = findViewById<EditText>(R.id.txt_group_name)
        groupName = groupNameControl.text.toString()
        if (groupName.isEmpty()) {
            UIHelper.showAlert(this, title.toString(), getString(R.string.message_enter_group))
        } else {
            enableControls(false)
            UIHelper.hideKeyboard(this.applicationContext, groupNameControl.windowToken)
            CompletableFuture.runAsync {
                try {
                    val dataString =
                        if (currentMode == Constants.ActivityIds.EDIT_GROUP && groupId > 0) {
                            "mode=SVEDT&gid=${groupId}&gname=${URLEncoder.encode(
                                groupName,
                                "utf-8"
                            )}"
                        } else {
                            "mode=ADD&grpname=${URLEncoder.encode(groupName, "utf-8")}"
                        }

                    val result = HTTPPostHelper.doHTTPPost(
                        Constants.GroupsCodeURL,
                        preferenceStore.getValue(Constants.PrefKeySessionId),
                        dataString
                    )
                    if (result != null) {
                        if (result.second.isNotEmpty()) {
                            val (status, _) = JSONHelper.parseResponse(result.second, "id", "code")
                            if (status) {
                                runOnUiThread { closeAndGoBack(true) }

                            } else {
                                runOnUiThread { closeAndGoBack(false) }
                            }
                        } else {
                            runOnUiThread { closeAndGoBack(false) }
                        }
                    } else {
                        runOnUiThread { closeAndGoBack(false) }
                    }
                } catch (ex: Exception) {
                    runOnUiThread { closeAndGoBack(false) }
                }
            }
        }
    }

    private fun closeAndGoBack(status: Boolean) {
        if (status) {
            // Put the String to pass back into an Intent and close this activity
            val intent = Intent()
            intent.putExtra(Constants.ACTIVITY_RESULT_KEY, status)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            UIHelper.showAlert(
                this,
                title.toString(),
                getString(
                    if (currentMode == Constants.ActivityIds.ADD_GROUP)
                        R.string.message_group_not_saved
                    else
                        R.string.message_group_not_updated
                )
            )
            enableControls(true)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    private fun enableControls(mode: Boolean) {
        val viewModeNegative = if (mode) View.GONE else View.VISIBLE
        findViewById<EditText>(R.id.txt_group_name).isEnabled = mode
        findViewById<Button>(R.id.btn_save_group).isEnabled = mode
        findViewById<ProgressBar>(R.id.addGroupProgressBar).visibility = viewModeNegative
    }
}