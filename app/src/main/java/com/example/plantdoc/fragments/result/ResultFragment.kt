package com.example.plantdoc.fragments.result

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.plantdoc.MainActivity
import com.example.plantdoc.data.entities.disease.Disease
import com.example.plantdoc.databinding.FragmentResultBinding
import com.example.plantdoc.ml.TomatoModel
import com.example.plantdoc.utils.Constants
import com.example.plantdoc.utils.HelperFunctions.getRealPathFromURIAPI19
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private val arguments: ResultFragmentArgs by navArgs()
    private val viewModel: ResultViewModel by viewModels()
    private var imageSize: Int = 224
    private var currentPhotoPath: String? = null
    private var config: HashMap<String?, String?> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentResultBinding.inflate(inflater, container, false)

//        configCloudinary()

        val uri = Uri.parse(arguments.uri)
        setImage(uri)

        binding.apply {
            predict.setOnClickListener { findNavController().navigateUp() }
        }

        return binding.root
    }
    private fun configCloudinary() {
        config["cloud_name"] = "dkwvmnamr"
        config["api_key"] = "868793386732371";
        config["api_secret"] = "FXeMjkU_OoJX-7tP5zxnNqOZz_c";
        MediaManager.init(requireContext(), config);
    }

    private fun setImage(uri: Uri) {
        lifecycleScope.launch {

            val filePath = getRealPathFromURIAPI19(requireContext(), uri)
            Log.d("MainActivity", "RealPath: $filePath")

            var image: Bitmap? = null
            try {
                image = BitmapFactory.decodeFile(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            binding.imageView.setImageBitmap(image)
            val photoFile: File? = try {
                createImageFile(requireContext())
            } catch (ex: IOException) {
                null
            }
            Log.d("MainActivity", photoFile?.absolutePath.toString())
            photoFile?.also {
                File(filePath).let { sourceFile ->
                    sourceFile.copyTo(File(currentPhotoPath))
                    compressImage(currentPhotoPath!!)
                    Log.d("MainActivity", "Here")
                    File(currentPhotoPath).absolutePath?.let { uploadToCloudinary(it) }
                }
                viewModel.resultModel.value.localUrl = it.absolutePath
            }

            image = Bitmap.createScaledBitmap(image!!, imageSize, imageSize, true)
            val predictedIndex = classifyImage(image!!)
            viewModel.disease =
                withContext(Dispatchers.IO) { getDiseaseByIndex(predictedIndex) }
            binding.result.text = viewModel.disease?.name
            viewModel.disease?.let { viewModel.resultModel.value.predictedClass = it.id }

            viewModel.loggedInUser.observe(viewLifecycleOwner) {
                it?.let { viewModel.insertHistory(userId = it.id) }
            }
        }
    }

    private suspend fun getDiseaseByIndex(idx: Int): Disease? {
        return viewModel.getDiseaseByIndex(idx)
    }

    private fun classifyImage(image: Bitmap): Int {
        return try {
            val model: TomatoModel = TomatoModel.newInstance(requireContext())

            // Creates inputs for reference.
            val inputFeature0: TensorBuffer =
                TensorBuffer.createFixedSize(
                    intArrayOf(1, imageSize, imageSize, 3),
                    DataType.FLOAT32
                )
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++] // RGB
                    byteBuffer.putFloat(((`val` shr 16) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat(((`val` shr 8) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs: TomatoModel.Outputs = model.process(inputFeature0)
            Log.d("ModelOutput", outputs.outputFeature0AsTensorBuffer.toString())
            val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer

            val confidences: FloatArray = outputFeature0.floatArray
            // find the index of the class with the biggest confidence.
            var maxPos = 0
            var maxConfidence = 0f

            for (i in confidences.indices) {
                Log.d("Each Confidence", confidences[i].toString())
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            val classes = arrayOf(
//                "Apple___Apple_scab",
//                "Apple___Black_rot",
//                "Apple___Cedar_apple_rust",
//                "Apple___healthy",
//                "Blueberry___healthy",
//                "Cherry___Powdery_mildew",
//                "Cherry___healthy",
//                "Corn___Cercospora_leaf_spot Gray_leaf_spot",
//                "Corn___Common_rust",
//                "Corn___Northern_Leaf_Blight",
//                "Corn___healthy",
//                "Grape___Black_rot",
//                "Grape___Esca_(Black_Measles)",
//                "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)",
//                "Grape___healthy",
//                "Orange___Haunglongbing_(Citrus_greening)",
//                "Peach___Bacterial_spot",
//                "Peach___healthy",
//                "Pepper,_bell___Bacterial_spot",
//                "Pepper,_bell___healthy",
//                "Potato___Early_blight",
//                "Potato___Late_blight",
//                "Potato___healthy",
//                "Raspberry___healthy",
//                "Soybean___healthy",
//                "Squash___Powdery_mildew",
//                "Strawberry___Leaf_scorch",
//                "Strawberry___healthy",

                "Tomato___Bacterial_spot",
                "Tomato___Early_blight",
                "Tomato___Late_blight",
                "Tomato___Leaf_Mold",
                "Tomato___Septoria_leaf_spot",
                "Tomato___Spider_mites Two-spotted_spider_mite",
                "Tomato___Target_Spot",
                "Tomato___Tomato_Yellow_Leaf_Curl_Virus",
                "Tomato___Tomato_mosaic_virus",
                "Tomato___healthy",

//                "Bacterial Spot",
//                "Early Blight",
//                "Healthy",
//                "Late Blight",
//                "Leaf Mold",
//                "Leaf_Miner",
//                "Mosaic Virus",
//                "Septoria",
//                "Spider Mites",
//                "Yellow Leaf Curl Virus"
            )

//            binding.result.text = classes[maxPos]

            Log.d("Confidences", confidences.toString())
            Log.d("Confidences", confidences.size.toString())
            Log.d("MaxConfidence", maxConfidence.toString())
            Log.d("maxPos", maxPos.toString())
            Log.d("Class", binding.result.text.toString())

            // Releases model resources if no longer used.
            model.close()
            maxPos
        } catch (e: IOException) {
            // TODO Handle the exception
            -1
        }
    }

    private fun getRealPathFromUri(imageUri: Uri, activity: Activity): String? {
        val path: String?
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(imageUri, filePathColumn, null, null, null)

        if (cursor == null) {
            path = imageUri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(filePathColumn[0])
            path = cursor.getString(idx)
        }
        cursor?.close()
        return path
    }

    private fun uploadToCloudinary(filePath: String) {
        Log.d("A", "sign up uploadToCloudinary- ")
        MediaManager.get().upload(filePath).callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                Log.d("MainActivity", "start")
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                Log.d("MainActivity", "Uploading... ")
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                Log.d("MainActivity", "image URL: " + resultData["url"].toString())
                viewModel.updateHistory(resultData["url"].toString())
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                Log.e("MainActivity", "Error " + error.description)
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                Log.d("MainActivity", "Reschedule " + error.description)
            }
        }).dispatch()
    }

    private fun createImageFile(context: Context): File {
        val storageDir: File? =
            context.getExternalFilesDir("${Environment.DIRECTORY_PICTURES}/${Constants.PREDICTION_PICTURE_DIR}")
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_SPREAD, Locale.getDefault())
        val currentDate = Date()
        val prefix = "prediction_${formatter.format(currentDate)}"
        return File(
            storageDir,
            "$prefix.jpg",
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun compressImage(imageUri: String): String? {
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(imageUri, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f * 2
        val maxWidth = 612.0f * 2
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(imageUri, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(imageUri!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90F)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180F)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270F)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename = currentPhotoPath
        try {
            out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }
}