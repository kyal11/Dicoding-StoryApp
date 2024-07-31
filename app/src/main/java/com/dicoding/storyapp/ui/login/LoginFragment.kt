package com.dicoding.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentLoginBinding
import com.dicoding.storyapp.foundation.utils.LoadingDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        loadingDialog = LoadingDialogFragment()
        setupListeners()
        observeViewModel()
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

        val edtEmail = ObjectAnimator.ofFloat(binding.editTextEmailCustom, View.ALPHA, 0f, 1f).setDuration(1000)
        val edtPassword = ObjectAnimator.ofFloat(binding.textInputLayout, View.ALPHA, 0f, 1f).setDuration(1000)

        val btnSigninInitialY = binding.btnSignin.y + 750f
        binding.btnSignin.y = btnSigninInitialY
        val btnSigninMove = ObjectAnimator.ofFloat(binding.btnSignin, View.TRANSLATION_Y, 0f).setDuration(500)

        val animatorSet = AnimatorSet().apply {
            playTogether(signinText, storyAppText, imageAnimatorX, descText)
            playSequentially(
                AnimatorSet().apply {
                    playTogether(edtEmail, edtPassword)
                },
                btnSigninMove
            )
        }
        animatorSet.start()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_welcomeFragment)
        }
        binding.btnSignin.setOnClickListener {
            val email = binding.editTextEmailCustom.text.toString()
            val password = binding.editTextPasswordCustom.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                loginUser(email, password)
            } else {
                showSnackBar(getString(R.string.fill_all_fields))
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginStatus.collectLatest { status ->
                status.let {
                    if (status) {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        hideLoading()
                        onDestroyView()
                    }
                }
            }
        }

        lifecycleScope.launch {
            loginViewModel.loginMessage.collectLatest { message ->
                message.let {
                    if (view != null && isAdded) {
                        showSnackBar(it)
                    }
                    hideLoading()
                }
            }
        }

        lifecycleScope.launch {
            loginViewModel.errorMessage.collectLatest { errorMessage ->
                errorMessage.let {
                    if (view != null && isAdded) {
                        showSnackBar(it)
                    }
                    hideLoading()
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        showLoading()
        loginViewModel.loginUser(email, password)
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


