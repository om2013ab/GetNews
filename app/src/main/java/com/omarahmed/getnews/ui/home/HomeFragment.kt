package com.omarahmed.getnews.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.omarahmed.getnews.ui.saved.SavedViewModel
import com.omarahmed.getnews.util.Constants.API_KEY
import com.omarahmed.getnews.util.NetworkResult
import com.omarahmed.getnews.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeAdapter.HomeAdapterInterface {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewPagerAdapter by lazy { ViewPagerAdapter() }
    private val homeAdapter by lazy { HomeAdapter(this,requireActivity()) }
    private val homeViewModel: HomeViewModel by viewModels()
    private val savedViewModel: SavedViewModel by viewModels()
    private val mHandler = Handler(Looper.myLooper()!!)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        getForYouNews()
        getLatestNews()
        setupRecyclerView()
        setupRefreshLayout()

        binding.homeViewModel = homeViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun getForYouNews() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.readForYouNews.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    Log.d("homeFragment", "readForYouNewsFromDatabase!!")
                    viewPagerAdapter.submitList(database[0].newsResponse.articles)
                } else {
                    Log.d("homeFragment", "readForYouNewsFromApi!!")
                    readForYouNewsFromApi()
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

    private fun readForYouNewsFromApi() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.getForYouNews(API_KEY)
            homeViewModel.forYouResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        response.data?.let {
                            viewPagerAdapter.submitList(it.articles)
                        }
                    }
                    is NetworkResult.Error -> {
                        readForYouNewsFromCache()
                        Log.d("homeFragment", response.message.toString())
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

    private fun setupRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            readLatestNewsFromApi()
            readForYouNewsFromApi()
        }
    }

    override fun shareNewsLink(article: Article) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,article.url)
            type = "text/plain"
        }
        startActivity(shareIntent)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}