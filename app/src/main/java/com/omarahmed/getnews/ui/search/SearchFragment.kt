package com.omarahmed.getnews.ui.search

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.omarahmed.getnews.R
import com.omarahmed.getnews.databinding.FragmentSearchBinding
import com.omarahmed.getnews.util.Constants.API_KEY
import com.omarahmed.getnews.util.NetworkResult
import com.omarahmed.getnews.util.onQueryTextSubmit
import com.omarahmed.getnews.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()
    private val searchAdapter by lazy { SearchAdapter() }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater)
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.tbSearch)
        binding.rvSearch.adapter = searchAdapter

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val searchIcon = menu.findItem(R.id.search_news)
        val searchView = searchIcon.actionView as SearchView
        searchView.apply {
            binding.tbSearch.setNavigationOnClickListener {
                clearFocus()
                findNavController().navigateUp()
            }
            onActionViewExpanded()
            queryHint = context.getString(R.string.search_hint)
            onQueryTextSubmit {
                searchNews(it)
                searchView.clearFocus()
            }
        }

    }

    private fun searchNews(query: String) {
        searchViewModel.getSearchNews(API_KEY, query)
        searchViewModel.searchNewsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.ivSearch.isVisible = false
                    binding.tvTitleSearch.isVisible = false
                    binding.tvDescSearch.isVisible = false
                    binding.pbSearch.isVisible = false
                    response.data?.let {
                        searchAdapter.submitList(it.articles)
                    }
                }
                is NetworkResult.Error -> {
                    binding.tvDescSearch.text = response.message
                    binding.ivSearch.isVisible = true
                    binding.tvTitleSearch.isVisible = true
                    binding.tvDescSearch.isVisible = true
                    binding.pbSearch.isVisible = false
                }
                is NetworkResult.Loading -> {
                    binding.ivSearch.isVisible = false
                    binding.tvTitleSearch.isVisible = false
                    binding.tvDescSearch.isVisible = false
                    binding.pbSearch.isVisible = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}