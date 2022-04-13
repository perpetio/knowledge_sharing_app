package com.perpetio.knowledgesharingapp.activity

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.databinding.ActivityCreatePostBinding
import com.perpetio.knowledgesharingapp.model.Feed
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.FeedViewModel
import kotlinx.coroutines.flow.collect

class CreatePostActivity :
    PhotoPickerActivity<ActivityCreatePostBinding>(ActivityCreatePostBinding::inflate) {

    private var avatarUrl: Uri? = null

    val viewModel: FeedViewModel by viewModels()

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
            btnCreate.setOnClickListener {
                if (isAllFieldsValid()) {
                    val user = PrefUtils.with(this@CreatePostActivity).getUser()
                    val id: String = user?.id + System.currentTimeMillis()
                    val feed = Feed(
                        id,
                        etTitle.text.toString(),
                        etDescription.text.toString(),
                        user?.avatarUrl,
                        user?.name,
                        user?.id!!,
                        avatarUrl?.toString(),
                        user.company,
                        System.currentTimeMillis(),
                        etLink.text.toString(),
                        mutableListOf(),
                        mutableListOf()
                    )
                    viewModel.createFeed(feed)
                } else {
                    showToast(R.string.error_empty_post)
                }
            }
            btnAddPhoto.setOnClickListener {
                showPickerDialog()
            }
        }

    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.Loaded -> onBackPressed()
                    is ViewModelState.Error -> showToast(it.message)
                }
            }
        }
    }


    private fun isAllFieldsValid(): Boolean {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()
        val isTitleValid = title.isNotEmpty()
        val isDescriptionValid = description.isNotEmpty()

        return isTitleValid && isDescriptionValid
    }

    override fun getViewBinding(): ActivityCreatePostBinding {
        return ActivityCreatePostBinding.inflate(layoutInflater)
    }

    override fun openImage(data: Uri?) {
        avatarUrl = data
        Glide.with(this)
            .load(data)
            .placeholder(R.drawable.user_placeholder)
            .error(R.drawable.user_placeholder)
            .into(binding.image)
    }
}