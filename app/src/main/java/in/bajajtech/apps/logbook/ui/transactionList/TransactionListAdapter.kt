package `in`.bajajtech.apps.logbook.ui.transactionList

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionListAdapter(ctx: Context, app: Application): RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder>() {
    class TransactionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val partyName: TextView = itemView.findViewById(R.id.text_party_name)
        val transactionType: TextView = itemView.findViewById(R.id.text_transaction_type)
        val transactionDate: TextView = itemView.findViewById(R.id.text_transaction_date)
        val exchangeSection: LinearLayout = itemView.findViewById(R.id.section_exchange)
        val exchangeAmount: TextView = itemView.findViewById(R.id.text_exchange_amount)
        val exchangeDirection: TextView = itemView.findViewById(R.id.text_exchange_direction)
        val amount: TextView = itemView.findViewById(R.id.text_amount)
        val comments: TextView = itemView.findViewById(R.id.text_comments)
    }
    private var mInflater: LayoutInflater = LayoutInflater.from(ctx)
    private var mTransactions: TransactionListViewModel = TransactionListViewModel(app)

    fun addTransaction(mNewTransaction: TransactionModel, mNotifyChange: Boolean=false){
        mTransactions.mTransactionList.add(mNewTransaction)
        if(mNotifyChange) notifyDataSetChanged()
    }

    fun removeAllTransactions(){
        mTransactions.mTransactionList.clear()
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {
        val itemView: View = mInflater.inflate(R.layout.list_item_transaction,parent,false)
        return TransactionViewHolder(itemView)
    }

    override fun getItemCount(): Int = mTransactions.mTransactionList.size

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int
    ) {
        if(mTransactions.mTransactionList.isNotEmpty()){
            with(mTransactions.mTransactionList[position]){
                holder.partyName.text = getPartyName()
                holder.transactionType.text = getTransactionTypeText()
                holder.transactionDate.text = getTransactionDateText()
                if(getTransactionType()==Constants.TransactionTypes.DIRECT){
                    holder.exchangeSection.visibility=View.GONE
                }else{
                    holder.exchangeSection.visibility=View.VISIBLE
                    holder.exchangeAmount.text = getExchangeRateText()
                    holder.exchangeDirection.text = getExchangeDirectionText()
                }
                if(getAmount(Constants.Currencies.INR)!=0.0){
                    holder.amount.text = getFormattedAmount(Constants.Currencies.INR)
                }
                if(getAmount(Constants.Currencies.USD)!=0.0){
                    holder.amount.text = getFormattedAmount(Constants.Currencies.USD)
                }
                if(getAmount(Constants.Currencies.AED)!=0.0){
                    holder.amount.text = getFormattedAmount(Constants.Currencies.AED)
                }
                holder.comments.text = getComments()
                if(position%2==0){
                    holder.itemView.setBackgroundColor(Constants.ColorCodes.GRAY)
                }else{
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }


}