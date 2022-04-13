package com.perpetio.knowledgesharingapp.utils

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.perpetio.knowledgesharingapp.model.*

object ConverterUtils {

    fun convertSnapshotToUserList(data: QuerySnapshot?): List<User> {
        val resultList = mutableListOf<User>()
        if (data != null) {
            for (document in data.documents) {
                val user = convertSnapshotToUser(document)
                resultList.add(user)
            }
        }
        return resultList
    }

    fun convertSnapshotToUser(document: DocumentSnapshot?): User {
        val id = document?.id
        val name = document?.getString(Const.KEY_NAME)
        val email = document?.getString(Const.KEY_EMAIL)
        val avatarUrl = document?.getString(Const.KEY_AVATAR_URL)
        val type = document?.getLong(Const.KEY_TYPE)
        val jobTitle = document?.getString(Const.KEY_JOB)
        val company = document?.getString(Const.KEY_COMPANY)
        return User(
            id,
            email,
            name,
            avatarUrl,
            type,
            jobTitle,
            company,
        )
    }


    fun convertSnapshotToChat(document: DocumentSnapshot?): Chat {
        val id = document?.id
        val name = document?.getString(Const.KEY_NAME)
        val image = document?.getString(Const.KEY_IMAGE)
        val users = document?.get(Const.KEY_USER_LIST)
        return Chat(
            id,
            name,
            image,
            users as List<String>
        )
    }

    fun convertSnapshotToMessage(document: DocumentSnapshot?): Message {
        val id: String? = document?.id
        val text: String? = document?.getString(Const.KEY_TEXT)
        val chatId: String? = document?.getString(Const.KEY_CHAT_ID)
        val image: String? = document?.getString(Const.KEY_IMAGE)
        val userId: String? = document?.getString(Const.KEY_USER_ID)
        val time: Long = document?.getLong(Const.KEY_TIME) ?: 0

        return Message(
            id, text, chatId, image, userId, time
        )
    }


    fun convertSnapshotToFeed(document: DocumentSnapshot?): Feed {
        val id: String? = document?.id
        val title: String? = document?.getString(Const.KEY_TITLE)
        val description: String? = document?.getString(Const.KEY_DESCRIPTION)
        val image: String? = document?.getString(Const.KEY_IMAGE)
        val userImage: String? = document?.getString(Const.KEY_USER_IMAGE)
        val userName: String? = document?.getString(Const.KEY_USER_NAME)
        val userId: String? = document?.getString(Const.KEY_USER_ID)
        val company: String? = document?.getString(Const.KEY_COMPANY)
        val time: Long = document?.getLong(Const.KEY_TIME) ?: 0
        val link: String? = document?.getString(Const.KEY_LINK)
        val likes = document?.get(Const.KEY_LIKED_USERS_LIST)
        val saved = document?.get(Const.KEY_SAVED_USERS_LIST)
        return Feed(
            id,
            title,
            description,
            userImage,
            userName,
            userId,
            image,
            company,
            time,
            link,
            saved as MutableList<String>,
            likes as MutableList<String>
        )
    }

    fun convertSnapshotToFeedList(data: QuerySnapshot?): List<Feed> {
        val resultList = mutableListOf<Feed>()
        if (data != null) {
            for (document in data.documents) {
                val feed = convertSnapshotToFeed(document)
                resultList.add(feed)
            }
        }
        return resultList
    }


    fun convertSnapshotToChatList(data: QuerySnapshot?): List<Chat> {
        val resultList = mutableListOf<Chat>()
        if (data != null) {
            for (document in data.documents) {
                val cha = convertSnapshotToChat(document)
                resultList.add(cha)
            }
        }
        return resultList
    }


    fun convertSnapshotToMessageList(data: QuerySnapshot?): List<Message> {
        val resultList = mutableListOf<Message>()
        if (data != null) {
            for (document in data.documents) {
                val message = convertSnapshotToMessage(document)
                resultList.add(message)
            }
        }
        return resultList
    }

    fun convertSnapshotToCommentsList(data: QuerySnapshot?): List<Comment> {
        val resultList = mutableListOf<Comment>()
        if (data != null) {
            for (document in data.documents) {
                val comment = convertSnapshotToComment(document)
                resultList.add(comment)
            }
        }
        return resultList
    }

    private fun convertSnapshotToComment(document: DocumentSnapshot?): Comment {
        val id: String? = document?.id
        val text: String? = document?.getString(Const.KEY_TEXT)
        val userImage: String? = document?.getString(Const.KEY_USER_IMAGE)
        val userName: String? = document?.getString(Const.KEY_USER_NAME)
        val postId: String? = document?.getString(Const.KEY_POST_ID)

        return Comment(
            id, text, userName, userImage, postId
        )
    }
}