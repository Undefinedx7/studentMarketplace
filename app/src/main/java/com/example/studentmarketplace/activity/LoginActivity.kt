package com.example.studentmarketplace.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentmarketplace.ApiClient
import com.example.studentmarketplace.ApiClient.ApiResponse
import com.example.studentmarketplace.ApiClient.ApiResponseListener
import com.example.studentmarketplace.R

class LoginActivity : AppCompatActivity() {

    private lateinit var mEmailEditText: EditText
    private lateinit var mPasswordEditText: EditText
    private lateinit var mLoginButton: Button
    // private lateinit var mforgottenPassTv : TextView

    fun onSignUpClick(view: View?) {
        // Handle sign up click event here
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        mEmailEditText = findViewById(R.id.email_edit_text)
        mPasswordEditText = findViewById(R.id.password_edit_text)
        mLoginButton = findViewById(R.id.login_button)
        val mForgottenPassTv: TextView = findViewById(R.id.forgottenPassTv)

        // Set click listener for "Forgotten Password" button
        mForgottenPassTv.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        mLoginButton.setOnClickListener(View.OnClickListener {
            val email = mEmailEditText.text.toString().trim()
            val password = mPasswordEditText.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@LoginActivity, "Please enter your email", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@LoginActivity, "Please enter your password", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            ApiClient.getInstance().authenticateUser(email, password, object : ApiResponseListener {
                override fun onSuccess(response: ApiResponse) {
                    // If login is successful, start MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onError(error: ApiClient.ApiError) {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        })
    }
}