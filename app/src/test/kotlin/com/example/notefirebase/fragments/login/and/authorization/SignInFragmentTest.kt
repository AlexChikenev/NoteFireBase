package com.example.notefirebase.fragments.login.and.authorization

import junit.framework.TestCase.assertNotNull
//import `package com`.example.notefirebase.fragments.login.and.`authorization;`
import org.junit.Test

class SignInFragmentTest {


    /**Should initialize FirebaseAuth, GoogleAuth, and EmailPasswordAuth instances*/
    @Test
    fun initInitializesAuthInstances() {
        val signInFragment = SignInFragment()
        signInFragment.init()

        assertNotNull(signInFragment.auth)
        assertNotNull(signInFragment.googleAuth)
        assertNotNull(signInFragment.emailPasswordAuth)
    }

}