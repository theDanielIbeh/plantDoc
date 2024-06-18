package com.example.plantdoc.fragments.diseases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.plantdoc.R
import com.example.plantdoc.data.entities.disease.Disease
import com.example.plantdoc.data.entities.plant.Plant
import com.example.plantdoc.databinding.FragmentDiseasesBinding
import com.example.plantdoc.fragments.plantDetails.PlantDetailsFragmentArgs
import com.example.plantdoc.utils.BaseSearchableFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class DiseasesFragment : BaseSearchableFragment<Plant>(),
    DiseasesPagingDataAdapter.HomeListener {

    companion object {
        private val TAG = DiseasesFragment::getTag.name
    }

    private val viewModel: DiseasesViewModel by viewModels()
    private lateinit var binding: FragmentDiseasesBinding
    private lateinit var adapter: DiseasesPagingDataAdapter
    private val args: DiseasesFragmentArgs by navArgs()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentDiseasesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.plant = args.plant

        setOnBackPressedCallback()
    }

    override fun initCompulsoryVariables() {
        viewModelFilterText = viewModel.filterText
        searchCallback = { it -> viewModel.search(it) }
    }

    override fun returnBindingRoot(): View {
        return binding.root
    }

    override fun setBinding() {}

    override fun initRecycler() {
        adapter = DiseasesPagingDataAdapter(requireContext(), this)
        lifecycleScope.launch {
            viewModel.diseases.observe(viewLifecycleOwner) { pagingData ->
                // submitData suspends until loading this generation of data stops
                // so be sure to use collectLatest {} when presenting a Flow<PagingData>
                    adapter.submitData(lifecycle, pagingData)
                lifecycleScope.launch {
                    adapter.loadStateFlow.map { it.refresh }
                        .distinctUntilChanged()
                        .collect {
                            if (it is LoadState.NotLoading) {
                                setSearchResult(adapter.itemCount)
                            }
                        }
                }
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }

    override fun setSearchResult(listSize: Int) {
        binding.noOfResultsTextview.visibility =
            View.VISIBLE
        val size = if (listSize >= 30) "$listSize+" else listSize.toString()
        binding.noOfResultsTextview.text = getString(R.string.y_results, size)
    }

    private fun setOnBackPressedCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                    exitProcess(0)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun viewDiseaseDetails(disease: Disease) {
//        findNavController().navigate(
//            PlantsFragment.actionAdminOrdersFragmentToAdminOrderDetailsFragment(
//                plant = plant
//            )
//        )
    }
}