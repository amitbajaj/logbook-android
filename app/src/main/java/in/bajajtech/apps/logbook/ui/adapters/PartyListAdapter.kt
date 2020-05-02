package `in`.bajajtech.apps.logbook.ui.adapters

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.models.PartyListViewModel
import `in`.bajajtech.apps.logbook.ui.models.PartyModel
import `in`.bajajtech.apps.logbook.ui.partyList.AddParty
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_party.view.*

class PartyListAdapter(ctx: Context, app: Application, parent: Fragment) : RecyclerView.Adapter<PartyListAdapter.PartyViewHolder>() {
    class PartyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partyName: TextView = itemView.findViewById(R.id.txn_direct_party_name)
        val inrBalance: TextView = itemView.findViewById(R.id.amount_inr)
        val usdBalance: TextView = itemView.findViewById(R.id.amount_usd)
        val aedBalance: TextView = itemView.findViewById(R.id.amount_aed)
        val editButton: ImageView = itemView.findViewById(R.id.img_edit_party)
    }

    private var mInflater: LayoutInflater = LayoutInflater.from(ctx)
    private var mParties: PartyListViewModel =
        PartyListViewModel(app)
    private val mParent = parent

    fun addParty(mNewParty: PartyModel, mNotifyChange: Boolean=true){
        mParties.partyList.add(mNewParty)
        if (mNotifyChange) notifyDataSetChanged()
    }

    fun removeAllParties(){
        mParties.partyList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolder {
        val itemView: View = mInflater.inflate(R.layout.list_item_party,parent,false)
        return PartyViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int {
        return mParties.partyList.size
    }

    override fun onBindViewHolder(holder: PartyViewHolder, position: Int) {
        if (mParties.partyList.isNotEmpty()){
            with(mParties.partyList[position]){
                holder.partyName.text = getPartyName()
                with(holder.inrBalance){
                    text = getFormattedBalance(Constants.Currencies.INR)
                    setTextColor(getCurrencyColorCode(Constants.Currencies.INR))
                }

                with(holder.usdBalance){
                    text = getFormattedBalance(Constants.Currencies.USD)
                    setTextColor(getCurrencyColorCode(Constants.Currencies.USD))
                }

                with(holder.aedBalance){
                    text = getFormattedBalance(Constants.Currencies.AED)
                    setTextColor(getCurrencyColorCode(Constants.Currencies.AED))
                }

                holder.itemView.setOnClickListener {
                    it.amount_row.visibility= if (it.amount_row.visibility==View.VISIBLE){
                        View.GONE
                    }else{
                        View.VISIBLE
                    }
                }
                holder.editButton.setOnClickListener {
                    val intent = Intent(it.context,
                        AddParty::class.java)
                    intent.putExtra(Constants.PARTY_ID,this)
                    mParent.startActivityForResult(intent,Constants.ActivityIds.EDIT_PARTY)
                }
                if(position%2==0){
                    holder.itemView.setBackgroundColor(Constants.ColorCodes.GRAY)
                }else{
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }
}