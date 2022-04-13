package com.perpetio.knowledgesharingapp.utils

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.perpetio.knowledgesharingapp.R
import java.util.regex.Pattern

object ValidationUtils {
    val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile( "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", Pattern.CASE_INSENSITIVE)
    val VALID_NAME_REGEX = Pattern.compile("[a-zA-Z][a-zA-Z ]+", Pattern.CASE_INSENSITIVE)
    val VALID_PASSWORD_REGEX =
        Pattern.compile("^[A-Z0-9.!@#$%^&*()_+?[-]]{6,}$", Pattern.CASE_INSENSITIVE)

    fun isEmailValid(email: String, textView: TextView): Boolean {
        return if (TextUtils.isEmpty(email) || email.trim { it <= ' ' }.length == 0) {
            textView.setText(R.string.error_empty_field)
            textView.visibility = View.VISIBLE
            false
        } else if (VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            textView.visibility = View.GONE
            true
        } else {
            textView.setText(R.string.error_invalid_email)
            textView.visibility = View.VISIBLE
            false
        }
    }

    fun isPasswordValid(password: String, textView: TextView): Boolean {
        return if (TextUtils.isEmpty(password) || password.trim { it <= ' ' }.length == 0) {
            textView.setText(R.string.error_empty_field)
            textView.visibility = View.VISIBLE
            false
        } else {
            if (VALID_PASSWORD_REGEX.matcher(password).find()) {
                textView.visibility = View.GONE
                true
            } else {
                textView.setText(R.string.error_invalid_password)
                textView.visibility = View.VISIBLE
                false
            }
        }
    }

    fun isNameValid(value: String, textView: TextView): Boolean {
        return if (TextUtils.isEmpty(value) || value.trim { it <= ' ' }.length == 0) {
            textView.setText(R.string.error_empty_field)
            textView.visibility = View.VISIBLE
            false
        } else {
            if (!VALID_NAME_REGEX.matcher(value).find()) {
                textView.setText(R.string.error_invalid_name)
                textView.visibility = View.VISIBLE
                false
            } else {
                textView.visibility = View.GONE
                true
            }
        }
    }
}