package com.example.plantdoc.fragments.upload

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
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
import com.example.plantdoc.databinding.FragmentUploadBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.min

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

        binding.apply {
            camera.setOnClickListener {
                if (checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PermissionChecker.PERMISSION_GRANTED
                ) {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, 3)
                } else {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
                }
            }
            gallery.setOnClickListener {
                if (checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PermissionChecker.PERMISSION_GRANTED
                ) {
                    // Permission is not granted, request the permission
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        100
                    )
                } else {
                    val cameraIntent =
                        Intent(
                            Intent.ACTION_GET_CONTENT,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
//                    cameraIntent.type = "image/*"
                    startActivityForResult(cameraIntent, 1)
                }
            }
        }
        return binding.root
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