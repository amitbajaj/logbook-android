package `in`.bajajtech.apps.logbook.ui.partyList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.utils.HTTPPostHelper
import `in`.bajajtech.apps.utils.JSONHelper
import `in`.bajajtech.apps.utils.PreferenceStore
import `in`.bajajtech.apps.utils.UIHelper
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import java.lang.Exception
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture

class AddParty : AppCompatActivity() {
    private lateinit var preferenceStore: PreferenceStore
    private var currentMode: Int = 0
    private var partyObject: PartyModel? = null

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
            }else{
                currentMode = Constants.ActivityIds.ADD_PARTY
            }

        }
        title=if(currentMode==Constants.ActivityIds.ADD_PARTY)
            getString(R.string.title_save_party_new)
        else
            getString(R.string.title_save_party_edit)

    }

    fun saveParty(btn: View){
        val partyNameControl =findViewById<EditText>(R.id.txt_party_name)
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
                        "mode=SVEDT&pid=${partyObject!!.getPartyId()}&pname=".plus(URLEncoder.encode(partyName,"utf-8"))
                    }else{
                        "mode=ADD&ptyname=".plus(URLEncoder.encode(partyName,"utf-8"))
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
        println("I am in options item")
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
        findViewById<EditText>(R.id.txt_party_name).isEnabled = mode
        findViewById<Button>(R.id.btn_save_party).isEnabled = mode
        findViewById<ProgressBar>(R.id.addPartyProgressBar).visibility = viewModeNegative
    }
}
