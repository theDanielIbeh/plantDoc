package com.example.plantdoctor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.cloudinary.android.MediaManager
import com.example.plantdoctor.fragments.login.LoginViewModel
import com.example.plantdoctor.utils.HelperFunctions
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val viewModel: LoginViewModel by viewModels()
    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null
    private lateinit var drawerLayout: DrawerLayout
    private var config: HashMap<String?, String?> = HashMap()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        setSupportActionBar(findViewById(R.id.nav_view))
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment) as NavHostFragment
        navController = navHostFragment?.navController

        val inflater = navHostFragment?.navController?.navInflater
        val navGraph = inflater?.inflate(R.navigation.app_nav_graph)
        navGraph?.setStartDestination(startDestId = R.id.uploadFragment)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer)

        // Find the ImageViews in the toolbar
        val backArrow: ImageView = findViewById(R.id.backArrow)
        val openMenu: ImageView = findViewById(R.id.openMenu)

        backArrow.setOnClickListener {
//            onBackPressedDispatcher.onBackPressed()
            navController?.popBackStack()
        }

        // Set an OnClickListener for the menu icon
        openMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START) // Close the drawer if it's open
            } else {
                drawerLayout.openDrawer(GravityCompat.START) // Open the drawer if it's closed
            }
        }

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
//
//        val toggle = ActionBarDrawerToggle(
//            this,
//            drawerLayout,
//            null,
//            R.string.open_nav,
//            R.string.close_nav
//        )
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.app_nav_host_fragment, UploadFragment()).commit()
//            navigationView.setCheckedItem(R.id.home_item)
//        }

//        HelperFunctions.setMenu(
//            this,
//            viewModel.preferencesRepository
//        )

        viewModel.isLoggedIn.observe(this) {
            it?.let {
                navigationView.menu.findItem(R.id.history_item).setVisible(it)
                navigationView.menu.findItem(R.id.log_out).setVisible(it)
                navigationView.menu.findItem(R.id.exit).setVisible(!it)
            }
        }

        Log.d("currentDestination", navController?.currentDestination?.id.toString())

        lifecycleScope.launch {
            navController?.currentBackStack?.collectLatest {
                Log.d("AppActivity", it.toString())
                Log.d("AppActivity", it.size.toString())
                Log.d("currentDestination", navController?.currentDestination?.id.toString())
                Log.d("currentDestination", R.id.uploadFragment.toString())

                if (arrayListOf(
                        R.id.uploadFragment,
                        R.id.plantsFragment,
                        R.id.historyFragment
                    ).contains(navController?.currentDestination?.id)
                ) {
                    backArrow.visibility = View.GONE
                    openMenu.visibility = View.VISIBLE
                } else {
                    backArrow.visibility = View.VISIBLE
                    openMenu.visibility = View.GONE
                }
            }
        }

        try {
            configCloudinary()
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
                navController?.navigate(R.id.uploadFragment)
            }

            R.id.history_item -> {
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.app_nav_host_fragment, HistoryFragment()).commit()
                navController?.navigate(R.id.historyFragment)
            }

            R.id.learning_item -> {
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.app_nav_host_fragment, PlantsFragment()).commit()
                navController?.navigate(R.id.plantsFragment)
            }

            R.id.log_out -> {
                lifecycleScope.launch {
                    HelperFunctions.logout(viewModel.preferencesRepository, this@AppActivity)
                }
            }

            R.id.exit -> {
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}