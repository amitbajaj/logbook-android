package `in`.bajajtech.apps.logbook.ui.reports

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.adapters.PartyNameAdapter
import `in`.bajajtech.apps.logbook.ui.adapters.ReportListAdapter
import `in`.bajajtech.apps.logbook.ui.controls.DateObject
import `in`.bajajtech.apps.logbook.ui.controls.DatePicker
import `in`.bajajtech.apps.logbook.ui.models.PartyModel
import `in`.bajajtech.apps.logbook.ui.models.ReportModel
import `in`.bajajtech.apps.logbook.ui.models.TransactionModel
import `in`.bajajtech.apps.utils.HTTPPostHelper
import `in`.bajajtech.apps.utils.JSONHelper
import `in`.bajajtech.apps.utils.PreferenceStore
import `in`.bajajtech.apps.utils.UIHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.util.concurrent.CompletableFuture

class Reports: Fragment() {
    private lateinit var preferenceStore: PreferenceStore
    private lateinit var adapter: ReportListAdapter
    private val partyList = mutableListOf<PartyModel>()
    private var dtFrom = ""
    private var dtTo = ""
    private var partyListString = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_report,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceStore= PreferenceStore(view.context)
        val recyclerView = view.findViewById<RecyclerView>(R.id.viewReportTransactionList)
        adapter = ReportListAdapter(
            this.context!!,
            activity?.application!!,
            this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(view.context)
        view.findViewById<Button>(R.id.report_from_date).setOnClickListener { showDate(it) }
        view.findViewById<Button>(R.id.report_to_date).setOnClickListener { showDate(it) }
        view.findViewById<Button>(R.id.report_load_txn).setOnClickListener { loadTransactions() }
        loadParties()
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
            val spinner = view!!.findViewById<Spinner>(R.id.report_parties)
            val adapter =
                PartyNameAdapter(
                    this.context!!,
                    R.layout.spinner_item_partyname,
                    partyList,
                    true,
                    spinner
                )
            spinner.adapter=adapter
            adapter.notifyDataSetChanged()

        }else{
            UIHelper.showAlert(this.context!!,getString(R.string.reports_screen_title),message)
        }
        enableControls(false)

    }

    private fun loadTransactions() {
        enableControls(true)
        val dtOFrom = view!!.findViewById<Button>(R.id.report_from_date).tag as DateObject?
        dtFrom = dtOFrom?.getDate() ?: ""
        val dtOTo = view!!.findViewById<Button>(R.id.report_to_date).tag as DateObject?
        dtTo = dtOTo?.getDate() ?: ""
        val partyNameAdapter = view!!.findViewById<Spinner>(R.id.report_parties).adapter as PartyNameAdapter
        partyListString = partyNameAdapter.getSelectedParties().joinToString { it.toString() }
        if (partyListString.isEmpty()){
            UIHelper.showAlert(view!!.context,getString(R.string.reports_screen_title),getString(R.string.no_party_selected))
            enableControls(false)
        }else{
            CompletableFuture.runAsync {
                val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
                val dataString = "mode=BALRPTAPI&pty=$partyListString&fdt=$dtFrom&tdt=$dtTo"
                try{
                    val result = HTTPPostHelper.doHTTPPost(Constants.ReportsCodeURL,sessionId,dataString)
                    if(result!=null){
                        if(result.second.isNotEmpty()){
                            val (status,dataObject) = JSONHelper.parseResponse(result.second,"data","code")
                            if(status){
                                try{
                                    val dataArray = dataObject as JSONArray
                                    var reportModel: ReportModel
                                    var transactionModel: TransactionModel
                                    var itemObject: JSONObject
                                    var childList: JSONArray
                                    var childObject: JSONObject

                                    var partyName: String
                                    adapter.removeAllEntries()
                                    dataArray.forEach { jsonObject ->
                                        itemObject = jsonObject as JSONObject
                                        partyName = ""
                                        partyList.forEach plist@{ partyModel ->
                                            if(partyModel.getPartyId().toString() == itemObject["cid"].toString()){
                                                partyName = partyModel.getPartyName()
                                                return@plist
                                            }
                                        }
                                        if(partyName.isEmpty()){
                                            partyName = "Name for ${itemObject["cid"].toString()} not found"
                                        }
                                        reportModel = ReportModel(
                                            partyName,
                                            itemObject["cid"].toString().toInt(),
                                            itemObject["oinr"].toString().toDouble(),
                                            itemObject["cinr"].toString().toDouble(),
                                            itemObject["ousd"].toString().toDouble(),
                                            itemObject["cusd"].toString().toDouble(),
                                            itemObject["oaed"].toString().toDouble(),
                                            itemObject["caed"].toString().toDouble()
                                        )
                                        childList = itemObject["list"] as JSONArray
                                        childList.forEach {
                                            childObject = it as JSONObject
                                            transactionModel =
                                                TransactionModel()
                                            transactionModel.setTransactionData(
                                                0,
                                                partyName,
                                                itemObject["cid"].toString().toInt(),
                                                java.sql.Date.valueOf(childObject["tdt"].toString()),
                                                childObject["inrbal"].toString().toDouble(),
                                                childObject["usdbal"].toString().toDouble(),
                                                childObject["aedbal"].toString().toDouble(),
                                                childObject["exh"].toString().toDouble(),
                                                childObject["edr"].toString().toInt(),
                                                childObject["ecy"].toString().toInt(),
                                                childObject["cmt"].toString(),
                                                childObject["tty"].toString()
                                            )
                                            reportModel.transactions.add(transactionModel)
                                        }
                                        adapter.addEntry(reportModel,false)
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



    }

    private fun processTransactionMessage(status: Boolean, message: String){
        if(status){
            val adapter = view!!.findViewById<RecyclerView>(R.id.viewReportTransactionList).adapter as ReportListAdapter
            adapter.notifyDataSetChanged()
        }else{
            UIHelper.showAlert(this.context!!,getString(R.string.reports_screen_title),message)
        }
        enableControls(false)
    }

    private fun enableControls(visibility: Boolean){
        view!!.findViewById<ProgressBar>(R.id.reportProgressBar).visibility = if(visibility) View.VISIBLE else View.GONE
        view!!.findViewById<Button>(R.id.report_from_date).isEnabled=!visibility
        view!!.findViewById<Button>(R.id.report_to_date).isEnabled=!visibility
        view!!.findViewById<Button>(R.id.report_load_txn).isEnabled=!visibility
        view!!.findViewById<Spinner>(R.id.report_parties).isEnabled=!visibility
        view!!.findViewById<RecyclerView>(R.id.viewReportTransactionList).isEnabled=!visibility
    }

    private fun showDate(btn: View){
        val datePicker = DatePicker(btn as Button)
        datePicker.show(this.childFragmentManager,btn.id.toString())
    }

}