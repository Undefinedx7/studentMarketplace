package com.example.studentmarketplace

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentmarketplace.ApiClientCallback.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mEmailEditText: EditText
    private lateinit var mPasswordEditText: EditText
    private lateinit var mLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        mEmailEditText = findViewById(R.id.email_edit_text)
        mPasswordEditText = findViewById(R.id.password_edit_text)
        mLoginButton = findViewById(R.id.login_button)

        mLoginButton.setOnClickListener(View.OnClickListener {
            val email = mEmailEditText.text.toString().trim()
            val password = mPasswordEditText.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@LoginActivity, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@LoginActivity, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            // Make API request to authenticate user
            val apiClient = ApiClient.getInstance(this@LoginActivity)
            apiClient.authenticateUser(email, password, object : ApiResponseListener {
                override fun onSuccess(response: ApiResponse) {
                    // If login is successful, start MainActivity
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

                override fun onError(error: ApiError) {
                    Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        })
    }
}
