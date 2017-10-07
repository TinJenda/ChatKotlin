package com.ute.tinit.chatkotlin.Activity

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;
import com.ute.tinit.chatkotlin.MainActivity
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import android.view.View
import android.widget.*
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.FirebaseException
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_new_account.*

class activity_new_account : AppCompatActivity() {

    private val TAG = "PhoneLogin"
    private var mVerificationInProgress = false
    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_new_account)

        mAuth = FirebaseAuth.getInstance()
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false
                Toast.makeText(this@activity_new_account, "Verification Complete", Toast.LENGTH_SHORT).show()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(this@activity_new_account, "Verification Failed", Toast.LENGTH_SHORT).show()
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@activity_new_account, "InValid Phone Number", Toast.LENGTH_SHORT).show()
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                }

            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken?) {
                // Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(this@activity_new_account, "Verification code has been send on your number", Toast.LENGTH_SHORT).show()
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
                Phonenoedittext.visibility = View.GONE
                PhoneVerify.setVisibility(View.GONE)
                textView2Phone.visibility = View.GONE
                imageView2Phone.setVisibility(View.GONE)
                textViewVerified.setVisibility(View.VISIBLE)
                OTPeditText.setVisibility(View.VISIBLE)
                OTPVERIFY.setVisibility(View.VISIBLE)
                // ...
            }
        }

        PhoneVerify.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        Phonenoedittext.text.toString(),
                        60,
                        java.util.concurrent.TimeUnit.SECONDS,
                        this@activity_new_account,
                        mCallbacks as PhoneAuthProvider.OnVerificationStateChangedCallbacks)
            }
        })

        OTPVERIFY.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val credential = PhoneAuthProvider.getCredential(mVerificationId!!, OTPeditText.getText().toString())
                // [END verify_with_code]
                signInWithPhoneAuthCredential(credential)
            }
        })


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Log.d(TAG, "signInWithCredential:success");
                        startActivity(Intent(this@activity_new_account, MainActivity::class.java))
                        Toast.makeText(this@activity_new_account, "Verification Done", Toast.LENGTH_SHORT).show()
                        // ...
                    } else {
                        // Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this@activity_new_account, "Invalid Verification", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
    }
}