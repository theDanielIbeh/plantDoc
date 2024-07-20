package com.example.plantdoc.fragments.plants

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantdoc.data.entities.plant.Plant
import com.example.plantdoc.databinding.PlantsRecyclerItemBinding

object ProductComparator : DiffUtil.ItemCallback<Plant>() {
    override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return oldItem == newItem
    }
}

class PlantsPagingDataAdapter(
    private val context: Context,
    private val listener: HomeListener,
) :
    PagingDataAdapter<Plant, PlantsPagingDataAdapter.HomeViewHolder>(ProductComparator) {
    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val layoutBinding = PlantsRecyclerItemBinding.inflate(inflater, parent, false)

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
    inner class HomeViewHolder(private val binding: PlantsRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant?, position: Int) {
            try {
                if (plant == null) {
                    return
                }

                binding.plant = plant

                binding.apply {
                    viewDetailsButton.setOnClickListener {
                        listener.viewPlantDetails(plant)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface HomeListener {
        fun viewPlantDetails(plant: Plant)
    }
}