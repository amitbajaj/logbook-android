package `in`.bajajtech.apps.logbook

import `in`.bajajtech.apps.logbook.ui.groupList.Groups
import `in`.bajajtech.apps.logbook.ui.partyList.Parties
import `in`.bajajtech.apps.logbook.ui.reports.Reports
import `in`.bajajtech.apps.logbook.ui.settings.Settings
import `in`.bajajtech.apps.logbook.ui.transactionList.Transactions
import `in`.bajajtech.apps.logbook.ui.users.Users
import `in`.bajajtech.apps.utils.PreferenceStore
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class NavBarActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: PreferenceStore
    private val partyFragment = Parties()
    private val transactionFragment = Transactions()
    private val reportFragment = Reports()
    private val userFragment = Users()
    private val settingsFragment = Settings()
    private val groupFragment = Groups()

    //    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var sideNavView: NavigationView
    private lateinit var mainContainer: DrawerLayout
    private var accessId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences= PreferenceStore(this)
        accessId = sharedPreferences.getValue(Constants.PrefKeyAccessId).toIntOrNull()
        if(accessId == null) accessId= Constants.STAFF_ACCESS_ID
        if(savedInstanceState==null){
//            setContentView(R.layout.activity_nav_bar)
            setContentView(R.layout.activity_main_with_side_nav)

            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)

//            bottomNavView = findViewById(R.id.nav_view)
//            if(accessId!=Constants.ADMIN_ACCESS_ID){
//                bottomNavView.menu[0].isVisible=false
//                bottomNavView.menu[2].isVisible=false
//                bottomNavView.menu[3].isVisible=false
//                showTransactions()
//            }else{
//                showPartyMaintenance()
//            }
//            bottomNavView.setOnNavigationItemSelectedListener { item->
//                return@setOnNavigationItemSelectedListener handleMenuOptions(item)
//            }

            mainContainer = findViewById(R.id.main_container)
            sideNavView = findViewById(R.id.nav_view_side)
            if(accessId!=Constants.ADMIN_ACCESS_ID){
                sideNavView.menu[0].isVisible = false
                sideNavView.menu[1].isVisible = false
                sideNavView.menu[3].isVisible = false
                sideNavView.menu[4].isVisible = false
            }
            sideNavView.setNavigationItemSelectedListener { item ->
                return@setNavigationItemSelectedListener handleMenuOptions(item)
            }
            this.showTransactions()
        }

    }

    private fun handleMenuOptions(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_groups -> {
                if (accessId == Constants.ADMIN_ACCESS_ID) showGroups()
            }
            R.id.menu_parties -> {
                if (accessId == Constants.ADMIN_ACCESS_ID) showPartyMaintenance()
            }
            R.id.menu_transactions -> {
                showTransactions()
            }
            R.id.menu_reports -> {
                if (accessId == Constants.ADMIN_ACCESS_ID) showReports()
            }
            R.id.menu_user_maintenance -> {
                if (accessId == Constants.ADMIN_ACCESS_ID) showUsers()
            }
            R.id.menu_settings -> {
                showSettings()
            }
            else -> {
                return false
            }
        }
        mainContainer.closeDrawers()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            if (mainContainer.isDrawerOpen(GravityCompat.START)) {
                mainContainer.closeDrawer(GravityCompat.START)
            } else {
                mainContainer.openDrawer(GravityCompat.START)
            }
            true
        } else {
            false
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

    private fun showGroups() {
        title = getString(R.string.groups_screen_title)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_container_view, groupFragment)
            commit()
        }
    }
}
