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
import com.google.firebase.firestore.FirebaseFirestore
import com.timur.mychat.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var signUpAuth: FirebaseAuth
    private lateinit var name: String
    private lateinit var nick: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var signUpPd: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)
        firestore = FirebaseFirestore.getInstance()
        signUpAuth = FirebaseAuth.getInstance()
        signUpPd = ProgressDialog(this)

        signUpBinding.signUpTextToSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signUpBinding.signUpBtn.setOnClickListener {
            name = signUpBinding.signUpEtName.text.toString()
            email = signUpBinding.signUpEmail.text.toString()
            password = signUpBinding.signUpPassword.text.toString()
            nick = signUpBinding.signUpEtNick.text.toString()
            if(signUpBinding.signUpEtName.text.isEmpty()){
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
            }
            if(signUpBinding.signUpEtNick.text.isEmpty()){
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
            }
            if(signUpBinding.signUpEmail.text.isEmpty()){
                Toast.makeText(this, "Enter Nickname", Toast.LENGTH_SHORT).show()
            }
            if(signUpBinding.signUpPassword.text.isEmpty()){
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            }
            if (signUpBinding.signUpEtName.text.isNotEmpty() &&
                signUpBinding.signUpEtNick.text.isNotEmpty() &&
                signUpBinding.signUpEmail.text.isNotEmpty() &&
                signUpBinding.signUpPassword.text.isNotEmpty()
            ){
                signUpUser(name, nick, email, password)
            }
        }
    }

    private fun signUpUser(name: String, nick: String, email: String, password: String) {
        signUpPd.show()
        signUpPd.setMessage("Signing Up")
        signUpAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if(it.isSuccessful){
                val user = signUpAuth.currentUser
                val hashMap = hashMapOf("userid" to user!!.uid!!, "username" to name,
                    "nickname" to nick,
                    "useremail" to email,
                    "status" to "default",
                    "imageUrl" to "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png")
                firestore.collection("Users").document(user.uid).set(hashMap)
                signUpPd.dismiss()
                startActivity(Intent(this, SignInActivity::class.java))
            }
        }
    }
}