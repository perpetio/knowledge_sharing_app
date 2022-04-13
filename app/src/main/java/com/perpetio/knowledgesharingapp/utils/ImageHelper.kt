package com.paris.girl.easycook.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.perpetio.knowledgesharingapp.BuildConfig
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton
import android.graphics.Bitmap

import android.graphics.BitmapFactory


object ImageHelper {
    private const val IMAGE_MIME_TYPE = "image/*"
    private const val MAX_IMAGE_DIMENSION = 1024

    fun getChoosePictureIntent(): Intent {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        return photoPickerIntent
    }

    fun compressImage(startUri: Uri?, context: Context): Uri? {
        if (context != null) {
            val f: File = File(context.getCacheDir(), "newAvatar")
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(context.getContentResolver(), startUri)
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)
                val bitmapdata = bos.toByteArray()
                val fos = FileOutputStream(f)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()
                return Uri.fromFile(f)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun compressPicture(context: Context, currentPhoto: Uri): Uri? {
        var compressedUri = Uri.parse("")
        try {
            val array: ByteArray? =
                getCorrectRotatedBitmap(context, currentPhoto)
            val bitmap = array?.let { BitmapFactory.decodeByteArray(array, 0, it.size) }
            val file = File(
                context.cacheDir,
                getFileNameFromUri(currentPhoto)
            )
            val fileOutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)
            compressedUri = uriFromFile(context, file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return compressedUri
    }

    fun uriFromFile(context: Context?, file: File?): Uri? {
        return if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                context!!, BuildConfig.APPLICATION_ID + ".provider",
                file!!
            )
        } else {
            Uri.fromFile(file)
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        return File(uri.path).name
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "avatar",
            null
        )
        return Uri.parse(path)
    }

    fun getCorrectRotatedBitmap(
        context: Context,
        imageUri: Uri
    ): ByteArray? {
        var `is`: InputStream? = null
        var bMapArray: ByteArray? = null
        try {
            `is` = context.contentResolver.openInputStream(imageUri)
            val dbo = BitmapFactory.Options()
            dbo.inJustDecodeBounds = true
            BitmapFactory.decodeStream(`is`, null, dbo)
            if (dbo.outWidth == -1) {
                BitmapFactory.decodeStream(`is`, null, dbo)
            }
            `is`?.close()
            val rotatedWidth: Int
            val rotatedHeight: Int
            val orientation: Int =
                getOrientation(context, imageUri)
            if (orientation == 90 || orientation == 270) {
                rotatedWidth = dbo.outHeight
                rotatedHeight = dbo.outWidth
            } else {
                rotatedWidth = dbo.outWidth
                rotatedHeight = dbo.outHeight
            }
            var srcBitmap: Bitmap?
            `is` = context.contentResolver.openInputStream(imageUri)
            if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
                val widthRatio =
                    rotatedWidth.toFloat() / MAX_IMAGE_DIMENSION
                val heightRatio =
                    rotatedHeight.toFloat() / MAX_IMAGE_DIMENSION
                val maxRatio = Math.max(widthRatio, heightRatio)
                val options = BitmapFactory.Options()
                options.inSampleSize = maxRatio.toInt()
                srcBitmap = BitmapFactory.decodeStream(`is`, null, options)
            } else {
                srcBitmap = BitmapFactory.decodeStream(`is`)
            }
            `is`?.close()
            if (orientation > 0 && srcBitmap != null) {
                val matrix = Matrix()
                matrix.postRotate(orientation.toFloat())
                srcBitmap = Bitmap.createBitmap(
                    srcBitmap, 0, 0, srcBitmap.width,
                    srcBitmap.height, matrix, true
                )
            }
            val type: String? = getMimeType(context, imageUri)
            val baos = ByteArrayOutputStream()
            if (!TextUtils.isEmpty(type) && srcBitmap != null) {
                if (type == "image/png") {
                    srcBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos)
                } else if (type == "image/jpg" || type == "image/jpeg") {
                    srcBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                }
            }
            bMapArray = baos.toByteArray()
            baos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bMapArray
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Throws(IOException::class)
    private fun getExifInterfaceN(
        context: Context,
        uri: Uri
    ): ExifInterface? {
        var inputStream: InputStream? = null
        return try {
            inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                ExifInterface(inputStream)
            } else {
                null
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Throws(IOException::class)
    private fun getOrientation(context: Context, imageUri: Uri): Int {
        val exif: ExifInterface
        exif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) (
                getExifInterfaceN(context, imageUri)!!
                ) else {
            try {
                ExifInterface(getRealPath(context, imageUri)!!)
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }
        }
        if (exif == null) {
            return 0
        }
        val orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION)!!.toInt()
        return if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            90
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            180
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            270
        } else {
            0
        }
    }

    fun getRealPath(context: Context, imageUri: Uri): String? {
        var filePath: String? = null
        var filePathUri = imageUri
        if (imageUri.scheme != null && imageUri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor = context.contentResolver.query(imageUri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePathUri = Uri.parse(cursor.getString(column_index))
                filePath = filePathUri.path
                cursor.close()
            }
        } else if (imageUri.scheme != null && imageUri.scheme == ContentResolver.SCHEME_FILE) {
            filePath = filePathUri.path
        }
        return filePath
    }


    private fun getMimeType(context: Context, imageUri: Uri): String? {
        val mimeType: String?
        mimeType =
            if (imageUri.scheme != null && imageUri.scheme == ContentResolver.SCHEME_CONTENT) {
                val cr = context.contentResolver
                cr.getType(imageUri)
            } else {
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase())
            }
        return mimeType
    }

}