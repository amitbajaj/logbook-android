package `in`.bajajtech.apps.logbook.ui.transactionList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.controls.DateObject
import `in`.bajajtech.apps.logbook.ui.controls.DatePicker
import `in`.bajajtech.apps.logbook.ui.controls.PartyNameAdapter
import `in`.bajajtech.apps.logbook.ui.models.PartyModel
import `in`.bajajtech.apps.logbook.ui.models.TransactionModel
import `in`.bajajtech.apps.utils.*
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
import kotlin.math.abs

class TransactionEdit: AppCompatActivity() {
    private lateinit var preferenceStore: PreferenceStore
    private lateinit var txnObject: TransactionModel
    private val partyList = mutableListOf<PartyModel>()
    private var selectedPartyId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceStore = PreferenceStore(this)
        title = getString(R.string.title_edit_transaction)
        setContentView(R.layout.activity_edit_transaction)
        val params = intent.extras
        if(params==null){
            UIHelper.showAlert(this,getString(R.string.title_edit_transaction),getString(R.string.no_txn_data))
            onBackPressed()
        }else{
            txnObject = params.get(Constants.TRANSACTION_ID) as TransactionModel
            title = getString(R.string.title_edit_transaction).plus(" : ").plus(txnObject.getTransactionId().toString())
            //ptyName.text = txnObject.getPartyName()
            selectedPartyId = txnObject.getPartyId()
            with(findViewById<Button>(R.id.txn_edit_date)){
                setOnClickListener{ showDate(it)}
                text=txnObject.getTransactionDate().toString()
                val dtObject = DateObject(1,1,1990)
                dtObject.setDate(txnObject.getTransactionDate().toString())
                tag=dtObject
            }
            findViewById<TextView>(R.id.txn_edit_currency).text=when{
                txnObject.getAmount(Constants.Currencies.INR)!=0.0->Constants.Currencies.INR_NAME
                txnObject.getAmount(Constants.Currencies.USD)!=0.0->Constants.Currencies.USD_NAME
                txnObject.getAmount(Constants.Currencies.AED)!=0.0->Constants.Currencies.AED_NAME
                else->"UC"
            }

            findViewById<EditText>(R.id.txn_edit_amount).setText(when{
                txnObject.getAmount(Constants.Currencies.INR)!=0.0->CurrencyHelper.roundAsDouble(abs(txnObject.getAmount(Constants.Currencies.INR)),2)
                txnObject.getAmount(Constants.Currencies.USD)!=0.0->CurrencyHelper.roundAsDouble(abs(txnObject.getAmount(Constants.Currencies.USD)),2)
                txnObject.getAmount(Constants.Currencies.AED)!=0.0->CurrencyHelper.roundAsDouble(abs(txnObject.getAmount(Constants.Currencies.AED)),3)
                else->0.0
            }.toString())
            if(txnObject.getTransactionType()==Constants.TransactionTypes.DIRECT){
                findViewById<LinearLayout>(R.id.txn_edit_exchange_group).visibility=View.GONE
            }else{
                findViewById<LinearLayout>(R.id.txn_edit_exchange_group).visibility=View.VISIBLE
                findViewById<EditText>(R.id.txn_edit_exchange_rate).setText(txnObject.getExchangeRate().toString())
                val exchangeDirectionSpinner = findViewById<Spinner>(R.id.txn_edit_exchange_direction)
                ArrayAdapter.createFromResource(this,R.array.txn_exchange_direction,android.R.layout.simple_spinner_item).also { arrayAdapter ->
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    exchangeDirectionSpinner.adapter=arrayAdapter
                }
                exchangeDirectionSpinner.setSelection(txnObject.getExchangeDirection()-1)
            }
            findViewById<EditText>(R.id.txn_edit_comments).setText(txnObject.getComments())
            loadParties()
        }
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
                                var iCounter = 0
                                var selectedPartyIndex = 0
                                dataArray.forEach {
                                    itemObject = it as JSONObject
                                    partyModel =
                                        PartyModel()
                                    partyModel.setPartyData(itemObject["id"].toString().toInt(),itemObject["name"].toString(),0.0,0.0,0.0)
                                    if(partyModel.getPartyId()==selectedPartyId) selectedPartyIndex = iCounter
                                    partyList.add(partyModel)
                                    iCounter++
                                }
                                runOnUiThread{processPartyMessage(true,"", selectedPartyIndex)}
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

    private fun processPartyMessage(status: Boolean, message: String, selectedIndex: Int = 0){
        if(status){
            val spinner = findViewById<Spinner>(R.id.txn_edit_party_name)
            val adapter = PartyNameAdapter(this,R.layout.spinner_item_partyname,partyList,false, spinner)
            spinner.adapter=adapter
            spinner.setSelection(selectedIndex)
            adapter.notifyDataSetChanged()
        }else{
            UIHelper.showAlert(this,getString(R.string.title_edit_transaction),message)
        }
        enableControls(true)
    }

    private fun showDate(btn: View){
        val datePicker = DatePicker(btn as Button)
        datePicker.show(supportFragmentManager,btn.id.toString())
    }

    fun saveEditTransaction(btn: View){
        val transactionId = txnObject.getTransactionId().toString()
        val transactionPartyId = (findViewById<Spinner>(R.id.txn_edit_party_name).selectedItem as PartyModel).getPartyId()
        val transactionDate = HTTPPostHelper.encode((findViewById<Button>(R.id.txn_edit_date).tag as DateObject).getDate())
        val transactionAmount = findViewById<EditText>(R.id.txn_edit_amount).text.toString().toDoubleOrNull()
        val transactionExchange: Double?
        val transactionExchangeDirection: String
        if(txnObject.getTransactionType()==Constants.TransactionTypes.DIRECT){
            transactionExchange=0.0
            transactionExchangeDirection="1"
        }else{
            transactionExchange = findViewById<EditText>(R.id.txn_edit_exchange_rate).text.toString().toDoubleOrNull()
            transactionExchangeDirection = Constants.getExchangeDirection(findViewById<Spinner>(R.id.txn_edit_exchange_direction).selectedItem.toString()).toString()
        }
        val transactionComments = HTTPPostHelper.encode(findViewById<EditText>(R.id.txn_edit_comments).text.toString())

        if(transactionExchange == null || transactionAmount == null){
            var message = ""
            if(transactionAmount == null) message = getString(R.string.txn_amount_is_missing)
            if(transactionExchange == null) message = (if(message.isNotEmpty()) message.plus("\n") else "").plus(getString(R.string.txn_exchange_rate_is_missing))
            UIHelper.showAlert(this,getString(R.string.title_edit_transaction),message)
        }else{
            UIHelper.hideKeyboard(this.applicationContext,findViewById<EditText>(R.id.txn_edit_comments).windowToken)
            enableControls(false)
            CompletableFuture.runAsync {
                val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
                val dataString = "mode=SVEDT&tid=$transactionId&pid=$transactionPartyId&dt=$transactionDate&amt=$transactionAmount&exch=$transactionExchange&edir=$transactionExchangeDirection&cmt=$transactionComments"
                try{
                    val result = HTTPPostHelper.doHTTPPost(Constants.TransactionsCodeURL,sessionId,dataString)
                    if(result!=null){
                        if(result.second.isNotEmpty()){
                            val (status, _) = JSONHelper.parseResponse(result.second, "list", "code")
                            if(status){
                                runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_EDIT_ACTION, true) }
                            }else{
                                runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_EDIT_ACTION, false) }
                            }
                        }else{
                            runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_EDIT_ACTION, false) }
                        }
                    }else{
                        runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_EDIT_ACTION, false) }
                    }
                }catch(ex: Exception){
                    runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_EDIT_ACTION, false) }
                }
            }
        }

    }

    fun askForDeleteConfirmation(btn: View){
        UIHelper.showConfirmationDialog(this,
            getString(R.string.title_edit_transaction),getString(R.string.delete_confirmation),
            getString(R.string.button_text_delete_positive), { deleteTransaction() },
            getString(R.string.button_text_delete_negative), {})
    }

    private fun deleteTransaction(){
        val transactionId = txnObject.getTransactionId().toString()
        UIHelper.hideKeyboard(this.applicationContext,findViewById<EditText>(R.id.txn_edit_comments).windowToken)
        enableControls(false)
        CompletableFuture.runAsync {
            val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
            val dataString = "mode=TDEL&tid=$transactionId"
            try{
                val result = HTTPPostHelper.doHTTPPost(Constants.TransactionsCodeURL,sessionId,dataString)
                if(result!=null){
                    if(result.second.isNotEmpty()){
                        val (status, _) = JSONHelper.parseResponse(result.second, "list", "code")
                        if(status){
                            runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_DELETE_ACTION, true) }
                        }else{
                            runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_DELETE_ACTION, false) }
                        }
                    }else{
                        runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_DELETE_ACTION, false) }
                    }
                }else{
                    runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_DELETE_ACTION, false) }
                }
            }catch(ex: Exception){
                runOnUiThread { closeAndGoBack(Constants.ActivityIds.EDIT_TRANSACTION_DELETE_ACTION, false) }
            }
        }

    }

    private fun closeAndGoBack(action: Int, status: Boolean){
        if(status){
            val intent = Intent()
            intent.putExtra(Constants.ACTIVITY_RESULT_KEY,status)
            intent.putExtra(Constants.SUB_ACTIVITY_KEY,action)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }else{
            if(action==Constants.ActivityIds.EDIT_TRANSACTION_EDIT_ACTION)
                UIHelper.showAlert(this, getString(R.string.title_edit_transaction), getString(R.string.message_transaction_not_saved))
            else
                UIHelper.showAlert(this, getString(R.string.title_edit_transaction), getString(R.string.message_transaction_not_deleted))
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
        findViewById<Spinner>(R.id.txn_edit_party_name).isEnabled=mode
        findViewById<Button>(R.id.txn_edit_date).isEnabled = mode
        findViewById<EditText>(R.id.txn_edit_amount).isEnabled=mode
        findViewById<EditText>(R.id.txn_edit_exchange_rate).isEnabled=mode
        findViewById<Spinner>(R.id.txn_edit_exchange_direction).isEnabled=mode
        findViewById<TextView>(R.id.txn_edit_comments).isEnabled=mode
        findViewById<Button>(R.id.save_edit_transaction).isEnabled = mode
        findViewById<Button>(R.id.delete_edit_transaction).isEnabled = mode
        findViewById<ProgressBar>(R.id.edit_transaction_progress).visibility = viewModeNegative
    }
}