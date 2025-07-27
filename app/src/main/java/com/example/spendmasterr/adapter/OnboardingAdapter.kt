package com.example.spendmasterr.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendmasterr.R
import com.example.spendmasterr.model.OnboardingScreen

class OnboardingAdapter(private val onboardingScreens: List<OnboardingScreen>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageOnboarding = view.findViewById<ImageView>(R.id.imageOnboarding)
        private val textTitle = view.findViewById<TextView>(R.id.textTitle)
        private val textDescription = view.findViewById<TextView>(R.id.textDescription)

        fun bind(onboardingScreen: OnboardingScreen) {
            imageOnboarding.setImageResource(onboardingScreen.image)
            textTitle.text = onboardingScreen.title
            textDescription.text = onboardingScreen.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_onboarding_screen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingScreens[position])
    }

    override fun getItemCount(): Int {
        return onboardingScreens.size
    }
} 