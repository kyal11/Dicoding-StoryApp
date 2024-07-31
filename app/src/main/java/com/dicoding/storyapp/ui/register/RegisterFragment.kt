package com.dicoding.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentRegisterBinding
import com.dicoding.storyapp.foundation.utils.LoadingDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialogFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()
        loadingDialog = LoadingDialogFragment()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnSignup.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.editTextEmailCustom.text.toString()
            val password = binding.editTextPasswordCustom.text.toString()

            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                registerUser(name, email, password)
            } else {
                showSnackBar(getString(R.string.fill_all_fields))
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            registerViewModel.registerStatus.collectLatest { status ->
                status?.let {
                    if (it) {
                        navigateToLogin()
                        hideLoading()
                        onDestroyView()
                    }
                }
            }
        }

        lifecycleScope.launch {
            registerViewModel.registerMessage.collectLatest { message ->
                message?.let {
                    showSnackBar(it)
                    hideLoading()
                }
            }
        }

        lifecycleScope.launch {
            registerViewModel.errorMessage.collectLatest { errorMessage ->
                errorMessage?.let {
                    showSnackBar(it)
                    hideLoading()
                }
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        showLoading()
        registerViewModel.registerUser(name, email, password)
    }

    private fun showSnackBar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
    }
    private fun playAnimation() {
        val imageAnimatorX = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val signinText = ObjectAnimator.ofFloat(binding.tvSignin, View.ALPHA, 0f, 1f).setDuration(1000)
        val storyAppText = ObjectAnimator.ofFloat(binding.tvStoryapp, View.ALPHA, 0f, 1f).setDuration(1000)
        val descText = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 0f, 1f).setDuration(1000)

        val edtName = ObjectAnimator.ofFloat(binding.edtName, View.ALPHA, 0f, 1f).setDuration(1000)
        val edtEmail = ObjectAnimator.ofFloat(binding.editTextEmailCustom, View.ALPHA, 0f, 1f).setDuration(1000)
        val edtPassword = ObjectAnimator.ofFloat(binding.textInputLayout, View.ALPHA, 0f, 1f).setDuration(1000)

        val btnSigninInitialY = binding.btnSignup.y + 750f
        binding.btnSignup.y = btnSigninInitialY
        val btnSigninMove = ObjectAnimator.ofFloat(binding.btnSignup, View.TRANSLATION_Y, 0f).setDuration(500)

        val animatorSet = AnimatorSet().apply {
            playTogether(signinText, storyAppText, imageAnimatorX, descText)
            playSequentially(
                AnimatorSet().apply {
                    playTogether(edtName, edtEmail, edtPassword)
                },
                btnSigninMove
            )
        }
        animatorSet.start()
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
