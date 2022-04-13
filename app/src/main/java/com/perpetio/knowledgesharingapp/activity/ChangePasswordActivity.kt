package com.perpetio.knowledgesharingapp.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.perpetio.knowledgesharingapp.databinding.ActivityChangePasswordBinding
import com.perpetio.knowledgesharingapp.utils.ValidationUtils
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

class ChangePasswordActivity :
    BaseActivity<ActivityChangePasswordBinding>(ActivityChangePasswordBinding::inflate) {
    val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        initUi()
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.Loaded -> finish()
                    is ViewModelState.Error -> showToast(it.message)
                }
            }
        }
    }


    private fun initUi() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            btnSubmit.setOnClickListener {
                hideKeyboard()
                if (isAllFieldsValid()) {
                    updatePassword(
                        etPassword.text.toString(),
                        etNewPassword.text.toString()
                    )
                }
            }
        }
    }

    private fun updatePassword(oldPassword: String, newPassword: String) {
        viewModel.updatePassword(oldPassword, newPassword)
    }

    private fun isAllFieldsValid(): Boolean {
        binding.apply {
            val oldPassword = etPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val isOldPasswordValid =
                ValidationUtils.isPasswordValid(oldPassword, errorPassword)
            val isNewPasswordValid =
                ValidationUtils.isPasswordValid(newPassword, errorNewPassword)

            if (isNewPasswordValid && isOldPasswordValid) {
                return true
            }
        }
        return false
    }


    override fun getViewBinding(): ActivityChangePasswordBinding {
        return ActivityChangePasswordBinding.inflate(layoutInflater)
    }
}