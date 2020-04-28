package `in`.bajajtech.apps.logbook.ui.transactionList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.controls.DateObject
import `in`.bajajtech.apps.logbook.ui.controls.DatePicker
import `in`.bajajtech.apps.logbook.ui.controls.PartyNameAdapter
import `in`.bajajtech.apps.logbook.ui.partyList.PartyModel
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

class TransactionCurrency: AppCompatActivity() {
    private lateinit var preferenceStore: PreferenceStore
    private val partyList = mutableListOf<PartyModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceStore = PreferenceStore(this)
        title = getString(R.string.menu_add_transaction_currency)
        setContentView(R.layout.activity_add_transaction_currency)
        findViewById<Button>(R.id.txn_currency_date).setOnClickListener { showDate(it) }
        val firstCurrencySpinner = findViewById<Spinner>(R.id.txn_currency_first)
        val secondCurrencySpinner = findViewById<Spinner>(R.id.txn_currency_second)
        val exchangeDirectionSpinner = findViewById<Spinner>(R.id.txn_currency_exchange_direction)
        ArrayAdapter.createFromResource(this, R.array.txn_currency,android.R.layout.simple_spinner_item).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            firstCurrencySpinner.adapter=arrayAdapter
            secondCurrencySpinner.adapter=arrayAdapter
        }

        ArrayAdapter.createFromResource(this, R.array.txn_exchange_direction,android.R.layout.simple_spinner_item).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            exchangeDirectionSpinner.adapter=arrayAdapter
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
                                    partyModel = PartyModel()
                                    partyModel.setPartyData(itemObject["id"].toString().toInt(),itemObject["name"].toString(),0.0,0.0,0.0)
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
            val spinner = findViewById<Spinner>(R.id.txn_currency_party_name)
            val adapter = PartyNameAdapter(this,R.layout.spinner_item_partyname,partyList,false, spinner)
            spinner.adapter=adapter
            adapter.notifyDataSetChanged()
        }else{
            UIHelper.showAlert(this,getString(R.string.title_add_currency_transaction),message)
        }
        enableControls(true)
    }

    private fun showDate(btn: View){
        val datePicker = DatePicker(btn as Button)
        datePicker.show(supportFragmentManager,btn.id.toString())
    }

    fun saveCurrencyTransaction(btn: View){
        val partySpinner = (findViewById<Spinner>(R.id.txn_currency_party_name).selectedItem as PartyModel).getPartyId()
        val firstCurrencySpinner = Constants.getCurrencyId(findViewById<Spinner>(R.id.txn_currency_first).selectedItem.toString())
        val secondCurrencySpinner = Constants.getCurrencyId(findViewById<Spinner>(R.id.txn_currency_second).selectedItem.toString())
        val exchangeDirectionSpinner = Constants.getExchangeDirection(findViewById<Spinner>(R.id.txn_currency_exchange_direction).selectedItem.toString())
        val exchangeRate = findViewById<EditText>(R.id.txn_currency_exchange_rate).text.toString().toDoubleOrNull()
        val transactionDate = findViewById<Button>(R.id.txn_currency_date).tag as DateObject?
        val transactionAmount = findViewById<EditText>(R.id.txn_currency_amount).text.toString().toDoubleOrNull()
        val transactionComments = findViewById<EditText>(R.id.txn_currency_comments).text.toString()

        if(transactionDate == null || transactionAmount == null || exchangeRate == null || firstCurrencySpinner == secondCurrencySpinner){
            var message: String = ""
            if(transactionDate == null) message = getString(R.string.txn_date_missing)
            if(transactionAmount == null) message = (if(message.isNotEmpty()) message.plus("\n") else "").plus(getString(R.string.txn_amount_is_missing))
            if(exchangeRate == null) message = (if(message.isNotEmpty()) message.plus("\n") else "").plus(getString(R.string.txn_exchange_rate_is_missing))
            if(firstCurrencySpinner == secondCurrencySpinner){
                if(message.isNotEmpty()){
                    message = message.plus("\n")
                }
                message = message.plus(getString(R.string.txn_exchange_same_currency))
            }
            UIHelper.showAlert(this,getString(R.string.title_add_currency_transaction),message)
        }else{
            enableControls(false)
            UIHelper.hideKeyboard(this.applicationContext,findViewById<EditText>(R.id.txn_currency_comments).windowToken)
            CompletableFuture.runAsync {
                val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
                val dataString = "mode=SVCCY&ptyid=$partySpinner&ccy1id=$firstCurrencySpinner&ccy2id=$secondCurrencySpinner&txnedir=$exchangeDirectionSpinner&txnexch=$exchangeRate&txndt=${HTTPPostHelper.encode(transactionDate.getDate())}&txnamt=$transactionAmount&txncmts=${HTTPPostHelper.encode(transactionComments)}"
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
            UIHelper.showAlert(this, getString(R.string.title_add_currency_transaction), getString(R.string.message_transaction_not_saved))
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
        return false;
    }

    private fun enableControls(mode: Boolean){
        val viewModeNegative = if(mode) View.GONE else View.VISIBLE
        findViewById<Spinner>(R.id.txn_currency_party_name).isEnabled = mode
        findViewById<Spinner>(R.id.txn_currency_first).isEnabled=mode
        findViewById<Spinner>(R.id.txn_currency_second).isEnabled=mode
        findViewById<Spinner>(R.id.txn_currency_exchange_direction).isEnabled=mode
        findViewById<TextView>(R.id.txn_currency_exchange_rate).isEnabled=mode
        findViewById<Button>(R.id.txn_currency_date).isEnabled=mode
        findViewById<TextView>(R.id.txn_currency_amount).isEnabled=mode
        findViewById<TextView>(R.id.txn_currency_comments).isEnabled=mode
        findViewById<Button>(R.id.save_currency_transaction).isEnabled = mode
        findViewById<ProgressBar>(R.id.currency_transaction_progress).visibility = viewModeNegative
    }
}