package com.rpn.adminmosque

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rpn.adminmosque.databinding.ActivityMainBinding
import com.rpn.adminmosque.repository.AuthAppRepository
import com.rpn.adminmosque.ui.activity.LoginActivity
import com.rpn.adminmosque.ui.viewmodel.LoginRegisterViewModel
import com.rpn.adminmosque.ui.viewmodel.MainViewModel
import com.rpn.adminmosque.utils.SettingsUtility
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent


class MainActivity : AppCompatActivity(), KoinComponent {


    lateinit var binding: ActivityMainBinding
    val mainViewModel by inject<MainViewModel>()

    val TAG = "MainActivity"
    val settingsUtility by inject<SettingsUtility>()
    val authAppRepository by inject<AuthAppRepository>()
    val loginRegisterViewModel by inject<LoginRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        mainViewModel.binding = binding

        val navView: BottomNavigationView = mainViewModel.binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_add_mosque,
                R.id.navigation_add_message,
                R.id.navigation_add_time
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (settingsUtility.userId == "" || settingsUtility.userId.isEmpty()) {
            navView.visibility = View.GONE
        } else {
            checkMosqueUpdated()
        }

    }

    fun checkMosqueUpdated() {
        mainViewModel.getMyMosquebyOwnerId {
            Log.d(TAG, "checkMosqueUpdated: $it")
        }
    }

    private fun updateUiWithUser() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val logout_menu = menu.findItem(R.id.menu_log_out)
        val add_mosque_menu = menu.findItem(R.id.menu_add_mosque)
        if (settingsUtility.userId == "" && settingsUtility.userId.isEmpty()) {
            logout_menu.isVisible = false
            add_mosque_menu.isVisible = true
        } else {
            logout_menu.isVisible = true
            add_mosque_menu.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.menu_add_mosque) {
            updateUiWithUser()
            return true
        }
        if (id == R.id.menu_log_out) {
            loginRegisterViewModel.logOut()
            mainViewModel.binding.navView.setSelectedItemId(R.id.navigation_home);
            mainViewModel.binding.navView.visibility = View.GONE
            updateUiWithUser()
            return true
        }
        return super.onOptionsItemSelected(item)

    }
}