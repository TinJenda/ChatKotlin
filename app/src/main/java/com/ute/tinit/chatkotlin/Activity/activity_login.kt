package com.ute.tinit.chatkotlin.Activity

//tin
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_login.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import android.view.View
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status


class activity_login : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    var mGoogleApiClient: GoogleApiClient? = null
    val RC_SIGN_IN = 9001
    var logout_check = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_login)
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // [END config_signin]

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this@activity_login /* FragmentActivity */, GoogleApiClient.OnConnectionFailedListener() {
                    Toast.makeText(this@activity_login, "Login fail", Toast.LENGTH_SHORT).show()
                }/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
                var intent=intent
              btnLogin()
    }


    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        // Firebase sign out
        mAuth!!.signOut()

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                object : ResultCallback<Status> {
                    override fun onResult(status: Status) {
                        updateUI(null)
                    }
                })
    }


    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            Toast.makeText(this@activity_login, "Welcome...!", Toast.LENGTH_SHORT).show()
            btn_login.setVisibility(View.GONE)
//            tv_user_email.setText( user.displayName)
//            tv_user_id.setText(user.uid)
            displayNext(user)
        } else {
//            tv_user_id.setText(null)
//            login_succes.visibility=View.GONE
            btn_login.setVisibility(View.VISIBLE)
        }
    }

    fun displayNext(user: FirebaseUser?) {
        var intent = Intent(this@activity_login, activity_profile_firstlogin::class.java)
        intent.putExtra("userid", user!!.uid)
        intent.putExtra("username", user!!.displayName)
        intent.putExtra("email", user!!.email)
        startActivity(intent)
        finish()
    }

    fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("AAA", "onConnectionFailed:" + connectionResult)
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this@activity_login, "Failddđ on activity Result", Toast.LENGTH_SHORT).show()
                // ...
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.getCurrentUser()
        updateUI(currentUser)
    }

    fun showProgressDialog() {
        progressLogin.visibility = View.VISIBLE
    }

    fun hideProgressDialog() {
        progressLogin.visibility = View.GONE
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        showProgressDialog()
        Log.d("AAA", "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    //hereeee
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("AAA", "signInWithCredential:success")
                        val user = mAuth!!.getCurrentUser()
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("AAA", "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@activity_login, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    // ...
                    hideProgressDialog()
                }
    }

    fun btnLogin() {
        btn_login.setOnClickListener {
            signIn()
        }
    }

}
