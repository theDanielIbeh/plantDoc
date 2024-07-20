package com.example.plantdoc.fragments.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantdoc.data.entities.disease.Disease
import com.example.plantdoc.data.entities.history.History
import com.example.plantdoc.databinding.HistoryRecyclerItemBinding
import com.example.plantdoc.utils.Constants
import com.example.plantdoc.utils.HelperFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

val TRANSACTION_MODEL_COMPARATOR = object : DiffUtil.ItemCallback<History>() {
    override fun areItemsTheSame(
        oldItem: History,
        newItem: History
    ): Boolean =
        // User ID serves as unique ID
        oldItem.date == newItem.date

    override fun areContentsTheSame(
        oldItem: History,
        newItem: History
    ): Boolean =
        // Compare full contents (note: Java users should call .equals())
        oldItem == newItem
}

class HistoryPagingDataAdapter(
    val mContext: Context,
    val listener: HistoryListener
) : PagingDataAdapter<History, HistoryPagingDataAdapter.ViewHolder>(
    TRANSACTION_MODEL_COMPARATOR
) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history: History? = getItem(position)
        holder.bind(history, position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding =
            HistoryRecyclerItemBinding.inflate(
                inflater,
                parent,
                false
            )
        return ViewHolder(layoutBinding)
    }

    // With this, every event location has a specific view-type
    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(val binding: HistoryRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: History?, position: Int) {
            try {
                if (history == null) {
                    return
                }

                CoroutineScope(Dispatchers.Main).launch {
                    val disease =
                        withContext(Dispatchers.IO) { listener.getDisease(history.predictedClassId) }
                    with(binding) {
                        tvDateValue.text = history.date
                        tvPredictionValue.text = disease?.name ?: ""
                        viewDetailsButton.setOnClickListener { listener.viewHistoryDetails(history) }
                    }
                    binding.executePendingBindings()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HistoryListener {
        suspend fun getDisease(diseaseId: Int): Disease?
        fun viewHistoryDetails(history: History)
    }
}
