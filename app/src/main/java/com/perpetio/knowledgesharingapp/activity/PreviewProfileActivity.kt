package com.perpetio.knowledgesharingapp.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.databinding.ActivityPreviewProfileBinding
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import kotlinx.coroutines.flow.collect

class PreviewProfileActivity :
    BaseActivity<ActivityPreviewProfileBinding>(ActivityPreviewProfileBinding::inflate) {

    val viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.extras?.getString(Const.KEY_USER_ID)
        if (userId != null) {
            setObservers()
            showProgress()
            viewModel.getUserInfo(userId)
        } else {
            onBackPressed()
        }

    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    is ViewModelState.Loading -> showProgress()
                    is ViewModelState.Error -> showToast(it.message)
                    is ViewModelState.LoadedUser -> setUserInfo(it.user)
                }
            }
        }
    }

    private fun setUserInfo(user: User) {
        hideProgress()
        binding.apply {
            Glide.with(this@PreviewProfileActivity)
                .load(user.avatarUrl)
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder)
                .circleCrop()
                .into(avatar)
            name.text = user.name
            email.text = user.email
            jobTitle.text = user.jobTitle
        }
    }

    override fun getViewBinding(): ActivityPreviewProfileBinding {
        return ActivityPreviewProfileBinding.inflate(layoutInflater)
    }
}