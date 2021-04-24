package com.example.trelloclone.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.firebase.FireStoreClass
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class MyProfileActivity : BaseActivity() {


    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl: String = ""

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setupActionBar()

        FireStoreClass().loadUserData(this@MyProfileActivity)

        iv_profile_user_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        btn_update.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
         grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            }
        } else {
            Toast.makeText(
                this,
                "Opps,you just denied the permission for the storage.You can change permission settings inside application settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showImageChooser() {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data
            try {
                Glide.with(this@MyProfileActivity).load(mSelectedImageFileUri).centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder).into(iv_profile_user_image)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        toolbar_my_profile_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()
        if (mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageUrl
        }
        if (et_name.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = et_name.text.toString()

        }
        if (et_mobile.text.toString() != mUserDetails.mobileNumber.toString()) {
            userHashMap[Constants.MOBILE] = et_mobile.text.toString().toLong()

        }
        FireStoreClass().updateUserProfileData(this, userHashMap)
    }

    fun setUserDataInUI(user: User) {

        mUserDetails = user
        Glide.with(this@MyProfileActivity).load(user.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).into(iv_profile_user_image)
        et_name.setText(user.name)
        et_email.setText(user.email)
        if (user.mobileNumber != 0L) {
            et_mobile.setText(user.mobileNumber.toString())
        }
    }

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(this,                    mSelectedImageFileUri
                )
            )
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapShot ->
                Log.i(
                    "Firebase Image URL",
                    taskSnapShot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.i("Downloadable Image Url", uri.toString())
                    mProfileImageUrl = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this@MyProfileActivity, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }


    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}