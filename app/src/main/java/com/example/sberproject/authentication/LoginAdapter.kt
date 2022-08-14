package com.example.sberproject.authentication

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.lifecycle.Lifecycle

class LoginAdapter (
    fm : FragmentManager,
    lifecycle: Lifecycle)
: FragmentStateAdapter(fm,lifecycle)
{
    override fun getItemCount(): Int {
        return 2
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment()
            else -> RegistrationFragment()
        }
    }
}