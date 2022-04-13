package com.perpetio.knowledgesharingapp.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.paris.girl.easycook.utils.ImageHelper
import com.perpetio.knowledgesharingapp.R

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import javax.inject.Inject

abstract class PhotoPickerActivity<B : ViewBinding>(bindingFactory: (LayoutInflater) -> B) :
    BaseActivity<B>(bindingFactory) {

    val TAG = PhotoPickerActivity::class.java.simpleName
    private val CROPPED_NAME = "cropped.jpg"
    protected var currentPhotoPath: String? = null
    private var isTakePhoto = false
    private val isSquare = true
    private var count = 0

    protected abstract fun openImage(data: Uri?)

    companion object {
        const val REQUEST_CODE_ASK_PERMISSIONS = 123
        const val STATE_PHOTO_PATH = "photoPath"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString(STATE_PHOTO_PATH)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PHOTO_PATH, currentPhotoPath)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto()
            } else {
                showToast("You need to allow access to external storage")
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    open fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    open fun getPhoto() {
        val hasStoragePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (hasStoragePermission == PackageManager.PERMISSION_GRANTED) {
            choosePhoto()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_ASK_PERMISSIONS
            )
        }
    }

    var resultLauncherForChoosePhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                openImage(data?.data?.let { ImageHelper.compressPicture(this, it) })
            }
        }

    open fun choosePhoto() {
        val intent: Intent = ImageHelper.getChoosePictureIntent()
        if (intent?.resolveActivity(this.packageManager) != null) {
            resultLauncherForChoosePhoto.launch(intent)
        }
    }

    open fun showPickerDialog() {
        val options = arrayOf<CharSequence>(
            getString(R.string.choose_from_gallery),
            getString(R.string.dialog_cancel)
        )
        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { dialog, item ->
            if (options[item] == getString(R.string.choose_from_gallery)) {
                getPhoto()
            } else if (options[item] == getString(R.string.dialog_cancel)) {
                dialog.dismiss()
            }
        }
        builder.show()
    }


}