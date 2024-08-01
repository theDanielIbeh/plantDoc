package com.example.plantdoctor.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.plantdoctor.MainActivity
import com.example.plantdoctor.R
import com.example.plantdoctor.data.PlantDocPreferencesRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone

object HelperFunctions {
    /**
     * Creates a file directory and a .nomedia file if it does not exist in external files dir
     */
    fun createMediaDirectory(applicationContext: Context, directory: String, root: String) {
        val imageDirectory =
            File(
                Objects.requireNonNull<File>(
                    applicationContext.getExternalFilesDir(
                        root
                    )
                ).absoluteFile,
                directory
            )
        if (!imageDirectory.isDirectory) {
            imageDirectory.mkdirs()
        }
        val noMediaFile = File(imageDirectory.absoluteFile, Constants.NO_MEDIA_FILE)

        if (!noMediaFile.exists()) {
            val outNoMedia = FileOutputStream(noMediaFile)
            outNoMedia.flush()
            outNoMedia.close()
        }
    }

    fun setMenu(
        activity: FragmentActivity,
        preferencesRepository: PlantDocPreferencesRepository
    ) {
        val isLoggedIn = preferencesRepository.getPreference(
            Boolean::class.java,
            Constants.IS_LOGGED_IN
        )
        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                isLoggedIn.observe(activity) {loggedIn ->
                    loggedIn?.let {
                        val menuItem= menu.findItem(R.id.history_item)
                        menuItem?.setVisible(loggedIn)
                    }
                }
                super.onPrepareMenu(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val navController =
                    (activity.supportFragmentManager.findFragmentById(R.id.app_nav_host_fragment) as NavHostFragment).navController
                // Handle the menu selection
                menuItem.setChecked(true)
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        navController.navigateUp()
//                        activity.onBackPressedDispatcher.onBackPressed()
                        true
                    }

//                    R.id.home_item -> {
//                       navController.popBackStack(R.id.uploadFragment, false)
//                        true
//                    }
//
//                    R.id.history_item -> {
//                        navController.navigate(R.id.historyFragment)
//                        navController.clearBackStack(R.id.historyFragment)
//                        true
//                    }
//
//                    R.id.learning_item -> {
//                        navController.navigate(R.id.plantsFragment)
//                        navController.clearBackStack(R.id.plantsFragment)
//                        true
//                    }

                    R.id.log_out -> {
                        activity.lifecycleScope.launch {
                            logout(preferencesRepository, activity)
                        }
                        true
                    }

                    else -> false
                }
            }
        }, activity, Lifecycle.State.RESUMED)
    }

    suspend fun logout(
        biblioHubPreferencesRepository: PlantDocPreferencesRepository,
        activity: FragmentActivity
    ) {
        biblioHubPreferencesRepository.clearDataStore()
        activity.finish()
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        activity.startActivity(intent)
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    fun displayDatePicker(
        editText: EditText,
        context: Context,
        minDate: Long? = null,
        maxDate: Long? = null,
    ) {
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]

        // date picker dialog
        val picker = DatePickerDialog(
            context,
            { _, selectedYear, monthOfYear, dayOfMonth ->
                // selected month usually starts from 0
                var mm = (monthOfYear + 1).toString()

                if (mm.length == 1) {
                    mm = "0$mm"
                }
                var dd = dayOfMonth.toString()
                if (dd.length == 1) {
                    dd = "0$dd"
                }
                val yearStr = "$dd-$mm-$selectedYear"
                val sdf = SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
                val date = sdf.parse(yearStr)
                val dateStr = date?.let { getDateString(Constants.DATE_FORMAT_FULL, it) }
                editText.setText(dateStr)
            }, year, month, day
        )
        if (minDate != null) {
            picker.datePicker.minDate = cldr.timeInMillis
        }
        if (maxDate != null) {
            picker.datePicker.maxDate = maxDate
        }
        picker.show()
    }

    /**
     * Get Date string in required format
     * Returns today's date string if no date is passed
     * @param keyDateFormat: options can be found in constants class
     */
    fun getDateString(
        keyDateFormat: String,
        date: Date = Date(),
        timeZone: String? = null
    ): String {
        val dateFormat: SimpleDateFormat = when (keyDateFormat) {
            Constants.DATE_FORMAT_HYPHEN_DMY -> {
                SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
            }

            Constants.DATE_FORMAT_FULL -> {
                SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault())
            }

            else -> {
                SimpleDateFormat(Constants.DATE_FORMAT_HYPHEN_DMY, Locale.getDefault())
            }
        }

        if (!timeZone.isNullOrEmpty()) {
            dateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        }
        return dateFormat.format(date)
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getRealPathFromURIAPI19(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver.query(
                        uri,
                        arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )
                    cursor!!.moveToNext()
                    val fileName = cursor.getString(0)
                    val path = Environment.getExternalStorageDirectory()
                        .toString() + "/Download/" + fileName
                    if (!TextUtils.isEmpty(path)) {
                        return path
                    }
                } finally {
                    cursor?.close()
                }
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:".toRegex(), "")
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads"),
                    java.lang.Long.valueOf(id)
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author Niks
     */
    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}