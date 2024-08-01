package com.example.plantdoctor.fragments.historyDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantdoctor.data.entities.history.History
import com.example.plantdoctor.databinding.FragmentHistoryDetailsBinding
import com.example.plantdoctor.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.history = args.history
        lifecycleScope.launch {
            viewModel.disease = withContext(Dispatchers.IO) {
                viewModel.getDisease(viewModel.history.predictedClassId)
            }
            viewModel.disease?.let { disease ->
                binding.viewInfo.setOnClickListener {
                    findNavController().navigate(
                        HistoryDetailsFragmentDirections.actionHistoryDetailsFragmentToDiseaseDetailsFragment(
                            disease = disease
                        )
                    )
                }
            }
        }


        loadImage(viewModel.history)

        return binding.root
    }

    private fun loadImage(history: History) {
        if (File(history.localUrl).exists()) {
            Glide.with(requireContext()).load(history.localUrl)
                .into(binding.historyImageView)
        } else {
            if (!HelperFunctions.isInternetAvailable(requireContext())) {
                Toast.makeText(
                    context,
                    "Cannot load image. No internet connection.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            try {
                Glide.with(requireContext()).load(history.remoteUrl)
                    .into(binding.historyImageView)
            } catch (e: Exception) {
                // Handle the error
                Log.d("Exception", e.message.toString())
                e.printStackTrace()
            }
        }
    }
}