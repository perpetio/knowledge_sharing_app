package com.perpetio.knowledgesharingapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.adapter.MessagesAdapter
import com.perpetio.knowledgesharingapp.databinding.ActivityChatBinding
import com.perpetio.knowledgesharingapp.dialog.SendImageDialog
import com.perpetio.knowledgesharingapp.model.Message
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.collect

class ChatActivity : PhotoPickerActivity<ActivityChatBinding>(ActivityChatBinding::inflate) {
    val viewModel: ChatViewModel by viewModels()
    var chatId: String? = null
    val user: User? = PrefUtils.with(this).getUser()

    private val messageClickListener: MessagesAdapter.MessageClickListener =
        object : MessagesAdapter.MessageClickListener {
            override fun onImageClick(url: String) {
                val intent = Intent(this@ChatActivity, ImagePreviewActivity::class.java)
                intent.putExtra(Const.KEY_IMAGE, url)
                startActivity(intent)
            }

            override fun onProfileClick(userId: String) {
                val intent = Intent(this@ChatActivity, PreviewProfileActivity::class.java)
                intent.putExtra(Const.KEY_USER_ID, userId)
                startActivity(intent)
            }

        }
    val adapter: MessagesAdapter = MessagesAdapter(messageClickListener)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getExtraInfo()
        setObservers()
        setupUI()
    }

    private fun getExtraInfo() {
        chatId = intent.extras?.getString(Const.KEY_CHAT_ID)
        chatId?.let {
            viewModel.addMessagesListener(it)
            user?.id?.let { it1 -> viewModel.getRecipientUser(it1, it) }
        }
    }

    private fun setupUI() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            recyclerview.layoutManager = LinearLayoutManager(this@ChatActivity)
            recyclerview.adapter = adapter
            btnSend.setOnClickListener {
                val comment = etMessage.text.toString()
                if (comment.isNotEmpty()) {
                    val message =
                        Message("", comment, chatId, null, user?.id, System.currentTimeMillis())
                    if (user != null) {
                        viewModel.createChatMessage(message)
                    }
                    etMessage.setText("")
                    hideKeyboard()
                }
            }
            btnSendImage.setOnClickListener {
                showPickerDialog()
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    is ViewModelState.Loading -> showProgress()
                    is ViewModelState.LoadedList -> showList(it.list as List<Message>)
                    is ViewModelState.LoadedItem -> addNewMessage(it.item as Message)
                    is ViewModelState.Error -> showToast(it.message)
                    is ViewModelState.LoadedUser -> setRecipientInfo(it.user)
                }
            }
        }
    }

    private fun addNewMessage(message: Message) {
        hideProgress()
        adapter.addNewItem(message)
        binding.recyclerview.layoutManager?.scrollToPosition(adapter.itemCount - 1)
    }

    private fun setRecipientInfo(user: User) {
        adapter.recipientUser = user
        viewModel.getMessages(chatId!!)
        binding.toolbar.title = user.name

    }


    private fun showList(list: List<Message>) {
        adapter.submitList(list)
        binding.recyclerview.layoutManager?.scrollToPosition(adapter.itemCount - 1)
        hideProgress()
    }


    override fun getViewBinding(): ActivityChatBinding {
        return ActivityChatBinding.inflate(layoutInflater)
    }

    override fun openImage(data: Uri?) {
        val dialog = SendImageDialog(this, binding.etMessage.text.toString(), data.toString())
        dialog.setOnDismissListener {
            val message =
                Message(
                    "",
                    binding.etMessage.text.toString(),
                    chatId,
                    data.toString(),
                    user?.id,
                    System.currentTimeMillis()
                )
            viewModel.createChatMessage(message)
        }
        dialog.show()
    }
}