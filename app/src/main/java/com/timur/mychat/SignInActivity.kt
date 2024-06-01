package com.timur.mychat

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.timur.mychat.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialogSignIn: ProgressDialog
    private lateinit var signInBinding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(signInBinding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        progressDialogSignIn = ProgressDialog(this)
        signInBinding.signInTextToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        signInBinding.loginButton.setOnClickListener {
            email = signInBinding.loginetemail.text.toString()
            password = signInBinding.loginetpassword.text.toString()
            if (signInBinding.loginetemail.text.toString().isEmpty()) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            }
            if (signInBinding.loginetpassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            }
            if (signInBinding.loginetemail.text.toString()
                    .isNotEmpty() && signInBinding.loginetpassword.text.toString().isNotEmpty()
            ) {
                signIn(email, password)
            }
        }

    }

    private fun signIn(email: String, password: String) {
        progressDialogSignIn.show()
        progressDialogSignIn.setMessage("Signing in")
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                progressDialogSignIn.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                progressDialogSignIn.dismiss()
                Toast.makeText(this, "Incorrect credentials", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            when (exception) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Toast.makeText(this, "Incorrect credentials", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    //other errors
                    Toast.makeText(this, "Auth Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        progressDialogSignIn.dismiss()
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialogSignIn.dismiss()
    }
}