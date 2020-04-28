package `in`.bajajtech.apps.logbook

import `in`.bajajtech.apps.logbook.ui.partyList.Parties
import `in`.bajajtech.apps.logbook.ui.transactionList.Transactions
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavBarActivity : AppCompatActivity() {
    private val partyFragment = Parties()
    private val transactionFragment = Transactions()
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState==null){
            setContentView(R.layout.activity_nav_bar)
            showPartyMaintenance()
            navView = findViewById(R.id.nav_view)
            navView.setOnNavigationItemSelectedListener { item->
                when(item.itemId){
                    R.id.menu_parties->{
                        showPartyMaintenance()
                    }
                    R.id.menu_transactions->{
                        showTransactions()
                    }
                    R.id.menu_reports->{
                        println("Add reports screen")
                    }
                }
                return@setOnNavigationItemSelectedListener true
            }
        }

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        println("requestCode: $requestCode resultCode: $resultCode")
//    }

    private fun showPartyMaintenance(){
        title = getString(R.string.party_screen_title)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_container_view,partyFragment)
            commit()
        }
    }

    private fun showTransactions(){
        title = getString(R.string.transactions_screen_title)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_container_view,transactionFragment)
            commit()
        }
    }

    private fun showReports(){
        title = getString(R.string.reports_screen_title)
    }
}
