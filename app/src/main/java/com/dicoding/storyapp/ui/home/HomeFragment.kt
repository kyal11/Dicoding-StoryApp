package com.dicoding.storyapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.FragmentHomeBinding
import com.dicoding.storyapp.foundation.adapter.StoryAdapter
import com.dicoding.storyapp.foundation.adapter.StoryClickListener
import com.dicoding.storyapp.ui.settings.SettingFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeStory.setOnRefreshListener {
            storyAdapter.refresh()
        }

        binding.toolbar.setOnClickListener {
            val settingDialog = SettingFragment()
            settingDialog.show(childFragmentManager, SETTING_TAG)
            settingDialog.setFragmentResultListener("logout") { _, bundle ->
                val isLogout = bundle.getBoolean("is_logout")
                if (isLogout) {
                    homeViewModel.logout()
                    requireActivity().finishAffinity()
                }
            }
        }
        binding.toolbar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.btn_maps -> {
                    if (findNavController().currentDestination?.id == R.id.homeFragment) {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMapsFragment())
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }
        binding.btnAddStory.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPostStoryFragment())
        }
        setupRecyclerView()
        observeViewModel()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter(object : StoryClickListener {
            override fun onStoryClick(story: ListStoryItem) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailStoryFragment(story.id))
            }
        })
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storyAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            homeViewModel.isListStory.collect{ pagingData ->
                pagingData?.let {
                    storyAdapter.submitData(it)
                    binding.swipeStory.isRefreshing = false
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.errorMessage.collect{ errorMessage ->
                errorMessage?.let {
                    if (view != null && isAdded) {
                        showSnackBar(it)
                    }
                    binding.swipeStory.isRefreshing = false
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.isLoading.collect { isLoading ->
                binding.swipeStory.isRefreshing = isLoading
            }
        }
    }

    private fun showSnackBar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val SETTING_TAG = "SettingDialog"
    }
}
