package com.example.plantdoctor.fragments.history

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.plantdoctor.data.entities.disease.Disease
import com.example.plantdoctor.data.entities.history.History
import com.example.plantdoctor.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.system.exitProcess

@AndroidEntryPoint
class HistoryFragment : Fragment(), HistoryPagingDataAdapter.HistoryListener {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()

    private var adapter: HistoryPagingDataAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(
            inflater,
            container,
            false
        )
        initCompulsoryVariables()



//        viewModel.isLoggedIn.observe(viewLifecycleOwner) {
//            if (it == true) {
//                setOnBackPressedCallback()
//            } else  {
//                findNavController().clearBackStack(this.id)
//            }
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBinding()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initCompulsoryVariables() {
        lifecycleScope.launch {
            viewModel.getTransactionsLive()

            initializeRecycler()
        }
    }

    private fun setBinding() {
        setupDateFilter()
    }

    private fun initializeRecycler() {
        adapter = HistoryPagingDataAdapter(
            requireContext(),
            this
        )
        viewModel.getTransactionsLive().observe(viewLifecycleOwner) {
            adapter?.submitData(lifecycle, it)
        }
        val vLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        binding.rvDepositTransferHistory.layoutManager = vLayoutManager
        binding.rvDepositTransferHistory.itemAnimator = DefaultItemAnimator()
        binding.rvDepositTransferHistory.adapter = adapter
        (binding.rvDepositTransferHistory.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }

    private fun filterTransactionByDate(date: String) {
        viewModel.getAllTransactionsByDateLive(date).observe(viewLifecycleOwner) {
            adapter?.submitData(lifecycle, it)
        }
    }

    private fun setupDateFilter() {
        binding.acvDate.setOnClickListener {
            triggerDatePicker()
        }

        binding.ivDateDropDown.setOnClickListener {
            triggerDatePicker()
        }

        binding.ivDateCancel.setOnClickListener {
            binding.acvDate.setText("")
            binding.shouldCancelShow = false
        }
    }

    private fun triggerDatePicker() {
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]

        // date picker dialog
        val picker = DatePickerDialog(
            requireContext(),
            { _, selectedYear, monthOfYear, dayOfMonth ->
                // selected month usually starts from 0
                var mm = (monthOfYear + 1).toString()

                if (mm.length == 1) {
                    mm = "0$mm"
                }
                var dd = dayOfMonth.toString()
                if (dd.length == 1) {
                    dd = "0$dd"
                }
                val yearStr = "$selectedYear-$mm-$dd"
                binding.acvDate.setText(yearStr)
            }, year, month, day
        )
        picker.datePicker.maxDate = Calendar.getInstance().timeInMillis

        picker.show()

        picker.setOnDismissListener {
            filterTransactionByDate(binding.acvDate.text.toString())
        }
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

    override suspend fun getDisease(diseaseId: Int): Disease? =
        viewModel.getDisease(diseaseId)

    override fun viewHistoryDetails(history: History) {
        findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToHistoryDetailsFragment(history))
    }
}
