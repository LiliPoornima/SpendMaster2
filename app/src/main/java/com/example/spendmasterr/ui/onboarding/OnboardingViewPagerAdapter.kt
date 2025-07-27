package com.example.spendmasterr.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spendmasterr.R
import com.example.spendmasterr.databinding.ItemOnboardingBinding

class OnboardingViewPagerAdapter : RecyclerView.Adapter<OnboardingViewPagerAdapter.OnboardingViewHolder>() {

    private val onboardingItems = listOf(
        OnboardingItem(
            "Welcome to SpendMaster",
            "Track your expenses and manage your budget easily",
            R.drawable.logo,
            R.color.onboarding_bg_1
        ),
        OnboardingItem(
            "Smart Budgeting",
            "Set budgets and get insights into your spending habits",
            R.drawable.money,
            R.color.onboarding_bg_2
        ),
        OnboardingItem(
            "Secure & Private",
            "Your financial data is safe with us",
            R.drawable.security,
            R.color.onboarding_bg_3
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position], position)
    }

    override fun getItemCount() = onboardingItems.size

    class OnboardingViewHolder(private val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OnboardingItem, position: Int) {
            binding.apply {
                // Set content
                textTitle.text = item.title
                textDescription.text = item.description
                imageOnboarding.setImageResource(item.imageResId)
                root.setBackgroundColor(ContextCompat.getColor(root.context, item.backgroundColor))

                // Clear any existing animations
                imageOnboarding.clearAnimation()
                textTitle.clearAnimation()
                textDescription.clearAnimation()

                when (position) {
                    2 -> { // Third page - Secure & Private
                        // Special animations for the security page
                        imageOnboarding.startAnimation(
                            AnimationUtils.loadAnimation(root.context, R.anim.secure_slide_in)
                        )

                        textTitle.postDelayed({
                            textTitle.startAnimation(
                                AnimationUtils.loadAnimation(root.context, R.anim.text_slide_up_fade)
                            )
                        }, 400)

                        textDescription.postDelayed({
                            textDescription.startAnimation(
                                AnimationUtils.loadAnimation(root.context, R.anim.text_slide_up_fade)
                            )
                        }, 600)
                    }
                    else -> {
                        // Default animations for other pages
                        imageOnboarding.startAnimation(
                            AnimationUtils.loadAnimation(root.context, R.anim.smooth_fade_in)
                        )
                        
                        textTitle.postDelayed({
                            textTitle.startAnimation(
                                AnimationUtils.loadAnimation(root.context, R.anim.fade_in)
                            )
                        }, 300)

                        textDescription.postDelayed({
                            textDescription.startAnimation(
                                AnimationUtils.loadAnimation(root.context, R.anim.slide_up)
                            )
                        }, 500)
                    }
                }
            }
        }
    }

    data class OnboardingItem(
        val title: String,
        val description: String,
        val imageResId: Int,
        val backgroundColor: Int
    )
} 