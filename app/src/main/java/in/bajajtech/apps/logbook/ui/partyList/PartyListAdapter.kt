package `in`.bajajtech.apps.logbook.ui.partyList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_party.view.*

class PartyListAdapter(ctx: Context, app: Application) : RecyclerView.Adapter<PartyListAdapter.PartyViewHolder>() {
    class PartyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partyName: TextView = itemView.findViewById(R.id.partyName)
        val inrBalance: TextView = itemView.findViewById(R.id.amount_inr)
        val usdBalance: TextView = itemView.findViewById(R.id.amount_usd)
        val aedBalance: TextView = itemView.findViewById(R.id.amount_aed)
    }

    private var mInflater: LayoutInflater = LayoutInflater.from(ctx)
    private var mParties: PartyListViewModel = PartyListViewModel(app)

    fun addParty(mNewParty: PartyModel, mNotifyChange: Boolean=true){
        mParties.partyList.add(mNewParty)
        if (mNotifyChange) notifyDataSetChanged()
    }

    fun removeAllParties(){
        mParties.partyList.clear()
    }

    fun getNewId(): Int{
        var newId = 0
        mParties.partyList.forEach{
            if(it.getPartyId()>=newId){
                newId = it.getPartyId()+1
            }
        }
        return newId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolder {
        val itemView: View = mInflater.inflate(R.layout.list_item_party,parent,false)
        return PartyViewHolder(itemView)
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
                if(position%2==0){
                    holder.itemView.setBackgroundColor(Constants.ColorCodes.GRAY)
                }else{
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }
}