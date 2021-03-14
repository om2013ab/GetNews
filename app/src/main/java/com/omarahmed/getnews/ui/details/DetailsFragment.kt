package com.omarahmed.getnews.ui.details

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.omarahmed.getnews.R
import com.omarahmed.getnews.data.room.entities.SavedNewsEntity
import com.omarahmed.getnews.databinding.FragmentDetailsBinding
import com.omarahmed.getnews.viewmodels.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()
    private val savedViewModel: SavedViewModel by viewModels()

    private val fromBottom by lazy { AnimationUtils.loadAnimation(requireContext(),
        R.anim.from_bottom_anim
    ) }
    private val toBottom by lazy { AnimationUtils.loadAnimation(requireContext(),
        R.anim.to_bottom_anim
    ) }

    private var clicked = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = FragmentDetailsBinding.inflate(layoutInflater)

        binding.webView.apply {
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    _binding?.let {
                        binding.pbDetails.isVisible = newProgress < 100 && binding.pbDetails.isInvisible
                    }
                    super.onProgressChanged(view, newProgress)
                }
            }
            loadUrl(args.article.url)
        }
        binding.fabOptions.setOnClickListener {
            setFabVisibility(clicked)
            setFabAnimation(clicked)
            clicked = !clicked
        }

        binding.fabSave.setOnClickListener {
            val savedNewsEntity = SavedNewsEntity(0,args.article)
            savedViewModel.insertSavedNews(savedNewsEntity)
            Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()
            binding.fabSave.setImageResource(R.drawable.ic_bookmark_saved)
            setFabVisibility(clicked)
            setFabAnimation(clicked)
            clicked = !clicked
        }
        binding.fabShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,args.article.url)
                type = "text/plain"
            }
            startActivity(shareIntent)
            setFabVisibility(clicked)
            setFabAnimation(clicked)
            clicked = !clicked
        }

        return binding.root
    }

    private fun setFabVisibility(clicked: Boolean) {
        binding.fabSave.apply {
            isVisible = !clicked
            binding.fabSave.isClickable = !clicked
        }
        binding.fabShare.apply {
            isVisible = !clicked
            binding.fabShare.isClickable = !clicked
        }
    }

    private fun setFabAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.fabSave.startAnimation(fromBottom)
            binding.fabShare.startAnimation(fromBottom)
            binding.fabOptions.setImageResource(R.drawable.ic_close)
        } else {
            binding.fabSave.startAnimation(toBottom)
            binding.fabShare.startAnimation(toBottom)
            binding.fabOptions.setImageResource(R.drawable.ic_dehaze)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}