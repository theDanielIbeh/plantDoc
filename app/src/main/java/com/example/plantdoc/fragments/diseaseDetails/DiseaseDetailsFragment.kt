package com.example.plantdoc.fragments.diseaseDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantdoc.databinding.FragmentDiseaseDetailsBinding
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
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.disease = args.disease

        return binding.root
    }

    private fun loadImage(imgSrc: String) =
        Glide.with(requireContext()).load(imgSrc)
            .into(binding.plantImageView)
}