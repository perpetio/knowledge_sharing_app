package com.perpetio.knowledgesharingapp.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.databinding.ActivityCreateUserBinding
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.utils.ValidationUtils
import com.perpetio.knowledgesharingapp.viewmodel.AuthViewModel
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

class CreateUserActivity :
    BaseActivity<ActivityCreateUserBinding>(ActivityCreateUserBinding::inflate) {
    val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            btnUserRegistration.setOnClickListener {
                if (isInputsValid()) {
                    hideKeyboard()
                    val user = User(
                        "",
                        etMail.text.toString(),
                        etName.text.toString(),
                        null,
                        Const.USER_TYPE,
                        etJobTitle.text.toString(),
                        PrefUtils.with(this@CreateUserActivity).getUser()?.company
                    )
                    viewModel.registerUser(user, etPassword.text.toString())
                }
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.LoadedUser -> launchNextStep()
                    is ViewModelState.Error -> showToast(it.message)
                }
            }
        }
    }

    private fun launchNextStep() {
        hideProgress()
        finish()
    }

    private fun isInputsValid(): Boolean {
        val email = binding.etMail.text.toString()
        val password = binding.etPassword.text.toString()
        val name = binding.etName.text.toString()
        val job = binding.etJobTitle.text.toString()
        val isEmailIsValid = ValidationUtils.isEmailValid(
            email,
            binding.errorEmail
        )
        val isPasswordValid = ValidationUtils.isPasswordValid(password, binding.errorPassword)
        val isNameValid = ValidationUtils.isNameValid(name, binding.errorName)
        val isJobTitleValid = ValidationUtils.isNameValid(job, binding.errorJob)
        if (isEmailIsValid && isPasswordValid && isNameValid && isJobTitleValid) {
            return true
        }
        return false
    }

    override fun getViewBinding(): ActivityCreateUserBinding {
        return ActivityCreateUserBinding.inflate(layoutInflater)
    }
}