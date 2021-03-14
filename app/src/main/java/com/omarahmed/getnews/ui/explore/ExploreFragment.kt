package com.omarahmed.getnews.ui.explore

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.omarahmed.getnews.R
import com.omarahmed.getnews.databinding.FragmentExploreBinding
import com.omarahmed.getnews.models.ExploreHeaderModel
import com.omarahmed.getnews.shared.ShareClickListener
import com.omarahmed.getnews.util.Constants.API_KEY
import com.omarahmed.getnews.util.NetworkResult
import com.omarahmed.getnews.viewmodels.ExploreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(), HeaderAdapter.HeaderAdapterInterface {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var exploreAdapter : ExploreAdapter
    private lateinit var headerAdapter: HeaderAdapter
    private val exploreViewModel: ExploreViewModel by viewModels()
    private lateinit var exploreHeaderModel: ArrayList<ExploreHeaderModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentExploreBinding.inflate(layoutInflater)
        exploreHeaderModel = arrayListOf(
                ExploreHeaderModel(R.drawable.ic__business,"BUSINESS"),
                ExploreHeaderModel(R.drawable.ic__tech,"TECHNOLOGY"),
                ExploreHeaderModel(R.drawable.ic__health,"HEALTH"),
                ExploreHeaderModel(R.drawable.ic__science,"SCIENCE"),
                ExploreHeaderModel(R.drawable.ic__entertainment,"ENTERTAINMENT"),
                ExploreHeaderModel(R.drawable.ic__sport,"SPORTS"),
        )
        headerAdapter = HeaderAdapter(exploreHeaderModel,this)
        exploreAdapter = ExploreAdapter(ShareClickListener { link ->
            shareNewsLink(link)
        })

        getExploreNews("business")
        binding.rvExplore.adapter = exploreAdapter
        binding.gvExplore.adapter = headerAdapter
        return binding.root
    }

    private fun shareNewsLink(link: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,link)
            type = "text/plain"
        }
        startActivity(shareIntent)
    }

    private fun getExploreNews(category: String){
        lifecycleScope.launchWhenStarted {
            exploreViewModel.getExploreNews(API_KEY,category)
            exploreViewModel.exploreNewsResponse.observe(viewLifecycleOwner){ response ->
                when(response){
                    is NetworkResult.Success -> {
                        binding.rvExplore.isVisible = true
                        binding.pbExplore.isVisible = false
                        binding.ivExploreError.isVisible = false
                        binding.tvExploreError.isVisible = false
                        response.data?.let {
                            exploreAdapter.submitList(it.articles)
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.pbExplore.isVisible = false
                        binding.ivExploreError.isVisible = true
                        binding.tvExploreError.apply {
                            isVisible = true
                            text = response.message
                        }
                        binding.rvExplore.isVisible = false
                    }

                    is  NetworkResult.Loading ->{
                        binding.pbExplore.isVisible = true
                        binding.ivExploreError.isVisible = false
                        binding.rvExplore.isVisible = false
                        binding.tvExploreError.isVisible = false


                    }
                }
            }
        }

    }

    override fun getCategory(position: Int) {
        val category = exploreHeaderModel[position].title
        getExploreNews(category)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}