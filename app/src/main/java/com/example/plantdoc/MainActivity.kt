package com.example.plantdoc

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.cloudinary.android.MediaManager
import com.example.plantdoc.fragments.login.LoginViewModel
import com.example.plantdoc.utils.Constants
import com.example.plantdoc.utils.HelperFunctions.createMediaDirectory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.nav_graph)

        createDirs()

        val isLoggedIn = viewModel.preferencesRepository.getPreference(
            Boolean::class.java,
            Constants.IS_LOGGED_IN
        )

        isLoggedIn.observe(this@MainActivity) {
            if (it == true) {
                val intent = Intent(this@MainActivity, AppActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

            } else {
                navGraph.setStartDestination(startDestId = R.id.loginFragment)
            }
            val navController = navHostFragment.navController
            navController.setGraph(navGraph, intent.extras)
        }

        if (!checkPermission())
            requestPermission()

//        viewModel.insertData()
        viewModel.downloadData()
    }

    /**
     * Create image directory if it does not exist
     */
    private fun createDirs() {
        try {
            createMediaDirectory(
                this,
                Constants.PREDICTION_PICTURE_DIR,
                Environment.DIRECTORY_PICTURES
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.INTERNET),
            PERMISSION_REQUEST_CODE
        )
    }
}