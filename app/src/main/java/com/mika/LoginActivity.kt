package com.mika

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mika.databinding.ActivityLoginBinding
import java.util.*


class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding
    private val AUTH_REQUEST_CODE = 1171
    private var firebaseAuth : FirebaseAuth? = null
    private lateinit var stateListener : FirebaseAuth.AuthStateListener
    private lateinit var providers : List<AuthUI.IdpConfig>
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>
    override fun onStart() {
        super.onStart()
        firebaseAuth?.addAuthStateListener(stateListener)


    }

    override fun onStop() {
        firebaseAuth?.removeAuthStateListener(stateListener)
        super.onStop()

    }

    private fun init(){
        providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build(),
                          AuthUI.IdpConfig.EmailBuilder().build())
        stateListener = FirebaseAuth.AuthStateListener {
            var user : FirebaseUser? = firebaseAuth?.currentUser
            if(user != null){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{

            }
        }
    }

    private fun checkResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val userId = firebaseAuth?.currentUser?.uid
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

            finishAndRemoveTask()

            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            val userPressedBackButton = (response == null)
            if (userPressedBackButton) {
                // _authResultCode.value = AuthResultCode.CANCELLED
               Toast.makeText(this,"login Cancelled!!",Toast.LENGTH_LONG).show()
                Log.d(null, "Login cancelled by user")
                return
            }
            val error = response?.error?.message
            when (response?.error?.errorCode) {


                ErrorCodes.NO_NETWORK -> {
                    //_authResultCode.value = AuthResultCode.NO_NETWORK
                    Toast.makeText(this,error,Toast.LENGTH_LONG).show()
                    Log.d(null, "Login failed on network connectivity")
                }

                ErrorCodes.PROVIDER_ERROR-> { Toast.makeText(this,"login Cancelled!!",Toast.LENGTH_LONG).show()}
                ErrorCodes.EMAIL_MISMATCH_ERROR    -> { Toast.makeText(this,"login Cancelled!!",Toast.LENGTH_LONG).show()}
                ErrorCodes.ERROR_GENERIC_IDP_RECOVERABLE_ERROR ->{ Toast.makeText(this,"login Cancelled!!",Toast.LENGTH_LONG).show()}

                else -> {
                    Toast.makeText(this,"login failed!!",Toast.LENGTH_LONG).show()
                    Log.d(null, "Login failed")

                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)

        init()
        val customLayout = AuthMethodPickerLayout.Builder(com.mika.R.layout.auth_layout)
            .setGoogleButtonId(com.mika.R.id.googleButton)
            .setEmailButtonId(com.mika.R.id.signInButton) // ...
//            .setTosAndPrivacyPolicyId(R.id.baz)
            .build()
        val intent = AuthUI
            .getInstance()
            .createSignInIntentBuilder()
            .setAuthMethodPickerLayout(customLayout)
            .setAvailableProviders(providers)
           // .setLogo(R.drawable.mika)

            .setIsSmartLockEnabled(true)
            .setTheme(com.mika.R.style.Theme_Mika_NoActionBar)
            .build()

        loginLauncher =  registerForActivityResult(FirebaseAuthUIActivityResultContract()){
            if (it != null) {
                checkResult(it)
            }
        }

        loginLauncher.launch(intent)


    }

}