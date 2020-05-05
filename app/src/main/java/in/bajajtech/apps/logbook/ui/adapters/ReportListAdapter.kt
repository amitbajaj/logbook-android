package `in`.bajajtech.apps.logbook.ui.adapters

import `in`.bajajtech.apps.logbook.Constants
import `in`.bajajtech.apps.logbook.R
import `in`.bajajtech.apps.logbook.ui.models.ReportListViewModel
import `in`.bajajtech.apps.logbook.ui.models.ReportModel
import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReportListAdapter(ctx: Context,app: Application, parent: Fragment): RecyclerView.Adapter<ReportListAdapter.ReportViewHolder>() {
    class ReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val mPartyName: TextView = itemView.findViewById(R.id.report_party_name)
        val mBalanceSection: LinearLayout = itemView.findViewById(R.id.report_party_balances)
        val mOpenBalanceINR: TextView = itemView.findViewById(R.id.report_opening_inr_balance)
        val mCloseBalanceINR: TextView = itemView.findViewById(R.id.report_closing_inr_balance)

        val mOpenBalanceUSD: TextView = itemView.findViewById(R.id.report_opening_usd_balance)
        val mCloseBalanceUSD: TextView = itemView.findViewById(R.id.report_closing_usd_balance)

        val mOpenBalanceAED: TextView = itemView.findViewById(R.id.report_opening_aed_balance)
        val mCloseBalanceAED: TextView = itemView.findViewById(R.id.report_closing_aed_balance)

        val mTransactionList: RecyclerView = itemView.findViewById(R.id.report_transaction_list)

    }

    private val mApp = app
    private val mInflater = LayoutInflater.from(ctx)
    private val mEntries = ReportListViewModel(app)
    private val mParent = parent

    fun addEntry(mReportEntry: ReportModel, mNotifyChange: Boolean=false){
        mEntries.reportList.add(mReportEntry)
        if(mNotifyChange) notifyDataSetChanged()
    }

    fun removeAllEntries(mNotifyChange: Boolean=false){
        mEntries.reportList.clear()
        if(mNotifyChange) notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_report, parent, false)
        return ReportViewHolder(itemView)
    }

    override fun getItemCount(): Int = mEntries.reportList.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        if(mEntries.reportList.isNotEmpty()){
            with(mEntries.reportList[position]){
                holder.mPartyName.text = getPartyName()
                holder.mPartyName.setOnClickListener {
                    if(holder.mBalanceSection.visibility==View.VISIBLE)
                        holder.mBalanceSection.visibility=View.GONE
                    else
                        holder.mBalanceSection.visibility=View.VISIBLE
                }
                holder.mOpenBalanceINR.text = getOpeningBalance(Constants.Currencies.INR)
                holder.mOpenBalanceINR.setTextColor(getBalanceColorCode(Constants.OPENING_BALANCE,Constants.Currencies.INR))
                holder.mOpenBalanceUSD.text = getOpeningBalance(Constants.Currencies.USD)
                holder.mOpenBalanceUSD.setTextColor(getBalanceColorCode(Constants.OPENING_BALANCE,Constants.Currencies.USD))
                holder.mOpenBalanceAED.text = getOpeningBalance(Constants.Currencies.AED)
                holder.mOpenBalanceAED.setTextColor(getBalanceColorCode(Constants.OPENING_BALANCE,Constants.Currencies.AED))

                holder.mCloseBalanceINR.text = getClosingBalance(Constants.Currencies.INR)
                holder.mCloseBalanceINR.setTextColor(getBalanceColorCode(Constants.CLOSING_BALANCE,Constants.Currencies.INR))
                holder.mCloseBalanceUSD.text = getClosingBalance(Constants.Currencies.USD)
                holder.mCloseBalanceUSD.setTextColor(getBalanceColorCode(Constants.CLOSING_BALANCE,Constants.Currencies.USD))
                holder.mCloseBalanceAED.text = getClosingBalance(Constants.Currencies.AED)
                holder.mCloseBalanceAED.setTextColor(getBalanceColorCode(Constants.CLOSING_BALANCE,Constants.Currencies.AED))

                val mTransactionListAdapter = TransactionListAdapter(holder.mTransactionList.context,mApp,mParent,false)
                transactions.forEach { mTransactionListAdapter.addTransaction(it) }
                holder.mTransactionList.apply {
                    layoutManager=LinearLayoutManager(holder.mTransactionList.context, RecyclerView.HORIZONTAL,false)
                    adapter=mTransactionListAdapter
                    setRecycledViewPool(RecyclerView.RecycledViewPool())
                }
            }
        }
    }
}