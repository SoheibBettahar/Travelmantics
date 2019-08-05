package com.example.travelmantics

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class RegisterActivity : AppCompatActivity() {

    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        FirebaseUtil.openFbReference(this)

        button_sign_up.setOnClickListener {
            val email = sign_up_text_input_email.editText!!.text.toString().trim()
            val password = sign_up_text_input_password.editText!!.text.toString().trim()
            val confirmPassword = sign_up_text_input_confirm.editText!!.text.toString().trim()

            if(validation()){
                createAccountWithEmailAndPassword(email, password)
            }
        }

        button_go_back.setOnClickListener {
            goToSignInActivity()
        }

    }

    private fun goToSignInActivity() {
        val intent = Intent(this@RegisterActivity, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validation(): Boolean {
     return true
    }

    fun createAccountWithEmailAndPassword(email: String, password: String) {
        FirebaseUtil.mFirebaseAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = FirebaseUtil.mFirebaseAuth!!.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this@RegisterActivity, ListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        goToSignInActivity()
    }
}


