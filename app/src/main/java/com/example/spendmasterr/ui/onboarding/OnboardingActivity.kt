package com.example.spendmasterr.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.spendmasterr.databinding.ActivityOnboardingBinding
import com.example.spendmasterr.ui.SignInActivity
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPagerAdapter: OnboardingViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupClickListeners()
    }

    private fun setupViewPager() {
        viewPagerAdapter = OnboardingViewPagerAdapter()
        binding.viewPager.adapter = viewPagerAdapter

        // Simple slide animation
        binding.viewPager.setPageTransformer { page, position ->
            // Slide animation
            page.translationX = position * -page.width
            
            // Fade animation
            page.alpha = if (position <= -1f || position >= 1f) 0f else 1f
            
            // Scale animation
            val scale = if (position < 0) {
                (1 + position) * 0.75f + 0.25f
            } else {
                (1 - position) * 0.75f + 0.25f
            }
            page.scaleX = scale
            page.scaleY = scale
        }

        // Disable user swipe
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val isLastPage = position == viewPagerAdapter.itemCount - 1

                // Update button text immediately
                binding.btnNext.text = if (isLastPage) "Get Started" else "Next"

                // Handle skip button visibility
                if (isLastPage) {
                    ViewCompat.animate(binding.btnSkip)
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            binding.btnSkip.visibility = View.GONE
                        }
                        .start()
                } else {
                    binding.btnSkip.visibility = View.VISIBLE
                    ViewCompat.animate(binding.btnSkip)
                        .alpha(1f)
                        .setDuration(200)
                        .start()
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem == viewPagerAdapter.itemCount - 1) {
                startSignInActivity()
            } else {
                // Animate to next page
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
            }
        }

        binding.btnSkip.setOnClickListener {
            startSignInActivity()
        }
    }

    private fun startSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
} 