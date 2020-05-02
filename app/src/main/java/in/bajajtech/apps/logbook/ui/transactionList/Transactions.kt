package `in`.bajajtech.apps.logbook.ui.transactionList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.adapters.TransactionListAdapter
import `in`.bajajtech.apps.logbook.ui.controls.DateObject
import `in`.bajajtech.apps.logbook.ui.controls.DatePicker
import `in`.bajajtech.apps.logbook.ui.controls.PartyNameAdapter
import `in`.bajajtech.apps.logbook.ui.models.PartyModel
import `in`.bajajtech.apps.logbook.ui.models.TransactionModel
import `in`.bajajtech.apps.utils.HTTPPostHelper
import `in`.bajajtech.apps.utils.JSONHelper
import `in`.bajajtech.apps.utils.PreferenceStore
import `in`.bajajtech.apps.utils.UIHelper
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.util.concurrent.CompletableFuture

class Transactions: Fragment() {
    private lateinit var preferenceStore: PreferenceStore
    private lateinit var adapter: TransactionListAdapter
    private val partyList = mutableListOf<PartyModel>()
    private var dtFrom = ""
    private var dtTo = ""
    private var partyListString = ""
    private var accessId: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_transactions,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceStore = PreferenceStore(view.context)

        val recyclerView = view.findViewById<RecyclerView>(R.id.viewTransactionList)
        adapter = TransactionListAdapter(
            this.context!!,
            activity?.application!!,
            this,
            true
        )
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(view.context)
        view.findViewById<Button>(R.id.txn_list_from_date).setOnClickListener { showDate(it) }
        view.findViewById<Button>(R.id.txn_list_to_date).setOnClickListener { showDate(it) }
        view.findViewById<Button>(R.id.txn_list_load_txn).setOnClickListener { loadTransactions() }
        loadParties()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_transactions,menu)
        accessId = preferenceStore.getValue(Constants.PrefKeyAccessId).toIntOrNull()
        if(accessId==null){
            accessId=Constants.STAFF_ACCESS_ID
        }
        if(accessId!=Constants.ADMIN_ACCESS_ID){
            menu[0].isVisible=false
            menu[1].isVisible=false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_direct_transaction->{
                val intent = Intent(this.context, TransactionDirect::class.java)
                startActivityForResult(intent, Constants.ActivityIds.ADD_DIRECT_TRANSACTION)
            }
//            R.id.menu_add_multi_direct_transaction->{
//                Toast.makeText(this.context, R.string.menu_add_transaction_multi_direct,Toast.LENGTH_SHORT).show()
//            }
            R.id.menu_add_currency_transfer->{
                val intent = Intent(this.context, TransactionCurrency::class.java)
                startActivityForResult(intent, Constants.ActivityIds.ADD_CURRENCY_TRANSACTION)
            }
            R.id.menu_add_party_transfer->{
                val intent = Intent(this.context, TransactionParty::class.java)
                startActivityForResult(intent, Constants.ActivityIds.ADD_PARTY_TRANSACTION)
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Constants.ActivityIds.ADD_DIRECT_TRANSACTION->{
                if(resultCode == Activity.RESULT_OK){
                    val status = data!!.getBooleanExtra(Constants.ACTIVITY_RESULT_KEY,false)
                    if(status){
                        UIHelper.showAlert(this.context!!,getString(R.string.title_add_direct_transaction),getString(R.string.message_transaction_saved))
                    }else{
                        UIHelper.showAlert(this.context!!,getString(R.string.title_add_direct_transaction),getString(R.string.message_transaction_not_saved))
                    }
                }
            }
            Constants.ActivityIds.ADD_CURRENCY_TRANSACTION->{
                if(resultCode == Activity.RESULT_OK){
                    val status = data!!.getBooleanExtra(Constants.ACTIVITY_RESULT_KEY,false)
                    if(status){
                        UIHelper.showAlert(this.context!!,getString(R.string.title_add_currency_transaction),getString(R.string.message_transaction_saved))
                    }else{
                        UIHelper.showAlert(this.context!!,getString(R.string.title_add_currency_transaction),getString(R.string.message_transaction_not_saved))
                    }
                }
            }
            Constants.ActivityIds.ADD_PARTY_TRANSACTION->{
                if(resultCode == Activity.RESULT_OK){
                    val status = data!!.getBooleanExtra(Constants.ACTIVITY_RESULT_KEY,false)
                    if(status){
                        UIHelper.showAlert(this.context!!,getString(R.string.title_add_party_transaction),getString(R.string.message_transaction_saved))
                    }else{
                        UIHelper.showAlert(this.context!!,getString(R.string.title_add_party_transaction),getString(R.string.message_transaction_not_saved))
                    }
                }
            }
            Constants.ActivityIds.EDIT_TRANSACTION->{
                if(resultCode == Activity.RESULT_OK){
                    if(data!=null){
                        val status = data!!.getBooleanExtra(Constants.ACTIVITY_RESULT_KEY,false)
                        if(status){
                            val message = when(data!!.getIntExtra(Constants.SUB_ACTIVITY_KEY,-10)){
                                Constants.ActivityIds.EDIT_TRANSACTION_EDIT_ACTION->getString(R.string.message_transaction_saved)
                                Constants.ActivityIds.EDIT_TRANSACTION_DELETE_ACTION->getString(R.string.message_transaction_deleted)
                                else->getString(R.string.message_invalid_action)
                            }
                            UIHelper.showAlert(this.context!!,getString(R.string.title_edit_transaction),message)
                            loadTransactions()
                        }else{
                            val message = when(data!!.getIntExtra(Constants.SUB_ACTIVITY_KEY,-10)){
                                Constants.ActivityIds.EDIT_TRANSACTION_EDIT_ACTION->getString(R.string.message_transaction_not_saved)
                                Constants.ActivityIds.EDIT_TRANSACTION_DELETE_ACTION->getString(R.string.message_transaction_not_deleted)
                                else->getString(R.string.message_invalid_action)
                            }
                            UIHelper.showAlert(this.context!!,getString(R.string.title_edit_transaction),message)
                        }
                    }
                }
            }
        }
    }

    private fun loadTransactions(){
        enableControls(true)
        val dtOFrom = view!!.findViewById<Button>(R.id.txn_list_from_date).tag as DateObject?
        dtFrom = dtOFrom?.getDate() ?: ""
        val dtOTo = view!!.findViewById<Button>(R.id.txn_list_to_date).tag as DateObject?
        dtTo = dtOTo?.getDate() ?: ""
        val partyNameAdapter = view!!.findViewById<Spinner>(R.id.txn_list_parties).adapter as PartyNameAdapter
        partyListString = partyNameAdapter.getSelectedParties().joinToString { it.toString() }
        if (partyListString.isEmpty()){
            partyListString="-1"
        }

        CompletableFuture.runAsync {
            val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
            val dataString = "mode=QRY&ptyid=".plus(partyListString).plus("&fdt=").plus(dtFrom).plus("&tdt=").plus(dtTo)
            try{
                val result = HTTPPostHelper.doHTTPPost(Constants.TransactionsCodeURL,sessionId,dataString)
                if(result!=null){
                    if(result.second.isNotEmpty()){
                        val (status,dataObject) = JSONHelper.parseResponse(result.second,"list","code")
                        if(status){
                            try{
                                var dataArray = dataObject as JSONArray
                                var transactionModel: TransactionModel
                                var itemObject: JSONObject
                                adapter.removeAllTransactions()
                                dataArray.forEach {
                                    itemObject = it as JSONObject
                                    transactionModel =
                                        TransactionModel()
                                    transactionModel.setTransactionData(
                                        itemObject["tid"].toString().toInt(),
                                        itemObject["pty"].toString(),
                                        itemObject["pid"].toString().toInt(),
                                        java.sql.Date.valueOf(itemObject["dt"].toString()),
                                        itemObject["inr"].toString().toDouble(),
                                        itemObject["usd"].toString().toDouble(),
                                        itemObject["aed"].toString().toDouble(),
                                        itemObject["exh"].toString().toDouble(),
                                        itemObject["edr"].toString().toInt(),
                                        itemObject["ecy"].toString().toInt(),
                                        itemObject["cmt"].toString(),
                                        itemObject["tty"].toString()
                                    )
                                    adapter.addTransaction(transactionModel,false)
                                }
                                activity?.runOnUiThread { processTransactionMessage(true,"") }
                            }catch(ex: Exception){
                                activity?.runOnUiThread { processTransactionMessage(false,getString(R.string.unable_to_process_data)) }
                            }
                        }else{
                            activity?.runOnUiThread { processTransactionMessage(false,getString(R.string.unable_to_read_from_server)) }
                        }
                    }else{
                        activity?.runOnUiThread { processTransactionMessage(false,getString(R.string.no_data_from_server)) }
                    }
                }else{
                    activity?.runOnUiThread { processTransactionMessage(false,getString(R.string.no_communication)) }
                }
            }catch(ex: Exception){
                activity?.runOnUiThread { processTransactionMessage(false,getString(R.string.no_download)) }
            }
        }

    }

    private fun loadParties(){
        enableControls(true)
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
                                    partyModel.setPartyData(itemObject["id"].toString().toInt(),itemObject["name"].toString(),0.0,0.0,0.0)
                                    partyList.add(partyModel)
                                }
                                activity?.runOnUiThread{processPartyMessage(true,"")}
                            }catch(ex: Exception){
                                activity?.runOnUiThread{ processPartyMessage(false,getString(R.string.unable_to_process_data))}
                            }

                        }else{
                            activity?.runOnUiThread{ processPartyMessage(false,getString(R.string.unable_to_read_from_server))}
                        }

                    }else{
                        activity?.runOnUiThread{ processPartyMessage(false,getString(R.string.no_data_from_server))}
                    }
                }else{
                    activity?.runOnUiThread{ processPartyMessage(false, getString(R.string.no_communication))}
                }
            }catch(ex: Exception){
                activity?.runOnUiThread{ processPartyMessage(false,getString(R.string.no_download))}
            }
        }
    }

    private fun processPartyMessage(status: Boolean, message: String){
        if(status){
            val spinner = view!!.findViewById<Spinner>(R.id.txn_list_parties)
            val adapter = PartyNameAdapter(this.context!!,R.layout.spinner_item_partyname,partyList,true, spinner)
            spinner.adapter=adapter
            adapter.notifyDataSetChanged()

        }else{
            UIHelper.showAlert(this.context!!,getString(R.string.transactions_screen_title),message)
        }
        enableControls(false)

    }

    private fun processTransactionMessage(status: Boolean, message: String){
        if(status){
            val adapter = view!!.findViewById<RecyclerView>(R.id.viewTransactionList).adapter as TransactionListAdapter
            adapter.notifyDataSetChanged()
        }else{
            UIHelper.showAlert(this.context!!,getString(R.string.transactions_screen_title),message)
        }
        enableControls(false)
    }

    private fun enableControls(visibility: Boolean){
        view!!.findViewById<ProgressBar>(R.id.transactionsProgressBar).visibility = if(visibility) View.VISIBLE else View.GONE
        view!!.findViewById<Button>(R.id.txn_list_from_date).isEnabled=!visibility
        view!!.findViewById<Button>(R.id.txn_list_to_date).isEnabled=!visibility
        view!!.findViewById<Button>(R.id.txn_list_load_txn).isEnabled=!visibility
        view!!.findViewById<Spinner>(R.id.txn_list_parties).isEnabled=!visibility
        view!!.findViewById<RecyclerView>(R.id.viewTransactionList).isEnabled=!visibility
    }

    private fun showDate(btn: View){
        val datePicker = DatePicker(btn as Button)
        datePicker.show(this.childFragmentManager,btn.id.toString())
    }

}