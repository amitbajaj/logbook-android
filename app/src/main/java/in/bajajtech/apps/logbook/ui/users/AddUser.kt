package `in`.bajajtech.apps.logbook.ui.users

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.models.UserModel
import `in`.bajajtech.apps.utils.HTTPPostHelper
import `in`.bajajtech.apps.utils.JSONHelper
import `in`.bajajtech.apps.utils.PreferenceStore
import `in`.bajajtech.apps.utils.UIHelper
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture

class AddUser:AppCompatActivity() {
    private lateinit var preferenceStore: PreferenceStore
    private var currentMode: Int = 0
    private var userObject: UserModel? = null
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceStore = PreferenceStore(this)
        setContentView(R.layout.activity_add_user)
        if(intent.hasExtra(Constants.SUB_ACTIVITY_KEY)){
            currentMode = intent.getIntExtra(
                Constants.SUB_ACTIVITY_KEY,
                Constants.ActivityIds.ADD_USER
            )
        }else{
            finish()
        }
        if(intent.hasExtra(Constants.USER_ID)){
            userObject = intent.getParcelableExtra(Constants.USER_ID) as UserModel
        }else{
            currentMode = Constants.ActivityIds.ADD_USER
        }
        if(currentMode==Constants.ActivityIds.EDIT_USER){
            title = getString(R.string.edit_user)
            userId = userObject!!.getId()
            with(findViewById<EditText>(R.id.user_user_id)){
                setText(userObject!!.getUserId())
                isEnabled=false
            }
            findViewById<EditText>(R.id.user_username).setText(userObject!!.getUserName())
            with(findViewById<Spinner>(R.id.user_profile)){
                ArrayAdapter.createFromResource(
                    this@AddUser,
                    R.array.usr_profile,
                    android.R.layout.simple_spinner_item
                ).also { arrayAdapter ->
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    this.adapter=arrayAdapter
                }
                if(userObject!!.getAccessId()==Constants.ADMIN_ACCESS_ID){
                    setSelection(0)
                }else{
                    setSelection(1)
                }
            }
        }else {
            title = getString(R.string.add_user)
            with(findViewById<Spinner>(R.id.user_profile)) {
                ArrayAdapter.createFromResource(
                    this@AddUser,
                    R.array.usr_profile,
                    android.R.layout.simple_spinner_item
                ).also { arrayAdapter ->
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    this.adapter = arrayAdapter
                    setSelection(1)
                }
            }
        }
    }

    fun saveUser(btn: View){
        val userNameControl =findViewById<EditText>(R.id.user_username)
        val userName = userNameControl.text.toString()
        if(userName.isEmpty()){
            if(currentMode == Constants.ActivityIds.ADD_USER){
                UIHelper.showAlert(this,getString(R.string.add_user),getString(R.string.message_user_name_missing))
            }else{
                UIHelper.showAlert(this,getString(R.string.edit_user),getString(R.string.message_user_name_missing))
            }
        }else{
            val profileId = findViewById<Spinner>(R.id.user_profile).selectedItemPosition+1
            val userPassword = findViewById<EditText>(R.id.user_password).text.toString()
            var userId = findViewById<TextView>(R.id.user_user_id).text.toString()
            if(currentMode == Constants.ActivityIds.ADD_USER && userId.isEmpty()){
                UIHelper.showAlert(this,getString(R.string.add_user),getString(R.string.message_user_id_missing))
            }else{
                enableControls(false)
                UIHelper.hideKeyboard(this.applicationContext, userNameControl.windowToken)
                CompletableFuture.runAsync {
                    try{
                        val dataString = if(currentMode == Constants.ActivityIds.EDIT_USER && userObject!=null){
                            "mode=SVEDTAPI&uid=${userObject!!.getId()}&uname=${URLEncoder.encode(userName,"utf-8")}&uprof=$profileId&upass=${URLEncoder.encode(userPassword,"utf-8")}"
                        }else{
                            "mode=ADD&usrid=${URLEncoder.encode(userId,"utf-8")}&usrname=${URLEncoder.encode(userName,"utf-8")}&usrpass=${URLEncoder.encode(userPassword,"utf-8")}&pid=$profileId"
                        }
                        val result = HTTPPostHelper.doHTTPPost(Constants.UsersCodeURL,preferenceStore.getValue(Constants.PrefKeySessionId),dataString)
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
    }

    private fun closeAndGoBack(status: Boolean){
        if(status){
            // Put the String to pass back into an Intent and close this activity
            val intent = Intent()
            intent.putExtra(Constants.ACTIVITY_RESULT_KEY,status)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }else{
            if(currentMode==Constants.ActivityIds.ADD_USER){
                UIHelper.showAlert(this, getString(R.string.add_user), getString(R.string.message_user_not_saved))
            }else{
                UIHelper.showAlert(this, getString(R.string.edit_user), getString(R.string.message_user_not_updated))
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
        return false;
    }

    private fun enableControls(mode: Boolean){
        val viewModeNegative = if(mode) View.GONE else View.VISIBLE
        if(currentMode==Constants.ActivityIds.ADD_USER)
            findViewById<EditText>(R.id.user_user_id).isEnabled=mode
        findViewById<EditText>(R.id.user_username).isEnabled = mode
        findViewById<EditText>(R.id.user_password).isEnabled = mode
        findViewById<Spinner>(R.id.user_profile).isEnabled = mode
        findViewById<Button>(R.id.user_save_user).isEnabled = mode
        findViewById<ProgressBar>(R.id.addUserProgressBar).visibility = viewModeNegative
    }

}