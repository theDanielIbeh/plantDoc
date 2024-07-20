package com.example.plantdoc.fragments.diseaseDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantdoc.databinding.FragmentDiseaseDetailsBinding
import com.example.plantdoc.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiseaseDetailsFragment : Fragment() {

    private val viewModel: DiseaseDetailsViewModel by viewModels()
    private lateinit var binding: FragmentDiseaseDetailsBinding
    private val args: DiseaseDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiseaseDetailsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.disease = args.disease

        viewModel.disease.let { disease ->
            disease.imageUrl?.let {
                loadImage(it)
            }
        }

        return binding.root
    }

    private fun loadImage(imgSrc: String) {
        if (!HelperFunctions.isInternetAvailable(requireContext())) {
            Toast.makeText(context, "Cannot load image. No internet connection.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            Glide.with(requireContext()).load(imgSrc)
                .into(binding.diseaseImageView)
        } catch (e: Exception) {
            // Handle the error
            Log.d("Exception", e.message.toString())
            e.printStackTrace()
        }
    }
}