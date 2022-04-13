package com.perpetio.knowledgesharingapp.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.adapter.CommentsAdapter
import com.perpetio.knowledgesharingapp.databinding.ActivityPostDetailsBinding
import com.perpetio.knowledgesharingapp.model.BaseModel
import com.perpetio.knowledgesharingapp.model.Comment
import com.perpetio.knowledgesharingapp.model.Feed
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.FeedViewModel
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

class PostDetailsActivity :
    BaseActivity<ActivityPostDetailsBinding>(ActivityPostDetailsBinding::inflate) {
    val viewModel: FeedViewModel by viewModels()
    var isPostLiked: Boolean = false
    var feed: Feed? = null
    var isPostSaved: Boolean = false
    var user = PrefUtils.with(this).getUser()
    val commentAdapter: CommentsAdapter by lazy {
        CommentsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getExtraInfo()
        setObservers()
    }

    private fun getExtraInfo() {
        if (intent?.extras != null) {
            val feedId = intent?.extras?.getString(Const.KEY_FEED)
            if (feedId == null) {
                onBackPressed()
            } else {
                isPostSaved = feed?.usersThatSavedPost?.contains(user?.id) == true
                isPostLiked = feed?.usersThatLikedPost?.contains(user?.id) == true
                viewModel.getFeedById(feedId)
            }
        } else {
            onBackPressed()
        }
    }

    private fun proceedItem(item: BaseModel) {
        if (item is Feed) {
            setupUI(item)
        } else if (item is Comment) {
            feed?.let { it1 ->
                viewModel.getCommentsForPost(
                    it1
                )
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.LoadedItem -> proceedItem(it.item)
                    is ViewModelState.Error -> showToast(it.message)
                    is ViewModelState.LoadedList -> updateList(it.list as List<Comment>)
                }
            }
        }
    }

    private fun updateList(list: List<Comment>) {
        hideProgress()
        commentAdapter.submitList(list)
    }

    private fun updateUser(user: User) {
        this.user = user
        PrefUtils.with(this).setUser(user)
    }

    private fun setupUI(returnedFeed: Feed) {
        hideProgress()
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            if (isPostSaved) {
                btnSave.setImageResource(R.drawable.ic_bookmark_selected)
            } else {
                btnSave.setImageResource(R.drawable.ic_bookmark_unselected)
            }
            feed = returnedFeed
            viewModel.getCommentsForPost(feed!!)
            userName.text = feed?.userName
            Glide.with(this@PostDetailsActivity)
                .load(feed?.userImage)
                .placeholder(R.drawable.user_placeholder)
                .circleCrop()
                .into(userAvatar)
            feedTitle.text = feed?.title
            feedDescription.text = feed?.description
            if (feed?.image != null) {
                image.isVisible = true
                Glide.with(this@PostDetailsActivity)
                    .load(feed?.image)
                    .placeholder(R.drawable.placeholder_photo)
                    .into(image)
            } else {
                image.isVisible = false
            }
            val userId = user?.id
            btnSave.setOnClickListener {
                val list = feed?.usersThatSavedPost
                if (isPostSaved) {
                    list?.remove(userId)
                    btnSave.setImageResource(R.drawable.ic_bookmark_unselected)
                    isPostSaved = false
                } else {
                    if (userId != null) {
                        list?.add(userId)
                    }
                    btnSave.setImageResource(R.drawable.ic_bookmark_selected)
                    isPostSaved = true
                }
                if (list != null) {
                    feed?.usersThatSavedPost = list
                }
                viewModel.addOrRemoveFeedAsSaved(feed!!)
            }
            if (isPostLiked) {
                btnLike.setImageResource(R.drawable.ic_like_selected)
            } else {
                btnLike.setImageResource(R.drawable.ic_like_unselected)
            }

            btnLike.setOnClickListener {
                val list = feed?.usersThatLikedPost
                if (isPostLiked) {
                    list?.remove(userId)
                    btnLike.setImageResource(R.drawable.ic_like_unselected)
                    isPostLiked = false
                } else {
                    if (userId != null) {
                        list?.add(userId)
                    }
                    btnLike.setImageResource(R.drawable.ic_like_selected)
                    isPostLiked = true
                }
                if (list != null) {
                    feed?.usersThatLikedPost = list
                }
                viewModel.likeOrUnLikeFeed(feed!!)
            }
            recyclerviewComments.layoutManager = LinearLayoutManager(this@PostDetailsActivity)
            recyclerviewComments.adapter = commentAdapter

            btnSend.setOnClickListener {
                val comment = etComment.text.toString()
                if (comment.isNotEmpty()) {
                    viewModel.createComment(comment, user!!, feed?.id!!)
                    etComment.setText("")
                    hideKeyboard()
                }
            }
        }


    }

    override fun getViewBinding(): ActivityPostDetailsBinding {
        return ActivityPostDetailsBinding.inflate(layoutInflater)
    }
}