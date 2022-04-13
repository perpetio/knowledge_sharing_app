package com.perpetio.knowledgesharingapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.perpetio.knowledgesharingapp.activity.BaseActivity

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    lateinit var binding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        val view: View = binding.root
        return view
    }

    abstract fun getViewBinding(): VB

    protected fun showToast(@StringRes stringRes: Int) {
        Toast.makeText(context, stringRes, Toast.LENGTH_LONG).show()
    }

    protected fun showToast(stringRes: String?) {
        hideProgress()
        Toast.makeText(context, stringRes, Toast.LENGTH_LONG).show()
    }

    protected fun showProgress() {
        val activity: BaseActivity<*>? = activity as BaseActivity<*>?
        if (activity != null) activity.showProgress()
    }

    protected fun hideProgress() {
        val activity: BaseActivity<*>? = activity as BaseActivity<*>?
        if (activity != null) activity.hideProgress()
    }

    protected fun startActivityClearTask(intent: Intent) {
        val activity: BaseActivity<*>? = activity as BaseActivity<*>?
        if (activity != null) activity.startActivityNewTask(intent)
    }
}
