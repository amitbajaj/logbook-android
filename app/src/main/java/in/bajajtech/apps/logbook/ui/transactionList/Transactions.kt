package `in`.bajajtech.apps.logbook.ui.transactionList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.utils.HTTPPostHelper
import `in`.bajajtech.apps.utils.JSONHelper
import `in`.bajajtech.apps.utils.PreferenceStore
import `in`.bajajtech.apps.utils.UIHelper
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.util.concurrent.CompletableFuture

class Transactions: Fragment() {
    private lateinit var preferenceStore: PreferenceStore
    private lateinit var adapter: TransactionListAdapter

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
        adapter = TransactionListAdapter(this.context!!, activity?.application!!)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(view.context)

        loadTransactions()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_transactions,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_direct_transaction->{
                Toast.makeText(this.context, R.string.menu_add_transaction_direct,Toast.LENGTH_SHORT).show()
            }
            R.id.menu_add_multi_direct_transaction->{
                Toast.makeText(this.context, R.string.menu_add_transaction_multi_direct,Toast.LENGTH_SHORT).show()
            }
            R.id.menu_add_currency_transfer->{
                Toast.makeText(this.context, R.string.menu_add_transaction_currency,Toast.LENGTH_SHORT).show()
            }
            R.id.menu_add_party_transfer->{
                Toast.makeText(this.context, R.string.menu_add_transaction_party,Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    private fun loadTransactions(){
        showProgress(true)
        CompletableFuture.runAsync {
            val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
            val dataString = "mode=QRY&ptyid=-1&fdt=&tdt="
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
                                    transactionModel = TransactionModel()
                                    transactionModel.setTransactionData(
                                        itemObject["tid"].toString().toInt(),itemObject["pty"].toString(),
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
                                activity?.runOnUiThread { processMessage(true,"") }
                            }catch(ex: Exception){
                                activity?.runOnUiThread { processMessage(false,getString(R.string.unable_to_process_data)) }
                            }
                        }else{
                            activity?.runOnUiThread { processMessage(false,getString(R.string.unable_to_read_from_server)) }
                        }
                    }else{
                        activity?.runOnUiThread { processMessage(false,getString(R.string.no_data_from_server)) }
                    }
                }else{
                    activity?.runOnUiThread { processMessage(false,getString(R.string.no_communication)) }
                }
            }catch(ex: Exception){
                activity?.runOnUiThread { processMessage(false,getString(R.string.no_download)) }
            }
        }

    }
    private fun processMessage(status: Boolean, message: String){
        val adapter = view!!.findViewById<RecyclerView>(R.id.viewTransactionList).adapter as TransactionListAdapter

        if(status){
            adapter.notifyDataSetChanged()
        }else{
            UIHelper.showAlert(this.context!!,getString(R.string.transactions_screen_title),message)
        }
        showProgress(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Constants.ActivityIds.ADD_PARTY->{
                if(resultCode == Activity.RESULT_OK){
                    val status = data!!.getBooleanExtra(Constants.ACTIVITY_RESULT_KEY,false)
                    if(status){
                        UIHelper.showAlert(view!!.context,getString(R.string.add_party),getString(R.string.party_saved))
                        loadTransactions()
                    }else{
                        UIHelper.showAlert(view!!.context,getString(R.string.add_party),getString(R.string.party_not_saved))
                    }
                }
            }
        }
    }

    private fun showProgress(visibility: Boolean){
        view!!.findViewById<ProgressBar>(R.id.transactionsProgressBar).visibility = if(visibility) View.VISIBLE else View.GONE
    }
}