package com.ets.onlinebiblioteka

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.ImageView
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.ets.onlinebiblioteka.fragments.main.MojiZahtjeviFragmentDirections
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.util.FcmService
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.util.NavDrawerController
import com.ets.onlinebiblioteka.viewmodels.ProfileViewModel
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavDrawerController {
    private val viewModel: ProfileViewModel by viewModels()

    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var drawerToggle: ActionBarDrawerToggle

    // fragments with custom actions on back pressed
    private var fragmentBackActions = hashMapOf(
        R.id.menu_item_moj_profil to R.id.nav_action_moj_profil_to_moji_zahtjevi,
        R.id.menu_item_edit_profil to R.id.nav_action_edit_profil_to_moj_profil,
        R.id.menu_item_knjige to R.id.nav_action_knjige_to_moji_zahtjevi,
    )
    // fragments with no action bar and a light gray status bar
    private var popupFragments = setOf(
        R.id.menu_item_moj_profil,
        R.id.menu_item_edit_profil,
        R.id.menu_item_zahtjev_info,
        R.id.menu_item_book_details,
        R.id.menu_item_author_details,
    )
    // fragments with an arrow instead of a hamburger icon in the action bar
    private var nonTopLevelFragments = setOf(
        R.id.menu_item_filters,
        R.id.menu_item_search_fragment,
        R.id.menu_item_all_books,
        R.id.menu_item_all_categories_or_genres
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalData.loadSharedPreferences(applicationContext)

        viewModel.loadUser()

        setupNavigation()
        setupDrawer()

        val navHeader = navigationView.getHeaderView(0)
            .findViewById<ConstraintLayout>(R.id.nav_drawer_header)

        val txtName = navHeader.findViewById<TextView>(R.id.nav_drawer_header_text_ime)
        val txtEmail = navHeader.findViewById<TextView>(R.id.nav_drawer_header_text_email)
        val imgProfile = navHeader.findViewById<ImageView>(R.id.nav_drawer_header_img)

        // load user data in the navigation drawer
        viewModel.getUser().observe(this) { user ->
            user?.let {
                txtName.text = it.name
                txtEmail.text = it.email

                Glide.with(this)
                    .load(GlobalData.getImageUrl(it.photo))
                    .centerCrop()
                    .placeholder(R.color.black)
                    .into(imgProfile)
            }
        }

        // switch between main and profile menus
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

        // handle popup fragments
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (popupFragments.contains(destination.id)) {
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


        intent.extras?.let { extras ->
            // if the app was opened from a notification with the topic nova knjiga, show book details
            extras.getParcelable<Book>(FcmService.TOPIC_NOVA_KNJIGA)?.let { book ->
                val action = MojiZahtjeviFragmentDirections.navActionMojiZahtjeviToBookDetails(book)
                navController.navigate(action)
            }
        }
    }

    override fun onBackPressed() {
        // close drawer if it was open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (navController.currentDestination!!.id == R.id.menu_item_moji_zahtjevi) {
            // close app
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        } else {
            // follow custom fragment back action
            fragmentBackActions[navController.currentDestination!!.id]?.let {
                navController.navigate(it)
                return;
            }

            // normally navigate backwards otherwise
            super.onBackPressed()
        }
    }

    // erase user token and go to login activity
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

        // all top level destinations
        val topLevelDestinations: MutableSet<Int> = HashSet()
        for (node in navController.graph.nodes.valueIterator()) {
            topLevelDestinations.add(node.id)
        }

        // minus the non-top level destinations
        topLevelDestinations.removeAll(nonTopLevelFragments)

        val appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .setOpenableLayout(drawerLayout)
            .build()

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // if the back arrow was pressed on a non-top level fragment, go back
        if (item.itemId == android.R.id.home && nonTopLevelFragments.contains(navController.currentDestination!!.id)) {
            onBackPressed()
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setDrawerEnabled(enabled: Boolean) {
        if (enabled) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            drawerLayout.close()
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    fun setTitle(title: String) {
        supportActionBar?.title = title
    }

    fun reloadUserData() {
        viewModel.loadUser()
    }
}
