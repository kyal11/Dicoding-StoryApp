package com.dicoding.storyapp.ui.postStory

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentPostStoryBinding
import com.dicoding.storyapp.foundation.utils.LoadingDialogFragment
import com.dicoding.storyapp.foundation.utils.getImageUri
import com.dicoding.storyapp.foundation.utils.reduceFileImage
import com.dicoding.storyapp.foundation.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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
    private lateinit var loadingDialog: LoadingDialogFragment
    private var lat: Double? = null
    private var lon: Double? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                showSnackBar(getString(R.string.permission_denied))
            }
        }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            showImage()
        } else {
            showSnackBar(getString(R.string.failed_to_get_image))
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
        loadingDialog = LoadingDialogFragment()
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
        binding.switchLocation.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                getMyLocation()
            } else {
                lat = null
                lon = null
            }
        }

        observeViewModel()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        lat = it.latitude
                        lon = it.longitude
                    } ?: showSnackBar(getString(R.string.failed_to_get_location))
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startCamera() {
        imageUri = getImageUri(requireContext())
        imageUri?.let { uri ->
            launcherIntentCamera.launch(uri)
        } ?: showSnackBar(getString(R.string.failed_to_get_image))
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
                showSnackBar(getString(R.string.empty_description))
                return
            }
            val descriptionRequestBody = descriptionText.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            showLoading()
            viewModel.postStory(descriptionRequestBody, multipartBody,  lat?.toFloat(), lon?.toFloat())
            observeViewModel()

        } ?: showSnackBar(getString(R.string.select_photo))
    }



    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.postStatus.collectLatest { status ->
                status?.let {
                    showSnackBar(it)
                    hideLoading()
                    if (findNavController().currentDestination?.id == R.id.postStoryFragment) {
                        findNavController().navigate(R.id.action_postStoryFragment_to_homeFragment)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { errorMessage ->
                errorMessage?.let {
                    showSnackBar(it)
                    hideLoading()
                }
            }
        }
    }

    private fun showSnackBar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }
    private fun showLoading() {
        if (!loadingDialog.isAdded) {
            loadingDialog.show(childFragmentManager, LOADING_TAG)
        }
    }

    private fun hideLoading() {
        if (loadingDialog.isAdded) {
            loadingDialog.dismiss()
        }
    }
    companion object {
        const val LOADING_TAG = "Loading"
    }
}
