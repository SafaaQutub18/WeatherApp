 package com.safaa.weatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.safaa.weatherapp.databinding.ActivityCodeBinding

 class CodeActivity : AppCompatActivity() {
     private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendBtn.setOnClickListener {
            var newCode= ""
            try {
                val codeInt = binding.inputCodeET.text.toString().toInt()
                newCode = codeInt.toString()
                saveCode(newCode)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }catch (e:Exception){
                Toast.makeText(this, "Please enter 5 integer number !_!", Toast.LENGTH_LONG).show()
            }

        }
    }

     private fun saveCode(zipCode: String) {
         sharedPreferences = this.getSharedPreferences(
             getString(R.string.preference_file_key), Context.MODE_PRIVATE)
         sharedPreferences.getString("zipCode", "").toString()

    with(sharedPreferences.edit()) {
        putString("zipCode", zipCode)
        apply()
    }

}

     }
