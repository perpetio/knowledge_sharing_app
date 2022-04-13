package com.perpetio.knowledgesharingapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.activity.ChatActivity
import com.perpetio.knowledgesharingapp.activity.CreateChatActivity
import com.perpetio.knowledgesharingapp.adapter.ChatAdapter
import com.perpetio.knowledgesharingapp.databinding.FragmentChatsBinding
import com.perpetio.knowledgesharingapp.model.Chat
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.collect

class ChatsFragment : BaseFragment<FragmentChatsBinding>() {
    private val adapter: ChatAdapter by lazy {
        ChatAdapter(chatClickListener)
    }
    val chatClickListener: ChatAdapter.ChatClickListener = object : ChatAdapter.ChatClickListener {
        override fun chatClick(chat: Chat) {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra(Const.KEY_CHAT_ID, chat.id)
            startActivity(intent)
        }

    }
    val viewModel: ChatViewModel by activityViewModels()
    var currentUser: User? = null
    val chatList: MutableList<Chat> = mutableListOf()

    override fun onStart() {
        super.onStart()
        currentUser?.let { viewModel.getUserChats(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setupUI()
    }

    private fun setupUI() {
        currentUser = PrefUtils.with(requireContext()).getUser()
        currentUser?.let { adapter.setUser(it) }
        binding.apply {
            recyclerview.layoutManager = LinearLayoutManager(requireContext())
            recyclerview.adapter = adapter
            btnCreateChat.setOnClickListener {
                val intent = Intent(requireContext(), CreateChatActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isChatExistInList(chat: Chat): Boolean {
        for (item in chatList) {
            if (item.id?.equals(chat.id) == true) {
                return true
            }
        }
        return false
    }


    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.Error -> showToast(it.message)
                    is ViewModelState.LoadedItem -> addChat(it.item as Chat)
                    is ViewModelState.LoadedList -> updateList(it.list as List<Chat>)
                }
            }
        }
    }

    private fun updateList(list: List<Chat>) {
        hideProgress()
        adapter.submitList(list)
    }

    private fun addChat(chat: Chat) {
        if (chat.id != null) {
            val isItemExist = isChatExistInList(chat)
            if (!isItemExist) {
                chatList.add(chat)
                adapter.addNewItem(chat)
            }
        }
    }

    override fun getViewBinding(): FragmentChatsBinding {
        return FragmentChatsBinding.inflate(layoutInflater)
    }
}