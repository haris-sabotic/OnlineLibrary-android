package com.ets.onlinebiblioteka

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.util.GlobalData
import com.ets.onlinebiblioteka.viewmodels.ProfileViewModel

class LoginActivity : AppCompatActivity() {
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        GlobalData.loadSharedPreferences(applicationContext)

        // if a user token was saved, move to main activity immediately
        GlobalData.getToken()?.let {
            startActivity(Intent(this, MainActivity::class.java).apply {
                intent.extras?.let {
                    putExtras(it)
                }
            })
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

    // manual navigation
    override fun onBackPressed() {
        when (navController.currentDestination!!.id) {
            R.id.nav_login_request_sent_password_fragment -> {
                navController.navigate(R.id.nav_login_action_request_sent_password_to_main)
            }
            R.id.nav_login_request_sent_username_fragment -> {
                navController.navigate(R.id.nav_login_action_request_sent_username_to_main)
            }
            R.id.nav_login_registration_fragment -> {
                navController.navigate(R.id.nav_login_action_registration_to_main)
            }
            R.id.nav_login_forgot_fragment -> {
                navController.navigate(R.id.nav_login_action_forgot_to_main)
            }
            else -> {
                // exit app
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(homeIntent)
            }
        }
    }
}