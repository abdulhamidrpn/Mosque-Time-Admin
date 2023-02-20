package com.rpn.adminmosque.ui.activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.rpn.adminmosque.R

/*

class EmailAndPasswordLoginActivity : AppCompatActivity() {
    private var userMail: EditText? = null
    private var userPassword: EditText? = null
    private var btnLogin: Button? = null
    private var loginProgress: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var HomeActivity: Intent? = null
    private var logToRegBtn: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_and_password_login)
        logToRegBtn = findViewById(R.id.loginToReg)
        logToRegBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(
                this@EmailAndPasswordLoginActivity,
                EmailAndPasswordRegisterActivity::class.java
            )
            startActivity(intent)
        })
        userMail = findViewById(R.id.login_mail)
        userPassword = findViewById(R.id.login_password)
        btnLogin = findViewById(R.id.loginnBtn)
        loginProgress = findViewById(R.id.login_progress)
        mAuth = FirebaseAuth.getInstance()
        HomeActivity = Intent(this, MainActivity::class.java)
        loginProgress.setVisibility(View.INVISIBLE)
        btnLogin.setOnClickListener(View.OnClickListener {
            loginProgress.setVisibility(View.VISIBLE)
            btnLogin.setVisibility(View.INVISIBLE)
            val mail = userMail.getText().toString()
            val password = userPassword.getText().toString()
            if (mail.isEmpty() || password.isEmpty()) {
                showMessage("Please Verify All Field")
                btnLogin.setVisibility(View.VISIBLE)
                loginProgress.setVisibility(View.INVISIBLE)
            } else {
                signIn(mail, password)
            }
        })
    }

    private fun signIn(mail: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(mail, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loginProgress!!.visibility = View.INVISIBLE
                btnLogin!!.visibility = View.VISIBLE
                updateUI()
            } else {
                showMessage(task.exception!!.message)
                btnLogin!!.visibility = View.VISIBLE
                loginProgress!!.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateUI() {
        startActivity(HomeActivity)
        finish()
    }

    private fun showMessage(text: String?) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user != null) {
            //user is already connected  so we need to redirect him to home page
            updateUI()
        }
    }
}*/
