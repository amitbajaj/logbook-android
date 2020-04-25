package `in`.bajajtech.apps.logbook.ui.partyList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.utils.HTTPPostHelper
import `in`.bajajtech.apps.utils.JSONHelper
import `in`.bajajtech.apps.utils.PreferenceStore
import `in`.bajajtech.apps.utils.UIHelper
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import java.lang.Exception
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture

class AddParty : AppCompatActivity() {
    private lateinit var preferenceStore: PreferenceStore

    override fun onCreate(savedInstanceState: Bundle?) {
        preferenceStore = PreferenceStore(this)
        super.onCreate(savedInstanceState)
        title=getString(R.string.save_party_title)
        setContentView(R.layout.activity_add_party)
    }

    fun saveParty(btn: View){
        val partyNameControl =findViewById<EditText>(R.id.txt_party_name)
        val partyName = partyNameControl.text.toString()
        if(partyName.isEmpty()){
            UIHelper.showAlert(this,getString(R.string.save_party_title),getString(R.string.enter_party_name))
        }else{
            enableControls(false)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(partyNameControl.windowToken, 0)
            CompletableFuture.runAsync {
                try{
                    val dataString = "mode=ADD&ptyname=".plus(URLEncoder.encode(partyName,"utf-8"))
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
            UIHelper.showAlert(this, getString(R.string.add_party), getString(R.string.party_not_saved))
            enableControls(true)
        }
    }

    private fun enableControls(mode: Boolean){
        val viewModeNegative = if(mode) View.GONE else View.VISIBLE
        findViewById<EditText>(R.id.txt_party_name).isEnabled = mode
        findViewById<Button>(R.id.btn_save_party).isEnabled = mode
        findViewById<ProgressBar>(R.id.addPartyProgressBar).visibility = viewModeNegative
    }
}
