package com.example.studentmarketplace.activity

import android.content.ContentValues
import android.content.Intent
import com.example.studentmarketplace.R
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.studentmarketplace.AddItemActivity
import com.example.studentmarketplace.HomeFragment
import com.example.studentmarketplace.databinding.ActivityMainBinding
import com.example.studentmarketplace.fragment.CategoryFragment
import com.example.studentmarketplace.fragment.FavoriteFragment
import com.example.studentmarketplace.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> replaceFragment(HomeFragment())
                R.id.action_profile -> replaceFragment(ProfileFragment())
                R.id.action_favorites -> replaceFragment(FavoriteFragment())
                R.id.action_category -> replaceFragment(CategoryFragment())
                R.id.action_sell -> {
                    val intent = Intent(this, AddItemActivity::class.java)
                    startActivity(intent)
                    true
                }
                else ->{

                }
            }
            true
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sell -> {
                val intent = Intent(this, AddItemActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}

