package com.dicoding.storyapp.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
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
            registerViewModel.registerStatus.collect { status ->
                status?.let {
                    showSnackBar(it)
                }
            }
        }

        lifecycleScope.launch {
            registerViewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showSnackBar(it)
                }
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        registerViewModel.registerUser(name, email, password)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
