package com.example.plantdoc.fragments.historyDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantdoc.databinding.FragmentHistoryDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDetailsFragment : Fragment() {

    private val viewModel: HistoryDetailsViewModel by viewModels()
    private lateinit var binding: FragmentHistoryDetailsBinding
    private val args: HistoryDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryDetailsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.history = args.history

        viewModel.history.let { product ->
            product.localUrl.let {
                loadImage(it)
            }
        }

        return binding.root
    }

    private fun loadImage(imgSrc: String) =
        Glide.with(requireContext()).load(imgSrc)
            .into(binding.plantImageView)
}