package com.perpetio.knowledgesharingapp.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.perpetio.knowledgesharingapp.R


open abstract class BaseActivity<B : ViewBinding>(val bindingFactory: (LayoutInflater) -> B) :
   AppCompatActivity() {
    lateinit var binding: B
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        val view: View = binding.root
        setContentView(view)
        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
    }
    abstract fun getViewBinding(): B

    protected open fun showToast(@StringRes stringRes: Int) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show()
    }

    protected open fun showToast(stringRes: String?) {
        hideProgress()
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show()
    }

    open fun showProgress() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, null, null, true, false)
            progressDialog?.setContentView(R.layout.loader)
            progressDialog?.getWindow()?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            progressDialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog?.setCancelable(false)
        }
        if (progressDialog != null && !progressDialog?.isShowing()!!) {
            progressDialog?.show()
        }
    }

    open fun hideProgress() {
        if (progressDialog != null) {
            progressDialog?.hide()
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    open fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    protected open fun replaceFragmentWithoutBackStack(
        activity: FragmentActivity,
        fragment: Fragment,
        containerId: Int,
        tag: String?
    ) {
        activity.supportFragmentManager
            .beginTransaction()
            .replace(containerId, fragment, tag)
            .commit()
    }

     fun startActivityNewTask(intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}