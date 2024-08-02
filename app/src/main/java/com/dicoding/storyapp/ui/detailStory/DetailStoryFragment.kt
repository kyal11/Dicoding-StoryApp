package com.dicoding.storyapp.ui.detailStory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.FragmentDetailStoryBinding
import com.dicoding.storyapp.foundation.utils.DateUtils
import com.dicoding.storyapp.foundation.utils.LoadingDialogFragment
import com.dicoding.storyapp.ui.login.LoginFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailStoryFragment : Fragment() {
    private val viewModel: DetailStoryViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialogFragment
    private lateinit var binding: FragmentDetailStoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailStoryBinding.inflate(inflater, container, false )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialogFragment()
        val storyId = arguments?.getString("storyId") ?: return
        getStoryDetail(storyId)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        observeViewModel()
    }

    private fun getStoryDetail(storyId: String) {
        showLoading()
        viewModel.getDetailStory(storyId)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.storyData.collect { story ->
                hideLoading()
                story?.let {
                    Glide.with(this@DetailStoryFragment)
                        .load(it.photoUrl)
                        .into(binding.ivStory)
                    binding.tvName.text = it.name
                    binding.tvDesc.text = it.description
                    binding.tvLocation.apply {
                        text = if (it.lat != null && it.lon != null) "${it.lat} | ${it.lon}" else ""
                        visibility = if (it.lat != null && it.lon != null) View.VISIBLE else View.GONE
                    }
                    binding.tvDate.text = it.createdAt?.let { date -> DateUtils.formatDate(date) }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.storyDataDb.collect { story ->
                hideLoading()
                story?.let {
                    Glide.with(this@DetailStoryFragment)
                        .load(it.photoUrl)
                        .into(binding.ivStory)
                    binding.tvName.text = it.name
                    binding.tvDesc.text = it.description
                    binding.tvLocation.apply {
                        text = if (it.lat != null && it.lon != null) "${it.lat} | ${it.lon}" else ""
                        visibility = if (it.lat != null && it.lon != null) View.VISIBLE else View.GONE
                    }
                    binding.tvDate.text = it.createdAt
                }
            }
        }
        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    if (view != null && isAdded) {
                        showSnackBar(it)
                        hideLoading()
                    }
                }
            }
        }
    }
    private fun showLoading() {
        if (!loadingDialog.isAdded) {
            loadingDialog.show(childFragmentManager, LoginFragment.LOADING_TAG)
        }
    }

    private fun hideLoading() {
        if (loadingDialog.isAdded) {
            loadingDialog.dismiss()
        }
    }
    private fun showSnackBar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}