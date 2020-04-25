package `in`.bajajtech.apps.logbook.ui.partyList

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.util.concurrent.CompletableFuture

class Parties : Fragment() {
    private lateinit var preferenceStore: PreferenceStore
    private lateinit var adapter: PartyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_parties,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceStore = PreferenceStore(view.context)

        val recyclerView: RecyclerView = view.findViewById(R.id.viewPartyList)
        adapter = PartyListAdapter(this.context!!, activity?.application!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        loadParties()

        //text_parties.text = getString(R.string.party_screen_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_parties,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_party->{
                val intent = Intent(this.context, AddParty::class.java)
                startActivityForResult(intent, Constants.ActivityIds.ADD_PARTY)
            }
        }
        return true
    }

    private fun loadParties(){
        showProgress(true)
        CompletableFuture.runAsync{
            val sessionId = preferenceStore.getValue(Constants.PrefKeySessionId)
            val dataString = "mode=QRYBAL"
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
                                adapter.removeAllParties()
                                dataArray.forEach {
                                    itemObject = it as JSONObject
                                    partyModel =
                                        PartyModel()
                                    partyModel.setPartyData(adapter.getNewId(),itemObject["name"].toString(),itemObject["inrbal"].toString().toDouble(),itemObject["usdbal"].toString().toDouble(),itemObject["aedbal"].toString().toDouble())
                                    adapter.addParty(partyModel,false)
                                }
                                activity?.runOnUiThread{processMessage(true,"")}
                            }catch(ex: Exception){
                                activity?.runOnUiThread{ processMessage(false,getString(R.string.unable_to_process_data))}
                            }

                        }else{
                            activity?.runOnUiThread{ processMessage(false,getString(R.string.unable_to_read_from_server))}
                        }

                    }else{
                        activity?.runOnUiThread{ processMessage(false,getString(R.string.no_data_from_server))}
                    }
                }else{
                    activity?.runOnUiThread{ processMessage(false, getString(R.string.no_communication))}
                }
            }catch(ex: Exception){
                activity?.runOnUiThread{ processMessage(false,getString(R.string.no_download))}
            }
        }
    }

    private fun processMessage(status: Boolean, message: String){
        val adapter = view!!.findViewById<RecyclerView>(R.id.viewPartyList).adapter as PartyListAdapter

        if(status){
            adapter.notifyDataSetChanged()
        }else{
            UIHelper.showAlert(this.context!!,getString(R.string.party_screen_title),message)
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
                        loadParties()
                    }else{
                        UIHelper.showAlert(view!!.context,getString(R.string.add_party),getString(R.string.party_not_saved))
                    }
                }
            }
        }
    }

    private fun showProgress(visibility: Boolean){
        view!!.findViewById<ProgressBar>(R.id.partiesProgressBar).visibility = if(visibility) View.VISIBLE else View.GONE
    }
}
