package com.perpetio.knowledgesharingapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.perpetio.knowledgesharingapp.databinding.ActivityLoginBinding

import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.utils.ValidationUtils
import com.perpetio.knowledgesharingapp.viewmodel.AuthViewModel
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect


class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setObservers()
    }

    private fun setupUI() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (isInputsValid()) {
                    hideKeyboard()
                    viewModel.login(
                        etMail.text.toString(),
                        etPassword.text.toString()
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
        val isEmailIsValid = ValidationUtils.isEmailValid(
            email,
            binding.errorEmail
        )
        val isPasswordValid = ValidationUtils.isPasswordValid(password, binding.errorPassword)
        if (isEmailIsValid && isPasswordValid) {
            return true
        }
        return false
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

}