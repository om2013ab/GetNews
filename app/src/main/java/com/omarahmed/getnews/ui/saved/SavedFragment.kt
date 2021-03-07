package com.omarahmed.getnews.ui.saved

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.omarahmed.getnews.R
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import com.omarahmed.getnews.databinding.FragmentSavedBinding
import com.omarahmed.getnews.models.Article
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment : Fragment(),SavedNewsAdapter.OnClickListener {
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val savedViewModel: SavedViewModel by viewModels()
    private val savedNewsAdapter by lazy { SavedNewsAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(layoutInflater)

        binding.rvSavedNews.apply {
            adapter = savedNewsAdapter
            layoutManager =  GridLayoutManager(requireContext(),2)
        }

        savedViewModel.readSavedNews.observe(viewLifecycleOwner){savedNews ->
            savedNewsAdapter.submitList(savedNews)
        }

        binding.saveViewModel = savedViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onUnsavedClick(savedNewsEntity: SavedNewsEntity) {
        savedViewModel.deleteSavedNews(savedNewsEntity)
        Snackbar.make(requireView(),"Successfully deleted",Snackbar.LENGTH_SHORT).apply {
            show()
            setAction("UNDO"){
                savedViewModel.insertSavedNews(savedNewsEntity)
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}