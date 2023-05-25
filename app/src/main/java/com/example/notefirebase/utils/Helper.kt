package com.example.notefirebase.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.notefirebase.R

class Helper(
    private val activity: FragmentActivity
) {
    fun navigate(fragment: Fragment){
        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentHolder, fragment)
            .commit()
    }
}