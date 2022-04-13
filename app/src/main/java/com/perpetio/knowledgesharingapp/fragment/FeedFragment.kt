package com.perpetio.knowledgesharingapp.fragment


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.activity.CreatePostActivity
import com.perpetio.knowledgesharingapp.activity.PostDetailsActivity
import com.perpetio.knowledgesharingapp.adapter.FeedAdapter
import com.perpetio.knowledgesharingapp.databinding.FragmentFeedBinding
import com.perpetio.knowledgesharingapp.model.BaseModel
import com.perpetio.knowledgesharingapp.model.Feed
import com.perpetio.knowledgesharingapp.utils.Const.Companion.KEY_FEED
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.FeedViewModel
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

class FeedFragment : BaseFragment<FragmentFeedBinding>() {
    private val adapter: FeedAdapter by lazy {
        FeedAdapter(feedListener)
    }

    val viewModel: FeedViewModel by activityViewModels()

    val feedListener: FeedAdapter.FeedClickListener = object : FeedAdapter.FeedClickListener {
        override fun onFeedClick(feed: Feed) {
            val intent = Intent(context, PostDetailsActivity::class.java)
            intent.putExtra(KEY_FEED, feed.id)
            startActivity(intent)
        }

        override fun onSavedPost(feed: Feed) {
            viewModel.addOrRemoveFeedAsSaved(feed)
        }

        override fun onLikedPost(feed: Feed) {
            viewModel.likeOrUnLikeFeed(feed)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        val company = PrefUtils.with(requireContext()).getUser()?.company
        if (company != null) {
            viewModel.getFeedList(company)
        }
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            recyclerview.layoutManager = LinearLayoutManager(requireContext())
            recyclerview.adapter = adapter

            btnCreatePost.setOnClickListener {
                val intent = Intent(requireContext(), CreatePostActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.LoadedList -> showList(it.list as List<Feed>)
                    is ViewModelState.Error -> showToast(it.message)
                }
            }
        }
    }

    private fun showList(list: List<BaseModel>) {
        if (list.size > 0 && list.get(0) is Feed) {
            adapter.submitList(list as List<Feed>)
        }
        hideProgress()
    }

    override fun getViewBinding(): FragmentFeedBinding {
        return FragmentFeedBinding.inflate(layoutInflater)
    }
}