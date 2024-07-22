package com.dicoding.storyapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.FragmentHomeBinding
import com.dicoding.storyapp.foundation.adapter.StoryAdapter
import com.dicoding.storyapp.foundation.adapter.StoryClickListener
import com.dicoding.storyapp.ui.settings.SettingFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
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

        binding.toolbar.setOnClickListener {
            val settingDialog = SettingFragment()
            settingDialog.show(parentFragmentManager, "SettingDialog")
        }
        binding.btnAddStory.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPostStoryFragment())
        }
        setupRecyclerView()
        observeViewModel()
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
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showSnackBar(it)
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}