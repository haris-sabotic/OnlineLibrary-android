package com.ets.onlinebiblioteka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ets.onlinebiblioteka.util.GlobalData

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

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.login_fragment_container_view)
                    as NavHostFragment

        navController = navHostFragment.navController
    }

    override fun onBackPressed() {
        if(navController.currentDestination!!.id == R.id.nav_login_forgot_info_fragment) {
            navController.navigate(R.id.nav_login_action_forgot_info_to_main)
        }
    }
}