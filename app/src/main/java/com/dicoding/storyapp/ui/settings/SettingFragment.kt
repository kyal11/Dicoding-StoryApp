package com.dicoding.storyapp.ui.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.pref.UserModel
import com.dicoding.storyapp.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : DialogFragment() {
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val paddingHorizontal = resources.getDimensionPixelSize(R.dimen.dialog_horizontal_padding)
        binding.root.setPadding(paddingHorizontal, 0, paddingHorizontal, 0)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.userSession.collect { userModel ->
                updateUI(userModel)
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            viewModel.updateTheme(isChecked)
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val isIndonesian = checkedId == R.id.rb_indonesia
            viewModel.updateLanguage(isIndonesian)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            dismiss()
            findNavController().navigate(R.id.action_homeFragment_to_welcomeFragment)
        }
    }

    private fun updateUI(userModel: UserModel) {
        binding.switchTheme.isChecked = userModel.theme
        if (userModel.language) {
            binding.rbIndonesia.isChecked = true
        } else {
            binding.rbEnglish.isChecked = true
        }
    }
}
