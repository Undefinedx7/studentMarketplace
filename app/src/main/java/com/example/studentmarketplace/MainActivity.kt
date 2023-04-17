package com.example.studentmarketplace

import com.example.studentmarketplace.R
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Get the values passed from the login activity
        val userId = intent.getStringExtra("user_id")
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")

        // Display a welcome message with the user's information
        /*val welcomeTextView = findViewById<TextView>(R.id.welcome_text_view)
        welcomeTextView.text = String.format(
            "Welcome %s! Your user ID is %s and your email is %s.",
            username,
            userId,
            email
        )*/
    }
}