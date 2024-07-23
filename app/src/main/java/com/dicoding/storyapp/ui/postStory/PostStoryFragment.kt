package com.dicoding.storyapp.ui.postStory

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentPostStoryBinding
import com.dicoding.storyapp.foundation.utils.getImageUri
import com.dicoding.storyapp.foundation.utils.reduceFileImage
import com.dicoding.storyapp.foundation.utils.uriToFile
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class PostStoryFragment : Fragment() {
    private val viewModel: PostStoryViewModel by viewModels()
    private var imageUri: Uri? = null
    private lateinit var binding: FragmentPostStoryBinding

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            imageUri = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nav = findNavController().currentDestination
        Log.d("PostStory", "Status: $nav")

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnGalery.setOnClickListener {
            startGallery()
        }
        binding.btnCamera.setOnClickListener {
            startCamera()
        }
        binding.btnPosting.setOnClickListener {
            postStory()
        }

        observeViewModel()
    }

    private fun startCamera() {
        imageUri = getImageUri(requireContext())
        imageUri?.let { uri ->
            launcherIntentCamera.launch(uri)
        } ?: showSnackBar("Failed to get image URI for camera")
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        binding.ivPhoto.setImageURI(imageUri)
    }

    private fun postStory() {
        imageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            val descriptionText = binding.edtDescription.text.toString()
            if (descriptionText.isBlank()) {
                showSnackBar("Description cannot be empty")
                return
            }
            val descriptionRequestBody = descriptionText.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            viewModel.postStory(descriptionRequestBody, multipartBody, null, null)
            observeViewModel()

        } ?: showSnackBar("Please select a photo")
    }



    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.postStatus.collect { status ->
                status?.let {
                    showSnackBar(it)
                    if (findNavController().currentDestination?.id == R.id.postStoryFragment) {
                        findNavController().navigate(R.id.action_postStoryFragment_to_homeFragment)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
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
