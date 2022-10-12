package com.example.pocketwiseexpensemanager

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private lateinit var LoginWithEmailBtn: Button //? = null
    private var usersRef: DatabaseReference? = null
    private var loader: ProgressDialog? = null
    private var authStateListener: AuthStateListener? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        authStateListener = AuthStateListener {
            val user = mAuth!!.currentUser
            if (user != null) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                //finish()
            }
        }
        usersRef = FirebaseDatabase.getInstance().getReference("users")
        loader = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        LoginWithEmailBtn = findViewById(R.id.mBtnSignIn)
        createRequest()
        LoginWithEmailBtn.setOnClickListener(View.OnClickListener { signIn() })
    }

    private fun startLoader() {
        loader!!.setMessage("Please wait...")
        loader!!.setCanceledOnTouchOutside(false)
        loader!!.show()
    }

    //we make a request to create a pop up of all emails signed in in that device. User selects an email.
    private fun createRequest() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //the request is passed to the google sign in client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    //initializing the login process
    //user selects an email after the button is clicked.
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        startLoader()
    }

    //google authenticating the account.
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Google returns an account, through the intent
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Toast.makeText(this, "Failure" + e.message, Toast.LENGTH_SHORT).show()
                loader!!.dismiss()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();*/loader!!.dismiss()
                    updateUI(user)
                } else {
                    Toast.makeText(this@LoginActivity, "Failed", Toast.LENGTH_SHORT).show()
                    loader!!.dismiss()
                }
            }
    }

    private fun updateUI(o: Any?) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this@LoginActivity)
        if (googleSignInAccount != null) {
            val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            val cal = Calendar.getInstance()
            val date = dateFormat.format(cal.time)
            val picUri = googleSignInAccount.photoUrl
            val profilepictureurl = picUri.toString()
            val name = googleSignInAccount.displayName
            val email = googleSignInAccount.email
            val id = mAuth!!.currentUser!!.uid
            val hashMap = HashMap<String, Any?>()
            hashMap["name"] = name
            hashMap["profilepictureurl"] = profilepictureurl
            hashMap["email"] = email
            hashMap["id"] = id
            hashMap["logedinon"] = date
            usersRef!!.child(id).updateChildren(hashMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Successful", Toast.LENGTH_SHORT).show()
                    /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();*/
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Failed to save data " + task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        mAuth!!.removeAuthStateListener(authStateListener!!)
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}