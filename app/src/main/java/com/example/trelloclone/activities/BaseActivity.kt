package com.example.trelloclone.activities

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.trelloclone.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressdialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text: String) {
        mProgressdialog = Dialog(this)
        mProgressdialog.setContentView(R.layout.dialog_progress)
        mProgressdialog.tv_progress_text.text = text
        mProgressdialog.show()
    }

    fun hideProgressDialog() {
        mProgressdialog.dismiss()
    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBacktoExit() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this@BaseActivity,
            R.string.please_click_back_again_to_exit,
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    fun showErrorSnackBar(message: String) {
        val snackBar: Snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarview = snackBar.view
        snackBarview.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))

        snackBar.show()
    }


}