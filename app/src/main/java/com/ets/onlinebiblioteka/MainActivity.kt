package com.ets.onlinebiblioteka

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.valueIterator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.ProfileViewModel
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private val viewModel: ProfileViewModel by viewModels()

    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var fragmentBackActions: HashMap<Int, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalData.loadSharedPreferences(applicationContext)

        fragmentBackActions = hashMapOf(
            R.id.menu_item_moj_profil to R.id.nav_action_moj_profil_to_moji_zahtjevi
        )

        setupNavigation()
        setupDrawer()

        val navHeader = navigationView.getHeaderView(0)
            .findViewById<ConstraintLayout>(R.id.nav_drawer_header)

        val txtName = navHeader.findViewById<TextView>(R.id.nav_drawer_header_text_ime)
        val txtEmail = navHeader.findViewById<TextView>(R.id.nav_drawer_header_text_email)

        viewModel.getUser().observe(this) {
            txtName.text = it.name
            txtEmail.text = it.email
        }

        navHeader.setOnClickListener {
            val size = navigationView.menu.size
            navigationView.menu.clear()

            if (size == 2) {
                navigationView.inflateMenu(R.menu.drawer_menu_main)
            } else {
                navigationView.inflateMenu(R.menu.drawer_menu_profile)
                setLogOutBtnClickListener()
            }
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (fragmentBackActions.containsKey(destination.id)) {
                supportActionBar?.hide()

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

                window.statusBarColor = resources.getColor(R.color.light_gray, theme)
                window.insetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
            } else {
                supportActionBar?.show()

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                window.statusBarColor = resources.getColor(R.color.blue, theme)
                window.insetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (navController.currentDestination!!.id == R.id.menu_item_moji_zahtjevi) {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        } else {
            fragmentBackActions[navController.currentDestination!!.id]?.let {
                navController.navigate(it)
                return;
            }

            super.onBackPressed()
        }
    }

    private fun setLogOutBtnClickListener(){
        navigationView.menu.findItem(R.id.menu_item_log_out).setOnMenuItemClickListener {
            GlobalData.clearToken()
            startActivity(Intent(this, LoginActivity::class.java))

            true
        }
    }

    private fun setupNavigation() {
        navigationView = findViewById(R.id.main_nav_view)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment)
                    as NavHostFragment

        navController = navHostFragment.navController

        navigationView.setupWithNavController(navController)
    }

    private fun setupDrawer() {
        drawerLayout = findViewById(R.id.main_drawer_layout)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val topLevelDestinations: MutableSet<Int> = HashSet()
        for (node in navController.graph.nodes.valueIterator()) {
            topLevelDestinations.add(node.id)
        }

        val appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .setOpenableLayout(drawerLayout)
            .build()

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

}
