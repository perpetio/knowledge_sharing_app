package com.perpetio.knowledgesharingapp.activity

import android.os.Bundle
import com.bumptech.glide.Glide
import com.perpetio.knowledgesharingapp.databinding.ActivityImagePreviewBinding
import com.perpetio.knowledgesharingapp.utils.Const

class ImagePreviewActivity :
    BaseActivity<ActivityImagePreviewBinding>(ActivityImagePreviewBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUrl = intent.extras?.getString(Const.KEY_IMAGE)
        Glide.with(this)
            .load(imageUrl)
            .into(binding.image)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun getViewBinding(): ActivityImagePreviewBinding {
        return ActivityImagePreviewBinding.inflate(layoutInflater)
    }
}