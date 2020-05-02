package `in`.bajajtech.apps.logbook.ui.settings

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.LoginActivity
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.utils.HTTPPostHelper
import `in`.bajajtech.apps.utils.JSONHelper
import `in`.bajajtech.apps.utils.PreferenceStore
import `in`.bajajtech.apps.utils.UIHelper
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import java.util.concurrent.CompletableFuture

class Settings:Fragment() {
    private lateinit var preferenceStore: PreferenceStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_settings,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceStore= PreferenceStore(view.context)
        view.findViewById<Button>(R.id.btn_update_password).setOnClickListener { updatePassword() }
        view.findViewById<Button>(R.id.btn_logout).setOnClickListener { doLogout() }
    }

    private fun updatePassword(){
        val oldPass = view!!.findViewById<EditText>(R.id.setting_text_old_password).text.toString()
        val newPass = view!!.findViewById<EditText>(R.id.setting_text_password).text.toString()
        val confirmPass = view!!.findViewById<EditText>(R.id.setting_text_repeat_password).text.toString()
        when {
            newPass.isEmpty() -> {
                UIHelper.showAlert(view!!.context,getString(R.string.settings_screen_title),getString(R.string.message_setting_invalid_new_password))
            }
            oldPass.isEmpty() -> {
                UIHelper.showAlert(view!!.context,getString(R.string.settings_screen_title),getString(R.string.message_setting_invalid_old_password))
            }
            newPass!=confirmPass -> {
                UIHelper.showAlert(view!!.context,getString(R.string.settings_screen_title),getString(R.string.message_user_password_missing))
            }
            else->{
                enableControls(false)
                CompletableFuture.runAsync {
                    val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
                    val dataString = "mode=UPPWD&opwd=$oldPass&upwd=$newPass"
                    try{
                        val result = HTTPPostHelper.doHTTPPost(Constants.UsersCodeURL,sessionId,dataString)
                        if(result!=null){
                            if(result.second.isNotEmpty()){
                                val (status,_) = JSONHelper.parseResponse(result.second,"code","code")
                                activity?.runOnUiThread { processMessage(status) }
                            }else{
                                activity?.runOnUiThread { processMessage(false) }
                            }
                        }else{
                            activity?.runOnUiThread { processMessage(false) }
                        }
                    }catch(ex: Exception){
//                        println(ex.message)
                        activity?.runOnUiThread { processMessage(false) }
                    }
                }

            }
        }
    }

    private fun doLogout(){
        enableControls(false)
        resetControls()
        val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
        val dataString = ""
        try{
            CompletableFuture.runAsync {
                HTTPPostHelper.doHTTPPost(Constants.LogoutURL,sessionId,dataString)
            }
        }catch(ex: Exception){
            //DO NOTHING
        }
        activity?.finish()
        Intent(this.context,LoginActivity::class.java).also{
            startActivity(it)
        }
    }

    private fun processMessage(status: Boolean){
        if(status){
            resetControls()
            UIHelper.showAlert(this.context!!,getString(R.string.menu_settings),getString(R.string.message_password_saved))
        }else{
            UIHelper.showAlert(this.context!!,getString(R.string.menu_settings),getString(R.string.message_password_not_saved))
        }
        enableControls(true)
    }

    private fun resetControls(){
        with(view!!) {
            findViewById<EditText>(R.id.setting_text_old_password).setText("")
            findViewById<EditText>(R.id.setting_text_password).setText("")
            findViewById<EditText>(R.id.setting_text_repeat_password).setText("")
        }
    }

    private fun enableControls(state: Boolean){
        with(view!!){
            findViewById<ProgressBar>(R.id.settingsProgressBar).visibility = if(state) View.GONE else View.VISIBLE
            findViewById<EditText>(R.id.setting_text_old_password).isEnabled=state
            findViewById<EditText>(R.id.setting_text_password).isEnabled=state
            findViewById<EditText>(R.id.setting_text_repeat_password).isEnabled=state
            findViewById<Button>(R.id.btn_update_password).isEnabled=state
            findViewById<Button>(R.id.btn_logout).isEnabled=state
        }
    }
}