package `in`.bajajtech.apps.logbook.ui.transactionList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.adapters.PartyNameAdapter
import `in`.bajajtech.apps.logbook.ui.controls.DateObject
import `in`.bajajtech.apps.logbook.ui.controls.DatePicker
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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.util.concurrent.CompletableFuture

class TransactionDirect: AppCompatActivity() {
    private lateinit var preferenceStore: PreferenceStore
    private val partyList = mutableListOf<PartyModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceStore = PreferenceStore(this)
        title = getString(R.string.title_add_direct_transaction)
        setContentView(R.layout.activity_add_transaction_direct)
        findViewById<Button>(R.id.txn_direct_date).setOnClickListener { showDate(it) }
        findViewById<Button>(R.id.save_direct_transaction).setOnClickListener { saveDirectTransaction() }
        val currencySpinner = findViewById<Spinner>(R.id.txn_direct_currency)
        val transactionTypeSpinner = findViewById<Spinner>(R.id.txn_direct_type)
        ArrayAdapter.createFromResource(this,R.array.txn_currency,android.R.layout.simple_spinner_item).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            currencySpinner.adapter=arrayAdapter
        }

        ArrayAdapter.createFromResource(this,R.array.txn_type,android.R.layout.simple_spinner_item).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            transactionTypeSpinner.adapter=arrayAdapter
        }
        loadParties()
    }

    private fun loadParties(){
        enableControls(false)
        CompletableFuture.runAsync{
            val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
            val dataString = "mode=QRY"
            try {
                val result = HTTPPostHelper.doHTTPPost(Constants.PartiesCodeURL, sessionId, dataString)
                if(result!=null){
                    if(result.second.isNotEmpty()){
                        val (status, dataObject) = JSONHelper.parseResponse(result.second,"list", "code")
                        if(status){
                            try{
                                val dataArray = (dataObject as JSONArray)
                                var partyModel: PartyModel
                                var itemObject: JSONObject
                                partyList.clear()
                                dataArray.forEach {
                                    itemObject = it as JSONObject
                                    partyModel =
                                        PartyModel()
                                    partyModel.setPartyData(
                                        itemObject["id"].toString().toInt(),
                                        itemObject["name"].toString(),
                                        0,
                                        "",
                                        0.0,
                                        0.0,
                                        0.0
                                    )
                                    partyList.add(partyModel)
                                }
                                runOnUiThread{processPartyMessage(true,"")}
                            }catch(ex: Exception){
                                runOnUiThread{ processPartyMessage(false,getString(R.string.unable_to_process_data))}
                            }

                        }else{
                            runOnUiThread{ processPartyMessage(false,getString(R.string.unable_to_read_from_server))}
                        }

                    }else{
                        runOnUiThread{ processPartyMessage(false,getString(R.string.no_data_from_server))}
                    }
                }else{
                    runOnUiThread{ processPartyMessage(false, getString(R.string.no_communication))}
                }
            }catch(ex: Exception){
                runOnUiThread{ processPartyMessage(false,getString(R.string.no_download))}
            }
        }
    }

    private fun processPartyMessage(status: Boolean, message: String){
        if(status){
            val spinner = findViewById<Spinner>(R.id.txn_direct_party_name)
            val adapter =
                PartyNameAdapter(
                    this,
                    R.layout.spinner_item_partyname,
                    partyList,
                    false,
                    spinner
                )
            spinner.adapter=adapter
            adapter.notifyDataSetChanged()
        }else{
            UIHelper.showAlert(this,getString(R.string.title_add_direct_transaction),message)
        }
        enableControls(true)
    }

    private fun showDate(btn: View){
        val datePicker = DatePicker(btn as Button)
        datePicker.show(supportFragmentManager,btn.id.toString())
    }

    private fun saveDirectTransaction() {
        val partySpinner = (findViewById<Spinner>(R.id.txn_direct_party_name).selectedItem as PartyModel).getPartyId()
        val currencySpinner = Constants.getCurrencyId(findViewById<Spinner>(R.id.txn_direct_currency).selectedItem.toString())
        val transactionTypeSpinner = Constants.getTransactionSubType(findViewById<Spinner>(R.id.txn_direct_type).selectedItem.toString())
        val transactionDate = findViewById<Button>(R.id.txn_direct_date).tag as DateObject?
        val transactionAmount = findViewById<EditText>(R.id.txn_direct_amount).text.toString().toDoubleOrNull()
        val transactionComments = findViewById<EditText>(R.id.txn_direct_comments).text.toString()

        if(transactionDate == null || transactionAmount == null){
            var message = ""
            if(transactionDate == null) message = getString(R.string.txn_date_missing)
            if(transactionAmount == null) message = (if(message.isNotEmpty()) message.plus("\n") else "").plus(getString(R.string.txn_amount_is_missing))
            UIHelper.showAlert(this,getString(R.string.title_add_direct_transaction),message)
        }else{
            UIHelper.hideKeyboard(this.applicationContext,findViewById<EditText>(R.id.txn_direct_comments).windowToken)
            enableControls(false)
            CompletableFuture.runAsync {
                val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
                val dataString = "mode=SVDRT&ptyid=$partySpinner&ccyid=$currencySpinner&txntype=$transactionTypeSpinner&txndt=${HTTPPostHelper.encode(transactionDate.getDate())}&txnamt=$transactionAmount&txncmts=${HTTPPostHelper.encode(transactionComments)}"
                try{
                    val result = HTTPPostHelper.doHTTPPost(Constants.TransactionsCodeURL,sessionId,dataString)
                    if(result!=null){
                        if(result.second.isNotEmpty()){
                            val (status, _) = JSONHelper.parseResponse(result.second, "list", "code")
                            if(status){
                                runOnUiThread { closeAndGoBack(true) }
                            }else{
                                runOnUiThread { closeAndGoBack(false) }
                            }
                        }else{
                            runOnUiThread { closeAndGoBack(false) }
                        }
                    }else{
                        runOnUiThread { closeAndGoBack(false) }
                    }
                }catch(ex: Exception){
                    runOnUiThread { closeAndGoBack(false) }
                }
            }
        }

    }

    private fun closeAndGoBack(status: Boolean){
        if(status){
            val intent = Intent()
            intent.putExtra(Constants.ACTIVITY_RESULT_KEY,status)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }else{
            UIHelper.showAlert(this, getString(R.string.title_add_direct_transaction), getString(R.string.message_transaction_not_saved))
            enableControls(true)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
        findViewById<Spinner>(R.id.txn_direct_party_name).isEnabled = mode
        findViewById<Spinner>(R.id.txn_direct_currency).isEnabled=mode
        findViewById<Spinner>(R.id.txn_direct_type).isEnabled=mode
        findViewById<Button>(R.id.txn_direct_date).isEnabled=mode
        findViewById<TextView>(R.id.txn_direct_amount).isEnabled=mode
        findViewById<TextView>(R.id.txn_direct_comments).isEnabled=mode
        findViewById<Button>(R.id.save_direct_transaction).isEnabled = mode
        findViewById<ProgressBar>(R.id.direct_transaction_progress).visibility = viewModeNegative
    }
}