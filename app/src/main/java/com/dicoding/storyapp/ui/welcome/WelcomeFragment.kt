package com.dicoding.storyapp.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.navigation.fragment.findNavController
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()
        binding.btnSignin.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }
        binding.btnSignup.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
        }
    }

    private fun playAnimation() {
        val imageViewInitialY = binding.imageView.y + 300f
        binding.imageView.y = imageViewInitialY
        val imageViewMoveUp = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, 0f).apply {
            duration = 1000
        }
        val imageViewBounce = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val welcomeText = ObjectAnimator.ofFloat(binding.textView2, View.ALPHA, 0f, 1f).setDuration(3000)
        val descText = ObjectAnimator.ofFloat(binding.textView4, View.ALPHA, 0f, 1f).setDuration(3000)

        val btnSigninInitialX = binding.btnSignin.x + 300f
        binding.btnSignin.x = btnSigninInitialX
        val btnSigninMove = ObjectAnimator.ofFloat(binding.btnSignin, View.TRANSLATION_X, 0f).setDuration(1500)

        val btnSignupInitialX = binding.btnSignup.x - 300f
        binding.btnSignup.x = btnSignupInitialX
        val btnSignupMove = ObjectAnimator.ofFloat(binding.btnSignup, View.TRANSLATION_X, 0f).setDuration(1500)

        AnimatorSet().apply {
            play(imageViewMoveUp)
            playTogether(welcomeText, descText)
            playTogether(btnSigninMove, btnSignupMove)
            start()
        }

        imageViewMoveUp.doOnEnd {
            imageViewBounce.start()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }
}