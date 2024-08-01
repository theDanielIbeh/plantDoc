package com.example.plantdoctor.fragments.diseases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.plantdoctor.R
import com.example.plantdoctor.data.entities.disease.Disease
import com.example.plantdoctor.data.entities.plant.Plant
import com.example.plantdoctor.databinding.FragmentDiseasesBinding
import com.example.plantdoctor.utils.BaseSearchableFragment
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
    override var viewModelFilterText: String? = null
    override var searchCallback: ((String) -> Unit)? = null
    override var searchButton: ImageButton? = null
    override var searchText: TextInputEditText? = null

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentDiseasesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel

        searchButton = binding.imageButtonStopSearch
        searchText = binding.etSearch

        viewModel.plant = args.plant
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
            viewModel.getDiseases().observe(viewLifecycleOwner) { pagingData ->
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

    override fun viewDiseaseDetails(disease: Disease) {
        findNavController().navigate(
            DiseasesFragmentDirections.actionDiseasesFragmentToDiseaseDetailsFragment(
                disease = disease
            )
        )
    }
}