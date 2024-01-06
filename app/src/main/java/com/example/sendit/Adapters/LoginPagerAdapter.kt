package com.example.sendit.Adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sendit.Login.LoginActivity
import com.example.sendit.Login.LoginFragment
import com.example.sendit.Login.SignupFragment

class LoginPagerAdapter(fa: LoginActivity): FragmentStateAdapter(fa) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment.newInstance("f1", "page #1")
            1 -> SignupFragment.newInstance("f2", "page #2")
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
    override fun getItemCount(): Int {
        return 2
    }
}