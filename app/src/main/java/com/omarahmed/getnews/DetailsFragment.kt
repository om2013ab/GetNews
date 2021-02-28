package com.omarahmed.getnews

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.omarahmed.getnews.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()

    private val fromBottom by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }

    private var clicked = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = FragmentDetailsBinding.inflate(layoutInflater)

        binding.webView.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.pbDetails.visibility = View.VISIBLE

                }


                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.pbDetails.visibility = View.INVISIBLE
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
            Toast.makeText(requireContext(), "save", Toast.LENGTH_SHORT).show()
        }
        binding.fabShare.setOnClickListener {
            Toast.makeText(requireContext(), "share", Toast.LENGTH_SHORT).show()
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