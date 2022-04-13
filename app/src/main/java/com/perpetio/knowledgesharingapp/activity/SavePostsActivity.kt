package com.perpetio.knowledgesharingapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.adapter.FeedAdapter
import com.perpetio.knowledgesharingapp.databinding.ActivitySavedPostsBinding
import com.perpetio.knowledgesharingapp.model.Feed
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.FeedViewModel
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

class SavePostsActivity :
    BaseActivity<ActivitySavedPostsBinding>(ActivitySavedPostsBinding::inflate) {

    private val adapter: FeedAdapter by lazy {
        FeedAdapter(feedListener)
    }
    val viewModel: FeedViewModel by viewModels()

    var savedPosts: MutableList<Feed> = mutableListOf()

    val feedListener: FeedAdapter.FeedClickListener = object : FeedAdapter.FeedClickListener {
        override fun onFeedClick(feed: Feed) {
            val intent = Intent(this@SavePostsActivity, PostDetailsActivity::class.java)
            intent.putExtra(Const.KEY_FEED, feed.id)
            startActivity(intent)
        }

        override fun onSavedPost(feed: Feed) {
            removeFeedFromList(feed)
            viewModel.addOrRemoveFeedAsSaved(feed)
        }

        override fun onLikedPost(feed: Feed) {
            viewModel.likeOrUnLikeFeed(feed)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        getPosts()
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            recyclerview.layoutManager = LinearLayoutManager(this@SavePostsActivity)
            recyclerview.adapter = adapter
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun getPosts() {
        val userId = PrefUtils.with(this).getUser()?.id
        userId?.let {
            viewModel.getSavedPosts(it)
        }
    }


    private fun removeFeedFromList(feed: Feed) {
        var indexToRemove = -1
        for (item in savedPosts) {
            if (item.id.equals(feed.id)) {
                indexToRemove = savedPosts.indexOf(item)
            }
        }
        if (indexToRemove != -1) {
            savedPosts.removeAt(indexToRemove)
            adapter.submitList(savedPosts)
        }
    }


    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    is ViewModelState.LoadedList -> showList(it.list as List<Feed>)
                    is ViewModelState.LoadedItem -> addFeedToList(it.item as Feed)
                    is ViewModelState.Error -> showToast(it.message)
                }
            }
        }
    }

    private fun addFeedToList(feed: Feed) {
        if (feed.id != null) {
            val isItemExist = isFeedExistInList(feed)
            if (!isItemExist) {
                savedPosts.add(feed)
            }
            adapter.addNewItem(feed)
        }
    }

    private fun isFeedExistInList(feed: Feed): Boolean {
        for (item in savedPosts) {
            if (item.id?.equals(feed.id) == true) {
                return true
            }
        }
        return false
    }

    private fun showList(list: List<Feed>) {
        savedPosts = list as MutableList<Feed>
        adapter.submitList(list)
        hideProgress()
    }

    override fun getViewBinding(): ActivitySavedPostsBinding {
        return ActivitySavedPostsBinding.inflate(layoutInflater)
    }
}