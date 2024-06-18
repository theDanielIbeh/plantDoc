package com.example.plantdoc

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.cloudinary.android.MediaManager
import com.example.plantdoc.fragments.history.HistoryFragment
import com.example.plantdoc.fragments.upload.UploadFragment
import com.example.plantdoc.fragments.login.LoginViewModel
import com.example.plantdoc.fragments.plants.PlantsFragment
import com.example.plantdoc.utils.HelperFunctions
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val viewModel: LoginViewModel by viewModels()
    private var navHostFragment: NavHostFragment? = null
    private lateinit var drawerLayout: DrawerLayout
    private var config: HashMap<String?, String?> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        setSupportActionBar(findViewById(R.id.nav_view))
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment?.navController

        val inflater = navHostFragment?.navController?.navInflater
        val navGraph = inflater?.inflate(R.navigation.app_nav_graph)
        navGraph?.setStartDestination(startDestId = R.id.uploadFragment)

        val toolbar = findViewById<Toolbar>(R.id.nav_view)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.app_nav_host_fragment, UploadFragment()).commit()
//            navigationView.setCheckedItem(R.id.home_item)
//        }

        HelperFunctions.setMenu(
            this,
            viewModel.preferencesRepository
        )

        configCloudinary()
    }

    private fun configCloudinary() {
        config["cloud_name"] = "dkwvmnamr"
        config["api_key"] = "868793386732371";
        config["api_secret"] = "FXeMjkU_OoJX-7tP5zxnNqOZz_c";
        MediaManager.init(this, config);
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.setChecked(true)
        when (item.itemId) {
            R.id.home_item -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.app_nav_host_fragment, UploadFragment()).commit()
            }

            R.id.history_item -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.app_nav_host_fragment, HistoryFragment()).commit()
            }
            R.id.learning_item -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.app_nav_host_fragment, PlantsFragment()).commit()
            }
            R.id.log_out -> {
                lifecycleScope.launch {
                    HelperFunctions.logout(viewModel.preferencesRepository, this@AppActivity)
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}