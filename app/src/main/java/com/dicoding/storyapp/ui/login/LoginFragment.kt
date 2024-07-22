package com.dicoding.storyapp.ui.login

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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
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
            loginViewModel.loginStatus.collect { status ->
                status?.let {
                    if (status) {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                    }

                }
            }
        }

        lifecycleScope.launch {
            loginViewModel.loginMassage.collect { status ->
                status?.let {
                    showSnackBar(it)
                }
            }
        }
        lifecycleScope.launch {
            loginViewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showSnackBar(it)
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        loginViewModel.loginUser(email, password)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}
