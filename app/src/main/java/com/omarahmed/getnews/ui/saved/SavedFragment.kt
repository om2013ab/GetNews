package com.omarahmed.getnews.ui.saved

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import com.omarahmed.getnews.databinding.FragmentSavedBinding
import com.omarahmed.getnews.models.Article
import com.omarahmed.getnews.shared.ShareClickListener
import com.omarahmed.getnews.shared.UnsavedClickListener
import com.omarahmed.getnews.viewmodels.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment : Fragment() {
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val savedViewModel: SavedViewModel by viewModels()
    private lateinit var savedNewsAdapter: SavedNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(layoutInflater)
        savedNewsAdapter = SavedNewsAdapter(ShareClickListener { url ->
            shareNewsLink(url)
        }, UnsavedClickListener { savedNewsEntity ->
            unsavedClick(savedNewsEntity)
        })

        binding.rvSavedNews.apply {
            adapter = savedNewsAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        savedViewModel.readSavedNews.observe(viewLifecycleOwner) { savedNews ->
            savedNewsAdapter.submitList(savedNews)
        }

        binding.saveViewModel = savedViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    private fun unsavedClick(savedNewsEntity: SavedNewsEntity) {
        savedViewModel.deleteSavedNews(savedNewsEntity)
        Snackbar.make(requireView(), "Successfully deleted", Snackbar.LENGTH_SHORT).apply {
            show()
            setAction("UNDO") {
                savedViewModel.insertSavedNews(savedNewsEntity)
            }
        }
    }

    private fun shareNewsLink(url: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        startActivity(shareIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}