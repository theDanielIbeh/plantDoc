package com.example.plantdoctor.fragments.plantDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantdoctor.databinding.FragmentPlantDetailsBinding
import com.example.plantdoctor.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantDetailsFragment : Fragment() {

    private val viewModel: PlantDetailsViewModel by viewModels()
    private lateinit var binding: FragmentPlantDetailsBinding
    private val args: PlantDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantDetailsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.plant = args.plant

        viewModel.plant.let { plant ->
            plant.imageUrl?.let {
                loadImage(it)
            }
        }

        binding.btn.setOnClickListener {
            findNavController().navigate(PlantDetailsFragmentDirections.actionPlantDetailsFragmentToDiseasesFragment(viewModel.plant))
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
                .into(binding.plantImageView)
        } catch (e: Exception) {
            // Handle the error
            Log.d("Exception", e.message.toString())
            e.printStackTrace()
        }
    }
}