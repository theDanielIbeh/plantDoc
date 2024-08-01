package com.example.plantdoctor.fragments.upload

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.plantdoctor.databinding.FragmentUploadBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [UploadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class UploadFragment : Fragment() {
    private lateinit var binding: FragmentUploadBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUploadBinding.inflate(inflater, container, false)

        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES), 100)
        } else if (checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
            != PermissionChecker.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request the permission
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES), 100)
        }

        binding.apply {
            gallery.setOnClickListener {
                takePhotoOrSelectFile()
            }
        }
        return binding.root
    }

    private fun takePhotoOrSelectFile() {
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
        } else if (checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
            != PermissionChecker.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request the permission
            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 100)
        } else {
            val choice =
                arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            myAlertDialog.setTitle("Select Image/File")
            myAlertDialog.setItems(
                choice
            ) { dialog, item ->
                when {
                    // Select "Take Photo" to take a photo
                    choice[item] == "Take Photo" -> {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(cameraIntent, 3)
                    }
                    // Select "Choose from Gallery" to pick image from gallery
                    choice[item] == "Choose from Gallery" -> {
                        val galleryIntent =
                            Intent(
                                Intent.ACTION_GET_CONTENT,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                        startActivityForResult(galleryIntent, 1)
                    }
                    // Select "Cancel" to cancel the task
                    choice[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            myAlertDialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 3) {
                val image = data!!.data
//                var image = data!!.extras!!["data"] as Bitmap?
//                val dimension =
//                    min(image!!.width.toDouble(), image.height.toDouble()).toInt()
//                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
//                imageView!!.setImageBitmap(image)
//
//                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
//                classifyImage(image)
                findNavController().navigate(
                    UploadFragmentDirections.actionUploadFragmentToResultFragment(
                        image.toString()
                    )
                )
            } else {
                val dat = data!!.data
                findNavController().navigate(
                    UploadFragmentDirections.actionUploadFragmentToResultFragment(
                        dat.toString()
                    )
                )
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}