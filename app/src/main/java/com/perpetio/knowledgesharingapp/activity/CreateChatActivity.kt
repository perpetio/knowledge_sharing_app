package com.perpetio.knowledgesharingapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.adapter.UserAdapter
import com.perpetio.knowledgesharingapp.databinding.ActivityCreateChatBinding
import com.perpetio.knowledgesharingapp.model.Chat
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.collect

class CreateChatActivity :
    BaseActivity<ActivityCreateChatBinding>(ActivityCreateChatBinding::inflate) {

    val viewModel: ChatViewModel by viewModels()
    val adapter: UserAdapter by lazy {
        UserAdapter(userClickListener)
    }
    val currentUser: User? = PrefUtils.with(this).getUser()

    val userClickListener: UserAdapter.UserClickListener = object : UserAdapter.UserClickListener {
        override fun userClicked(user: User) {
            if (currentUser != null) {
                viewModel.createChat(currentUser, user)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        setupUI()
        getCompanyUsers()
    }

    private fun setupUI() {
        binding.apply {
            recyclerview.layoutManager = LinearLayoutManager(this@CreateChatActivity)
            recyclerview.adapter = adapter
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun getCompanyUsers() {
        val user = PrefUtils.with(this).getUser()
        user?.company?.let { viewModel.getCompanyUsers(it) }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.LoadedList -> showList(it.list as List<User>)
                    is ViewModelState.Error -> showToast(it.message)
                    is ViewModelState.LoadedItem -> showChat(it.item as Chat)
                }
            }
        }
    }

    private fun showChat(chat: Chat) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(Const.KEY_CHAT_ID, chat.id)
        startActivity(intent)
        finish()
    }

    private fun showList(list: List<User>) {
        hideProgress()
        adapter.submitList(filterList(list))
    }

    private fun filterList(list: List<User>): List<User> {
        val resultList: MutableList<User> = mutableListOf()
        for (user in list) {
            if (!currentUser?.id.equals(user.id)) {
                resultList.add(user)
            }
        }
        return resultList
    }


    override fun getViewBinding(): ActivityCreateChatBinding {
        return ActivityCreateChatBinding.inflate(layoutInflater)
    }
}