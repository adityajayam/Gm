package com.demo.goldmansachs.ui.home

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.demo.goldmansachs.GoldMacSachsApplication
import com.demo.goldmansachs.R
import com.demo.goldmansachs.database.LatestApod
import com.demo.goldmansachs.databinding.FragmentHomeBinding
import com.demo.goldmansachs.viewmodel.MainActivityViewModel
import com.demo.goldmansachs.viewmodel.MainActivityViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory((activity?.application as GoldMacSachsApplication).repository)
    }
    private lateinit var binding: FragmentHomeBinding

    private var searchQuery: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (isOnline() && mainActivityViewModel.data.value == null) {
            getApodData(searchQuery)
        } else if (!isOnline()) {
            toggleVisibility(View.GONE)
            binding.errorText.visibility = View.GONE
            toggleProgressBar(View.VISIBLE)
            lifecycle.coroutineScope.launch {
                mainActivityViewModel.getLatestApod().collect {
                    toggleProgressBar(View.GONE)
                    if (it.isNotEmpty()) {
                        toggleVisibility(View.VISIBLE)
                        setApodDataOnUi(it[0])
                    } else {
                        mainActivityViewModel.errorData.value = R.string.no_network
                    }
                }
            }
        }

        mainActivityViewModel.errorData.observe(viewLifecycleOwner, {
            toggleVisibility(View.GONE)
            toggleProgressBar(View.GONE)
            val string = getString(it)
            val wordToSpan: Spannable = SpannableString(string)
            wordToSpan.setSpan(
                ForegroundColorSpan(Color.parseColor(HEX_COLOR)),
                string.indexOf(TRY_AGAIN),
                string.indexOf(TRY_AGAIN) + 9,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.errorText.text = wordToSpan
        })

        mainActivityViewModel.data.observe(viewLifecycleOwner, {
            mainActivityViewModel.saveLatestApod(LatestApod(it.url, it.date, it.explanation, it.title, mainActivityViewModel.selected))
            toggleVisibility(View.VISIBLE)
            toggleProgressBar(View.GONE)
            setApodDataOnUi(LatestApod(it.url, it.date, it.explanation, it.title, mainActivityViewModel.selected))
        })

        setFabButtonBackground(mainActivityViewModel.selected)
        mainActivityViewModel.image.observe(viewLifecycleOwner, {
            binding.image.setImageBitmap(it)
        })

        binding.favouriteActionButton.setOnClickListener {
            mainActivityViewModel.selected = !mainActivityViewModel.selected
            if (mainActivityViewModel.selected) {
                mainActivityViewModel.saveFavourite()
            } else {
                mainActivityViewModel.deleteFavourite()
            }
            setFabButtonBackground(mainActivityViewModel.selected)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchQuery = p0
                Log.e(TAG, searchQuery!!)
                mainActivityViewModel.selected = false
                setFabButtonBackground(mainActivityViewModel.selected)
                getApodData(searchQuery)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

        binding.errorText.setOnClickListener {
            if (isOnline()) {
                getApodData(searchQuery)
            } else {
                mainActivityViewModel.errorData.value = R.string.no_network
            }
        }
    }

    /**
     * Fetch Aopd data from the server
     */
    private fun getApodData(date: String?){
        toggleVisibility(View.GONE)
        binding.errorText.visibility = View.GONE
        toggleProgressBar(View.VISIBLE)
        if (isOnline()) {
            mainActivityViewModel.getApodData(date)
        } else {
            mainActivityViewModel.errorData.value = R.string.no_network
        }
    }

    /**
     * Set the Apod data to the UI for display
     */
    private fun setApodDataOnUi(it: LatestApod) {
        binding.date.text = it.date
        binding.title.text = it.title
        binding.description.text = it.explanation
        CoroutineScope(Dispatchers.IO).launch {
            val request = ImageRequest.Builder(requireContext()).data(it.url)
                .error(R.drawable.ic_broken_image)
                .placeholder(R.drawable.loading_animation)
                .target(binding.image)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            activity?.imageLoader?.execute(request)
        }
    }

    private fun toggleVisibility(visibility: Int) {
        binding.favouriteActionButton.visibility = visibility
        binding.image.visibility = visibility
        binding.description.visibility = visibility
        binding.date.visibility = visibility
        binding.title.visibility = visibility
        if (visibility == View.GONE) {
            binding.errorText.visibility = View.VISIBLE
        } else {
            binding.errorText.visibility = View.GONE
        }
    }

    private fun toggleProgressBar(visibility: Int){
        binding.progressBar.visibility = visibility
    }

    private fun setFabButtonBackground(isSelected: Boolean) {
        if (isSelected) {
            binding.favouriteActionButton.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.favourite_full
                )
            )
        } else {
            binding.favouriteActionButton.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.favorite_border
                )
            )
        }
    }

    /**
     * Check if phone is connected to wifi or network
     * @return true if connected
     */
    private fun isOnline(): Boolean {
        val connMgr =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    companion object {
        private const val TAG = "HomeFragment"
        private const val TRY_AGAIN = "Try again"
        private const val HEX_COLOR = "#FF3700B3"
    }
}