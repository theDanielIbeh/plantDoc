package com.example.plantdoctor.fragments.diseases

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantdoctor.data.entities.disease.Disease
import com.example.plantdoctor.databinding.DiseasesRecyclerItemBinding

object ProductComparator : DiffUtil.ItemCallback<Disease>() {
    override fun areItemsTheSame(oldItem: Disease, newItem: Disease): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Disease, newItem: Disease): Boolean {
        return oldItem == newItem
    }
}

class DiseasesPagingDataAdapter(
    private val context: Context,
    private val listener: HomeListener,
) :
    PagingDataAdapter<Disease, DiseasesPagingDataAdapter.HomeViewHolder>(ProductComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = DiseasesRecyclerItemBinding.inflate(inflater, parent, false)

        return HomeViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = getItem(position)
        // Note that item may be null. ViewHolder must support binding a
        // null item as a placeholder.
        holder.bind(item, position)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a Product object.
    inner class HomeViewHolder(private val binding: DiseasesRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(disease: Disease?, position: Int) {
            try {
                if (disease == null) {
                    return
                }

                binding.disease = disease

                binding.apply {
                    viewDetailsButton.setOnClickListener {
                        listener.viewDiseaseDetails(disease)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        fun viewDiseaseDetails(disease: Disease)
    }
}