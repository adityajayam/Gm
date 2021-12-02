package com.demo.goldmansachs.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.demo.goldmansachs.R
import com.demo.goldmansachs.database.ApodDataEntity
import com.demo.goldmansachs.databinding.FavouriteListItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FavouriteListAdapter(
    private val context: Context,
    private val listener: (ApodDataEntity) -> Unit,
    private val removeFinished: (ArrayList<ApodDataEntity>) -> Unit
) : ListAdapter<ApodDataEntity, FavouriteListAdapter.ItemViewHolder>(DiffCallback) {
    private var deleteList: ArrayList<ApodDataEntity> = ArrayList()

    init {
        setHasStableIds(true)
    }

    class ItemViewHolder(val itemViewBinding: FavouriteListItemBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root)

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            FavouriteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        CoroutineScope(Dispatchers.IO).launch {
            val request = ImageRequest.Builder(context).data(item.url)
                .error(R.drawable.ic_broken_image)
                .placeholder(R.drawable.loading_animation)
                .target(holder.itemViewBinding.searchItemImage)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            context.imageLoader.execute(request)
        }
        holder.itemViewBinding.favouriteItem.setOnLongClickListener {
            showAllCheckBox()
            listener(item)
            true
        }
        holder.itemViewBinding.selected.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.e(TAG, position.toString() + isChecked)
            currentList[position].isSelected = isChecked
            deleteList.add(getItem(position))
        }
        if (item.isSelected) {
            holder.itemViewBinding.selected.visibility = View.VISIBLE
        } else {
            holder.itemViewBinding.selected.visibility = View.GONE
        }
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].url.hashCode().toLong()
    }

    fun removeAllSelected() {
        val current = currentList.toMutableList()
        current.removeAll(deleteList)
        submitList(current)
        hideAllCheckBox()
        removeFinished(deleteList)
    }

    private fun showAllCheckBox() {
        for (item in currentList) {
            item.isSelected = true
        }
        notifyDataSetChanged()
    }

    fun hideAllCheckBox() {
        for (item in currentList) {
            item.isSelected = false
        }
        notifyDataSetChanged()
    }

    fun getDeleteList(): ArrayList<ApodDataEntity> {
        return deleteList
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ApodDataEntity>() {
            override fun areItemsTheSame(
                oldItem: ApodDataEntity,
                newItem: ApodDataEntity
            ): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(
                oldItem: ApodDataEntity,
                newItem: ApodDataEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
        private const val TAG = "FavouriteListAdapter"
    }
}