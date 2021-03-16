package com.omarahmed.getnews.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.omarahmed.getnews.R
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import com.omarahmed.getnews.databinding.FragmentHomeBinding
import com.omarahmed.getnews.databinding.LatestNewsHeaderBinding
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.shared.ShareClickListener
import com.omarahmed.getnews.viewmodels.SavedViewModel
import com.omarahmed.getnews.util.Constants.API_KEY
import com.omarahmed.getnews.util.NetworkResult
import com.omarahmed.getnews.util.observeOnce
import com.omarahmed.getnews.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable
import java.util.*
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeAdapter.HomeAdapterInterface {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewPagerAdapter by lazy { ViewPagerAdapter() }
    private lateinit var homeAdapter: HomeAdapter
    private val homeViewModel: HomeViewModel by viewModels()
    private val savedViewModel: SavedViewModel by viewModels()
    private val mHandler = Handler(Looper.myLooper()!!)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        homeAdapter = HomeAdapter(this, requireActivity(), ShareClickListener { link ->
            shareNewsLink(link)
        })

        getLatestNews()
        setupRecyclerView()


        binding.homeViewModel = homeViewModel
        binding.lifecycleOwner = this
        binding.ivSearchIcon.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        return binding.root
    }

    private fun getForYouNews(headerBinding: LatestNewsHeaderBinding) {
        lifecycleScope.launchWhenStarted {
            homeViewModel.readForYouNews.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    Log.d("homeFragment", "readForYouNewsFromDatabase!!")
                    viewPagerAdapter.submitList(database[0].newsResponse.articles)
                } else {
                    Log.d("homeFragment", "readForYouNewsFromApi!!")
                    readForYouNewsFromApi(headerBinding)
                }
            }
        }
    }

    private fun getLatestNews() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.readLatestNews.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    homeAdapter.addHeaderAndSubmitList(database[0].newsResponse.articles)
                    Log.d("homeFragment", "readLatestNewsFromDatabase!!")

                } else {
                    readLatestNewsFromApi()
                    Log.d("homeFragment", "readLatestNewsFromApi!!")
                }
            }
        }
    }

    private fun readForYouNewsFromApi(headerBinding: LatestNewsHeaderBinding) {
        lifecycleScope.launchWhenStarted {
            homeViewModel.getForYouNews(API_KEY, getCountry())
            homeViewModel.forYouResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        headerBinding.ivForYouNotFound.isVisible = false
                        headerBinding.tvForYouNotFound.isVisible = false
                        response.data?.let {
                            viewPagerAdapter.submitList(it.articles)
                        }
                    }
                    is NetworkResult.Error -> {
//                        readForYouNewsFromCache()
                        Log.d("homeFragment", response.message.toString())
                        homeViewModel.readForYouNews.observe(viewLifecycleOwner){
                            if (response.message.toString().contains("Not found") && it.isEmpty()) {
                                headerBinding.ivForYouNotFound.isVisible = true
                                headerBinding.tvForYouNotFound.isVisible = true
                                Log.d("not found", response.message.toString())
                            }
                        }

                    }
                    is NetworkResult.Loading -> Unit
                }
            }
        }
    }

    private fun readLatestNewsFromApi() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.getLatestNews(API_KEY)
            homeViewModel.latestNewsResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        binding.refreshLayout.isRefreshing = false
                        response.data?.let {
                            homeAdapter.addHeaderAndSubmitList(it.articles)
                        }
                    }
                    is NetworkResult.Error -> {
                        readLatestNewsFromCache()
                        binding.refreshLayout.isRefreshing = false
                        Log.d("homeFragment", response.message.toString())

                    }
                    is NetworkResult.Loading -> {
                        binding.refreshLayout.isRefreshing = true
                    }
                }
            }
        }
    }

    private fun readLatestNewsFromCache() {
        homeViewModel.readLatestNews.observe(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()) {
                homeAdapter.addHeaderAndSubmitList(database[0].newsResponse.articles)
            }
        }
    }

    private fun readForYouNewsFromCache() {
        homeViewModel.readForYouNews.observe(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()) {
                viewPagerAdapter.submitList(database[0].newsResponse.articles)
            }
        }
    }

    private fun setupRecyclerView() {
        val manager = GridLayoutManager(requireContext(), 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> 2
                    else -> 1
                }
            }
        }
        binding.rvLatestNews.apply {
            adapter = homeAdapter
            binding.rvLatestNews.layoutManager = manager
        }
    }

    private fun setupRefreshLayout(headerBinding: LatestNewsHeaderBinding) {
        binding.refreshLayout.setOnRefreshListener {
            readLatestNewsFromApi()
            readForYouNewsFromApi(headerBinding)
        }
    }

    private fun shareNewsLink(link: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        startActivity(shareIntent)
    }

    private fun getCountry(): String {
        val telephonyManager = activity?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCode = telephonyManager.networkCountryIso.toUpperCase(Locale.ROOT)
        val countryName = Locale("", countryCode).displayName
        Log.d("countryCode", countryName)
        return countryName
    }

    override fun onSavedClick(article: Article, imageView: ImageView) {
        val savedNewsEntity = SavedNewsEntity(0, article)
        savedViewModel.insertSavedNews(savedNewsEntity)
        imageView.setImageResource(R.drawable.ic_bookmark_saved)
        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
    }

    override fun onUnSavedClick(newsId: Int, article: Article, imageView: ImageView) {
        val savedNewsEntity = SavedNewsEntity(newsId, article)
        savedViewModel.deleteSavedNews(savedNewsEntity)
        imageView.setImageResource(R.drawable.ic_bookmark_border)
        Toast.makeText(requireContext(), "Removed", Toast.LENGTH_SHORT).show()
    }


    override fun setupHeader(headerBinding: LatestNewsHeaderBinding) {
        val compositePaTransformer = CompositePageTransformer()
        compositePaTransformer.apply {
            addTransformer(MarginPageTransformer(30))
            addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.14f
            }
        }
        headerBinding.vpForYou.apply {
            adapter = viewPagerAdapter
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(compositePaTransformer)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mHandler.removeMessages(0)
                    val runnable = Runnable {
                        currentItem = ++currentItem
                    }
                    val infinite = Runnable {
                        currentItem = 0
                    }
                    mHandler.postDelayed(runnable, 5000)
                    if (position == viewPagerAdapter.currentList.size - 1) {
                        mHandler.removeCallbacks(runnable)
                        mHandler.postDelayed(infinite, 5000)
                    }
                }
            })

        }
        getForYouNews(headerBinding)
        setupRefreshLayout(headerBinding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}