package com.example.notefirebase.fragments.settings

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.databinding.FragmentMainSettingsBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.login.and.authorization.ReAuthenticateFragment
import com.example.notefirebase.fragments.login.and.authorization.SignInFragment
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener


class MainSettingsFragment : Fragment() {
    private lateinit var frameBinding: FragmentMainSettingsBinding
    private lateinit var helper: Helper
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        frameBinding = FragmentMainSettingsBinding.inflate(inflater, container, false)
        // Initializing user interface elements

        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()

        mInterstitialAd = InterstitialAd(requireContext())
        mInterstitialAd!!.setAdUnitId("R-M-DEMO-interstitial")
        mInterstitialAd!!.loadAd(adRequest)

        mInterstitialAd!!.setInterstitialAdEventListener(object : InterstitialAdEventListener {
            override fun onAdLoaded() {
                Log.i(TAG, "onAdLoaded")
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                // Handle the error
                Log.i(TAG, error.description)
            }

            override fun onAdDismissed() {
                // Called when an interstitial ad has been dismissed.
                Log.d("TAG", "The ad was dismissed.")
            }

            override fun onAdShown() {
                // Called when an interstitial ad has been shown.
                Log.d("TAG", "The ad was shown.")
            }

            override fun onImpression(impressionData: ImpressionData?) {
                // Called when an impression was tracked
                Log.d("TAG", "The ad imprassion was tracked.")
            }

            override fun onAdClicked() {
                // Called when user clicked on the ad.
                Log.d("TAG", "The ad was clicked.")
            }

            override fun onReturnedToApplication() {
                // Called when user returned to application after click.
                Log.d("TAG", "The ad was clicked.")
            }

            override fun onLeftApplication() {
                // Called when user is about to leave application after tapping on an ad.
                Log.d("TAG", "The ad left application after click.")
            }
        })


        init()
        return frameBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        helper = Helper(requireActivity())
    }

    private fun init() {
        with(frameBinding) {
            // Getting the current user
            val user = Firebase.auth.currentUser
            // Getting a link to the Firebase database
            val database = Firebase.database.reference

            // Creating a list of provider IDs
            val providerIdsList: MutableList<String> = mutableListOf()

            // Check if there is a link to the database and the current user
            if (user != null && database != null) {
                userEmail.text = user.email
                // Getting a list of provider IDs that were used to authenticate the user
                for (userInfo in user.providerData) {
                    val providerId = userInfo.providerId
                    providerIdsList.add(providerId)
                }
                // If the user used Google to log in, then we display his name and photo
                if (providerIdsList.contains("google.com")) {
                    userName.text = user.displayName

                    Picasso.get().load(user.photoUrl).into(userImg)
                } else {
                    // If the user used another provider, then we get his name from the database
                    val userId = user.uid
                    database.child("users").child(userId).child("name").get()
                        .addOnSuccessListener { nameSnapshot ->
                            val name = nameSnapshot.getValue(String::class.java)
                            userName.text = name
                        }
                }
            }
        }
    }

    private fun setupClickListeners() {
        with(frameBinding) {
            // Log out from account
            btnLogOut.setOnClickListener {
                logOut()
            }

            // Change email
            btnChangeEmail.setOnClickListener {
                helper.navigate(ReAuthenticateFragment())
            }

            // Change password
            btnChangePassword.setOnClickListener {
                helper.navigate(InputYourEmailForResetFragment())
            }

            // Go to main
            btnToMain.setOnClickListener {
                if (mInterstitialAd!!.isLoaded) {
                    mInterstitialAd!!.show()
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                helper.navigate(MainFragment())
            }
        }
    }

    private fun logOut() {
        Firebase.auth.signOut()
        updateUI(null)
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            return
        } else {
            helper.navigate(SignInFragment())
        }
    }
}