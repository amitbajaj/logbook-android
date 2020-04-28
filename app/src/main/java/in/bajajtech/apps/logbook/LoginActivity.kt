package `in`.bajajtech.apps.logbook

import `in`.bajajtech.apps.utils.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import java.util.concurrent.CompletableFuture

class LoginActivity : AppCompatActivity(){
    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.

    // Boolean telling us whether a connection is in progress, so we don't trigger overlapping
    // connections with consecutive button clicks.
    private var activeConnection = false

    //Shared Preferences Object
    private lateinit var sharedPrefs: PreferenceStore

    //Alert Message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.welcome_message)
        setContentView(R.layout.activity_login)
        sharedPrefs =
            PreferenceStore(applicationContext)
        findViewById<EditText>(R.id.txt_username).setText("amitbajaj")
        findViewById<EditText>(R.id.txt_password).setText("password")
        doLogin(findViewById(R.id.btn_login))
    }

    fun doLogin(btn: View) {
        val userId = findViewById<EditText>(R.id.txt_username)
        val password = findViewById<EditText>(R.id.txt_password)
        if(userId.text.isEmpty() || password.text.isEmpty()){
            UIHelper.showAlert(btn.context,"Login","Enter UserId/Password")
            return
        }
        val dataString = "mode=LGN&uid=".plus(userId.text.toString()).plus("&pwd=").plus(password.text.toString())
        val sessionId = sharedPrefs.getValue(Constants.PrefKeySessionId)
        if(!activeConnection){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(userId.windowToken, 0)
            activeConnection=true
            enableDisableControls(false)
            CompletableFuture.runAsync {
                try{
                    val result = HTTPPostHelper.doHTTPPost(Constants.LoginURL,sessionId,dataString)
                    runOnUiThread {processLoginResult(result)}
                }catch(ex: Exception){
                    runOnUiThread{processLoginResult(null)}
                }
            }
        }else{
            UIHelper.showAlert(btn.context,"Login","Previous request is still pending...")
        }


    }

    private fun processLoginResult(result: Pair<String, String>?){
        activeConnection=false
//        println("I am here... where are you?")
        if(result!=null){
            if(result.first.isNotEmpty()) sharedPrefs.setValue(Constants.PrefKeySessionId,result.first)
            val msg = result.second
            val (status, message) = JSONHelper.parseResponse(msg,"code","code")
            if(status){
                finish()
                Intent(this,NavBarActivity::class.java).also{
                    startActivity(it)
                }
//                Intent(this, PartyMaintenance::class.java).also {
//                    startActivity(it)
//                }
            }else{
                activeConnection=false
                if(message!=null){
                    UIHelper.showAlert(findViewById<Button>(R.id.btn_login).context,"Login",message as String)
                }else{
                    UIHelper.showAlert(findViewById<Button>(R.id.btn_login).context,"Login","Unknown error")
                }
            }
        }else{
            UIHelper.showAlert(findViewById<Button>(R.id.btn_login).context,"Login","Unknown error")
        }
        enableDisableControls(true)

    }


//    fun doLogin1(btnParam: View){
//        val userId = findViewById<EditText>(R.id.txt_username)
//        val password = findViewById<EditText>(R.id.txt_password)
//        val dataString = "mode=LGN&uid=".plus(userId.text.toString()).plus("&pwd=").plus(password.text.toString())
//        val sessionId = sharedPrefs.getValue(Constants.PrefKeySessionId)
//        if (!activeConnection) {
//            // Execute the async download.
//            networkFragment?.apply {
//                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//                imm?.hideSoftInputFromWindow(userId.windowToken, 0)
//                enableDisableControls(false)
//                activeConnection = true
//                doServerPost(Constants.LoginURL,sessionId, dataString)
//            }
//        }else{
//            UIHelper.showAlert(btnParam.context,"Login","Previous request pending....")
//        }
//    }
//
//    override fun updateFromDownload(result: Pair<String, String?>){
//        if(result.first.isNotEmpty()){
//            sharedPrefs.setValue(Constants.PrefKeySessionId,result.first)
//        }
//        val msg = if(result.second != null){
//            result.second as String
//        }else{
//            "{\"status\":\"fail\",\"reason\":\"Unknown Error..\"}"
//        }
//        val (status, message) = JSONHelper.parseResponse(msg,"reason")
//        if(status){
//            finish()
//            Intent(this, PartyMaintenance::class.java).also {
//                startActivity(it)
//            }
//        }else{
//            activeConnection=false
//            if(message!=null){
//                UIHelper.showAlert(findViewById<Button>(R.id.btn_login).context,"Login",message as String)
//            }else{
//                UIHelper.showAlert(findViewById<Button>(R.id.btn_login).context,"Login","Unknown error")
//            }
//            enableDisableControls(true)
//        }
//    }
//
//    override fun isDataNetworkActive(): Boolean {
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        return connectivityManager.isDefaultNetworkActive
//    }
//
//    override fun onProgressUpdate(progressCode: Int, percentComplete: Int) {
//        when (progressCode) {
//// You can add UI behavior for progress updates here.
//            ERROR -> {
////                Log.println(Log.INFO,"TAG","Some error : ".plus(progressCode).plus(" - ").plus(percentComplete))
//            }
//            CONNECT_SUCCESS -> {
////                Log.println(Log.INFO,"TAG","Connection success")
//            }
//            GET_INPUT_STREAM_SUCCESS -> {
////                Log.println(Log.INFO,"TAG","Initiated Stream : ".plus(progressCode).plus(" - ").plus(percentComplete))
//            }
//            PROCESS_INPUT_STREAM_IN_PROGRESS -> {
////                Log.println(Log.INFO,"TAG","Stream in progress : ".plus(progressCode).plus(" - ").plus(percentComplete))
//            }
//            PROCESS_INPUT_STREAM_SUCCESS -> {
////                Log.println(Log.INFO,"TAG","Stream completed : ".plus(progressCode).plus(" - ").plus(percentComplete))
//            }
//        }
//    }
//
//    override fun finishDownloading() {
//        activeConnection = false
//        networkFragment?.cancelDownload()
//        enableDisableControls(true)
//    }

    private fun enableDisableControls(mode: Boolean){
//        val viewModePositive = if(mode) View.VISIBLE else View.GONE
        val viewModeNegative = if(mode) View.GONE else View.VISIBLE
        findViewById<EditText>(R.id.txt_username).isEnabled = mode
        findViewById<EditText>(R.id.txt_password).isEnabled = mode
        findViewById<Button>(R.id.btn_login).isEnabled = mode
        findViewById<ProgressBar>(R.id.loginProgressBar).visibility = viewModeNegative
    }
}
