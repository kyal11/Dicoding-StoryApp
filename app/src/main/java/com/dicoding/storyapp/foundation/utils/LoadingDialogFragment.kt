package com.dicoding.storyapp.foundation.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dicoding.storyapp.databinding.FragmentLoadingDialogBinding

class LoadingDialogFragment : DialogFragment() {
    private lateinit var binding : FragmentLoadingDialogBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentLoadingDialogBinding.inflate(layoutInflater)

        val dialogLoading = Dialog(requireContext())
        dialogLoading.setContentView(binding.root)
        dialogLoading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogLoading.setCancelable(false)
        dialogLoading.setCanceledOnTouchOutside(false)
        return dialogLoading
    }
}