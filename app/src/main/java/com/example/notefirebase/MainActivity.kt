package com.example.notefirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notefirebase.fragments.login.and.authorization.SignInFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder, SignInFragment())
            .commit()
    }
}