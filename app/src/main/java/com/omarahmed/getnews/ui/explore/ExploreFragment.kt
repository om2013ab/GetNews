package com.omarahmed.getnews.ui.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.omarahmed.getnews.R
import com.omarahmed.getnews.databinding.ExploreHeaderLayoutBinding
import com.omarahmed.getnews.databinding.FragmentExploreBinding
import com.omarahmed.getnews.util.Constants.API_KEY
import com.omarahmed.getnews.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(), ExploreAdapter.ExploreAdapterInterface, HeaderAdapter.HeaderAdapterInterface {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private val exploreAdapter by lazy { ExploreAdapter(this) }
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
        getExploreNews()
        binding.rvExplore.adapter = exploreAdapter
        return binding.root
    }

    private fun getExploreNews(){
        lifecycleScope.launchWhenStarted {
            exploreViewModel.getExploreNews(API_KEY,"business")
            exploreViewModel.exploreNewsResponse.observe(viewLifecycleOwner){ response ->
                when(response){
                    is NetworkResult.Success -> {
                        response.data?.let {
                            exploreAdapter.addHeaderAndSubmitList(it.articles)
                        }
                    }
                    is NetworkResult.Error -> {

                    }

                    is  NetworkResult.Loading ->{

                    }
                }
            }
        }

    }
    override fun setupHeader(headerBinding: ExploreHeaderLayoutBinding) {
        headerBinding.gvExploreHeader.apply {
            adapter = headerAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getCategory(position: Int) {
        val category = exploreHeaderModel[position].title
        exploreViewModel.getExploreNews(API_KEY,category)
    }


}