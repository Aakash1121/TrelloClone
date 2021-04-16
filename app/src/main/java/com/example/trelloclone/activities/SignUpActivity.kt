package com.example.trelloclone.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.trelloclone.R
import com.example.trelloclone.firebase.FireStoreClass
import com.example.trelloclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        btn_sign_up.setOnClickListener {
            registerUser()
        }

    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this@SignUpActivity,
            " you have" + "successfully registered the email",
            Toast.LENGTH_SHORT
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }


    }

    private fun registerUser() {
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {

            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->


                    if (task.isSuccessful) {
                        val fireabaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = fireabaseUser.email!!
                        val user = User(fireabaseUser.uid, name, registeredEmail)
                        FireStoreClass().registerUser(this, user)
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Registeration Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


        }

    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password")
                false
            }
            else -> {
                true
            }
        }
    }
}

