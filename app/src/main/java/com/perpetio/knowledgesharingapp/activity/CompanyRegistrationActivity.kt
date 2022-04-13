package com.perpetio.knowledgesharingapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.databinding.ActivityCompanyRegistrationBinding
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.utils.ValidationUtils
import com.perpetio.knowledgesharingapp.viewmodel.AuthViewModel
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

class CompanyRegistrationActivity :
    BaseActivity<ActivityCompanyRegistrationBinding>(ActivityCompanyRegistrationBinding::inflate) {
    val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setObservers()
    }

    private fun setupUI() {
        binding.apply {
            btnUserRegistration.setOnClickListener {
                if (isInputsValid()) {
                    hideKeyboard()
                    val user = User(
                        "", etMail.text.toString(),
                        etName.text.toString(),
                        null,
                        Const.COMPANY_TYPE,
                        getString(R.string.owner),
                        etName.text.toString()
                    )

                    viewModel.registerUser(
                        user, binding.etPassword.text.toString()
                    )
                }
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.LoadedUser -> it.user?.let { it1 -> launchNextStep(it1) }
                    is ViewModelState.Error -> showToast(it.message)
                }
            }
        }
    }

    private fun launchNextStep(user: User) {
        hideProgress()
        PrefUtils.with(this).setUser(user)
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            )
        )
        finish()
    }


    private fun isInputsValid(): Boolean {
        val email = binding.etMail.text.toString()
        val password = binding.etPassword.text.toString()
        val name = binding.etName.text.toString()
        val isEmailIsValid = ValidationUtils.isEmailValid(
            email,
            binding.errorEmail
        )
        val isPasswordValid = ValidationUtils.isPasswordValid(password, binding.errorPassword)
        val isNameValid = ValidationUtils.isNameValid(name, binding.errorName)
        if (isEmailIsValid && isPasswordValid && isNameValid) {
            return true
        }
        return false
    }

    override fun getViewBinding(): ActivityCompanyRegistrationBinding {
        return ActivityCompanyRegistrationBinding.inflate(layoutInflater)
    }
}