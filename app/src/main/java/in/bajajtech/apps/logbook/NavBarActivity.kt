package `in`.bajajtech.apps.logbook

import `in`.bajajtech.apps.logbook.ui.partyList.Parties
import `in`.bajajtech.apps.logbook.ui.reports.Reports
import `in`.bajajtech.apps.logbook.ui.settings.Settings
import `in`.bajajtech.apps.logbook.ui.transactionList.Transactions
import `in`.bajajtech.apps.logbook.ui.users.Users
import `in`.bajajtech.apps.utils.PreferenceStore
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavBarActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: PreferenceStore
    private val partyFragment = Parties()
    private val transactionFragment = Transactions()
    private val reportFragment = Reports()
    private val userFragment = Users()
    private val settingsFragment = Settings()
    private lateinit var navView: BottomNavigationView
    private var accessId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences= PreferenceStore(this)
        accessId = sharedPreferences.getValue(Constants.PrefKeyAccessId).toIntOrNull()
        if(accessId == null) accessId= Constants.STAFF_ACCESS_ID
        if(savedInstanceState==null){
            setContentView(R.layout.activity_nav_bar)
            navView = findViewById(R.id.nav_view)
            if(accessId!=Constants.ADMIN_ACCESS_ID){
                navView.menu[0].isVisible=false
                navView.menu[2].isVisible=false
                navView.menu[3].isVisible=false
                showTransactions()
            }else{
                showPartyMaintenance()
            }

            navView.setOnNavigationItemSelectedListener { item->
                when(item.itemId){
                    R.id.menu_parties->{
                        if(accessId==Constants.ADMIN_ACCESS_ID) showPartyMaintenance()
                    }
                    R.id.menu_transactions->{
                        showTransactions()
                    }
                    R.id.menu_reports->{
                        if(accessId==Constants.ADMIN_ACCESS_ID) showReports()
                    }
                    R.id.menu_user_maintenance->{
                        if(accessId==Constants.ADMIN_ACCESS_ID) showUsers()
                    }
                    R.id.menu_settings->{
                        showSettings()
                    }
                }
                return@setOnNavigationItemSelectedListener true
            }
        }

    }

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
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_container_view,reportFragment)
            commit()
        }
    }

    private fun showUsers(){
        title=getString(R.string.users_screen_title)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_container_view,userFragment)
            commit()
        }
    }

    private fun showSettings(){
        title=getString(R.string.settings_screen_title)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_container_view,settingsFragment)
            commit()
        }
    }
}
