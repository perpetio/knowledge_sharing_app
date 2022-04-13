package com.perpetio.knowledgesharingapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.perpetio.knowledgesharingapp.model.Chat
import com.perpetio.knowledgesharingapp.model.Message
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.utils.ConverterUtils
import kotlinx.coroutines.flow.MutableStateFlow

class ChatViewModel : BaseLogicViewModel() {

    fun createChat(currentUser: User, recipientUser: User) {
        state.value = ViewModelState.Loading
        val chatId = currentUser.id.plus(".").plus(recipientUser.id)
        val name = currentUser.name.plus("_").plus(recipientUser.name)
        val image = currentUser.avatarUrl.plus("_").plus(recipientUser.avatarUrl)
        val userList = mutableListOf<String>()
        userList.add(currentUser.id ?: "")
        userList.add(recipientUser.id ?: "")

        val chat = hashMapOf(
            Const.KEY_ID to chatId,
            Const.KEY_NAME to name,
            Const.KEY_IMAGE to image,
            Const.KEY_USER_LIST to userList
        )
        val chatModel = Chat(chatId, name, image, userList)

        db.collection(Const.FOLDER_CHATS)
            .document(chatId)
            .set(chat)
            .addOnSuccessListener {
                state.value = ViewModelState.LoadedItem(chatModel)
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun getUserChats(user: User) {
        state.value = ViewModelState.Loading
        val id = user.id
        if (id != null) {
            db.collection(Const.FOLDER_CHATS)
                .whereArrayContains(Const.KEY_USER_LIST, id)
                .get()
                .addOnSuccessListener {
                    state.value =
                        ViewModelState.LoadedList(ConverterUtils.convertSnapshotToChatList(it))
                }
                .addOnFailureListener {
                    state.value = ViewModelState.Error(it.localizedMessage)
                }
        }
    }

    fun createChatMessage(message: Message, user: User) {
        if (message.image != null) {
            saveImage(message)
        }
        val data = hashMapOf(
            Const.KEY_TEXT to message.text,
            Const.KEY_CHAT_ID to message.chatId,
            Const.KEY_IMAGE to message.image,
            Const.KEY_USER_ID to message.userId,
            Const.KEY_TIME to message.time
        )

        db.collection(Const.FOLDER_MESSAGES)
            .add(data)
            .addOnSuccessListener {
                state.value = ViewModelState.LoadedItem(message)
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun addMessagesListener(chatId: String) {
        db.collection(Const.FOLDER_MESSAGES)
            .whereEqualTo(Const.KEY_CHAT_ID, chatId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    state.value =
                        ViewModelState.LoadedList(
                            ConverterUtils.convertSnapshotToMessageList(
                                snapshot
                            )
                        )
                    return@addSnapshotListener
                }
            }
    }

    fun getMessages(chatId: String) {
        state.value = ViewModelState.Loading
        db.collection(Const.FOLDER_MESSAGES)
            .whereEqualTo(Const.KEY_CHAT_ID, chatId)
            .orderBy(Const.KEY_TIME)
            .get()
            .addOnSuccessListener {
                state.value =
                    ViewModelState.LoadedList(
                        ConverterUtils.convertSnapshotToMessageList(
                            it
                        )
                    )
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }

    }

    fun saveImage(message: Message) {
        state.value = ViewModelState.Loading
        val storageReference: StorageReference = storage.reference
        message.id?.let { it1 ->
            message.image?.let {
                storageReference.child(Const.FOLDER_MESSAGES)
                    .child(it1.toString())
                    .child(Const.AVATAR_FILENAME)
                    .putFile(Uri.parse(it))
                    .addOnFailureListener {
                        state.value = ViewModelState.Error(it.localizedMessage)
                    }
                    .addOnSuccessListener {
                        getImageURl(message, it)
                    }


            }
        }
    }

    private fun getImageURl(message: Message, taskSnapshot: UploadTask.TaskSnapshot) {
        val downloadUri = taskSnapshot.getStorage().getDownloadUrl()
        downloadUri
            .addOnSuccessListener {
                val generatedFilePath = downloadUri.getResult().toString()
                message.image = generatedFilePath
                updateMessageImage(message)
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun updateMessageImage(message: Message) {
        val data = hashMapOf(
            Const.KEY_IMAGE to message.image,
        )
        db.collection(Const.FOLDER_MESSAGES)
            .document(message.id ?: "")
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                state.value = ViewModelState.Loaded
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun getRecipientUser(currentUserId: String, chatId: String) {
        db.collection(Const.FOLDER_CHATS)
            .document(chatId)
            .get()
            .addOnSuccessListener {
                val chat = ConverterUtils.convertSnapshotToChat(it)
                var recipientUserId = ""
                for (id in chat.users) {
                    if (!id.equals(currentUserId)) {
                        recipientUserId = id
                    }
                }
                getUserInfo(recipientUserId)
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }
}