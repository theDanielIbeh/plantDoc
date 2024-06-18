package com.example.plantdoc.fragments.plantDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantdoc.databinding.FragmentPlantDetailsBinding
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
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.plant = args.plant

        viewModel.plant.let { product ->
            product.imageUrl?.let {
                loadImage(it)
            }
        }

        return binding.root
    }

    private fun loadImage(imgSrc: String) =
        Glide.with(requireContext()).load(imgSrc)
            .into(binding.plantImageView)
}