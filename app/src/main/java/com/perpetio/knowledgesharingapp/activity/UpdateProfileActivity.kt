package com.perpetio.knowledgesharingapp.activity

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.databinding.ActivityUpdateProfileBinding
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.utils.ValidationUtils
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

class UpdateProfileActivity :
    PhotoPickerActivity<ActivityUpdateProfileBinding>(ActivityUpdateProfileBinding::inflate) {

    private var avatarUrl: Uri? = null

    val viewModel: UserViewModel by viewModels()
    val user: User? = PrefUtils.with(this).getUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setObservers()
    }

    private fun setupUI() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
            avatar.setOnClickListener {
                showPickerDialog()
            }
            btnSave.setOnClickListener {
                if (isDataValid()) {
                    showProgress()
                    user?.jobTitle = etJobTitle.text.toString()
                    user?.name = etName.text.toString()
                    user?.let { it1 -> viewModel.updateUser(it1) }
                }
            }
            val user = PrefUtils.with(this@UpdateProfileActivity).getUser()
            etName.setText(user?.name)
            etJobTitle.setText(user?.jobTitle)

            Glide.with(this@UpdateProfileActivity)
                .load(user?.avatarUrl)
                .placeholder(R.drawable.user_placeholder)
                .circleCrop()
                .into(avatar)
        }

    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.LoadedUser -> it.user?.let { it1 -> launchNextStep(it1) }
                    is ViewModelState.Error -> showToast(it.message)
                    is ViewModelState.LoadedUserWithResult -> updateUserState(it.user)
                }
            }
        }
    }

    private fun updateUserState(user: User?) {
        hideProgress()
        if (user != null) {
            PrefUtils.with(this).setUser(user)
        }
    }

    private fun launchNextStep(user: User) {
        hideProgress()
        PrefUtils.with(this).setUser(user)
        onBackPressed()
    }

    private fun isDataValid(): Boolean {
        val isNameValid =
            ValidationUtils.isNameValid(binding.etName.text.toString(), binding.errorName)
        val isJobTitleValid =
            ValidationUtils.isNameValid(binding.etJobTitle.text.toString(), binding.errorJob)

        if (isNameValid && isJobTitleValid) {
            return true
        }
        return false
    }

    override fun getViewBinding(): ActivityUpdateProfileBinding {
        return ActivityUpdateProfileBinding.inflate(layoutInflater)
    }

    override fun openImage(data: Uri?) {
        avatarUrl = data
        Glide.with(this)
            .load(data)
            .placeholder(R.drawable.user_placeholder)
            .error(R.drawable.user_placeholder)
            .circleCrop()
            .into(binding.avatar)
        val user = PrefUtils.with(this).getUser()
        user?.avatarUrl = avatarUrl.toString()
        user?.let { viewModel.saveImage(it) }
    }
}