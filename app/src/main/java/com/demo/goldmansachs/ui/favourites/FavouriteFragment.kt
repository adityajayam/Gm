package com.demo.goldmansachs.ui.favourites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.goldmansachs.GoldMacSachsApplication
import com.demo.goldmansachs.adapters.FavouriteListAdapter
import com.demo.goldmansachs.databinding.FragmentFavouriteBinding
import com.demo.goldmansachs.viewmodel.MainActivityViewModel
import com.demo.goldmansachs.viewmodel.MainActivityViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null

    private val binding get() = _binding!!

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory((activity?.application as GoldMacSachsApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = FavouriteListAdapter(requireContext(), {
            binding.delete.visibility = View.VISIBLE
            binding.cancel.visibility = View.VISIBLE
        }, {
            binding.delete.visibility = View.GONE
            binding.cancel.visibility = View.GONE
        })
        binding.delete.setOnClickListener {
            try {
                mainActivityViewModel.deleteFavourites(adapter.getDeleteList())
                adapter.removeAllSelected()
            } catch (exp: Exception) {
                Log.e(TAG, "Incomplete delete, so load items again")
                loadFavourites(adapter)
            }
        }
        loadFavourites(adapter)
        binding.cancel.setOnClickListener {
            adapter.hideAllCheckBox()
            binding.cancel.visibility = View.GONE
            binding.delete.visibility = View.GONE
        }
        binding.searchList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.searchList.adapter = adapter
    }

    private fun loadFavourites(adapter: FavouriteListAdapter) {
        lifecycle.coroutineScope.launch {
            mainActivityViewModel.getFavourites().collect {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "FavouriteFragment"
    }
}