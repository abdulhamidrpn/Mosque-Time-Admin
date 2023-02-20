package com.rpn.adminmosque.ui.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rpn.adminmosque.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    val TAG = "AuthTAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}