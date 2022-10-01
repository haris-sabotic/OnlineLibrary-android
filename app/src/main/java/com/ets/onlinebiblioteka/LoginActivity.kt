package com.ets.onlinebiblioteka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.ProfileViewModel

class LoginActivity : AppCompatActivity() {
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        GlobalData.loadSharedPreferences(applicationContext)

        GlobalData.getToken()?.let {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Clear login data cached from previous login
        GlobalData.getSharedPreferences().getString(ProfileViewModel.USER_DATA_SHARED_PREFS_KEY, null)?.let {
            GlobalData.getSharedPreferences().edit()
                .remove(ProfileViewModel.USER_DATA_SHARED_PREFS_KEY)
                .commit()
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.login_fragment_container_view)
                    as NavHostFragment

        navController = navHostFragment.navController
    }

    override fun onBackPressed() {
        when (navController.currentDestination!!.id) {
            R.id.nav_login_request_sent_password_fragment -> {
                navController.navigate(R.id.nav_login_action_request_sent_password_to_main)
            }
            R.id.nav_login_request_sent_username_fragment -> {
                navController.navigate(R.id.nav_login_action_request_sent_username_to_main)
            }
            R.id.nav_login_forgot_fragment -> {
                navController.navigate(R.id.nav_login_action_forgot_to_main)
            }
            else -> {
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(homeIntent)
            }
        }
    }
}