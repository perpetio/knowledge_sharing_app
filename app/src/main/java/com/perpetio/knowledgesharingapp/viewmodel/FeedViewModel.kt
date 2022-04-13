package com.perpetio.knowledgesharingapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.perpetio.knowledgesharingapp.model.Comment
import com.perpetio.knowledgesharingapp.model.Feed
import com.perpetio.knowledgesharingapp.model.Message
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.utils.ConverterUtils
import kotlinx.coroutines.flow.MutableStateFlow

class FeedViewModel : BaseLogicViewModel() {

    fun createFeed(feed: Feed) {
        state.value = ViewModelState.Loading
        if (feed.image != null) {
            saveImage(feed)
        }
        val data = hashMapOf(
            Const.KEY_TITLE to feed.title,
            Const.KEY_DESCRIPTION to feed.description,
            Const.KEY_USER_IMAGE to feed.userImage,
            Const.KEY_USER_NAME to feed.userName,
            Const.KEY_USER_ID to feed.userId,
            Const.KEY_IMAGE to feed.image,
            Const.KEY_COMPANY to feed.company,
            Const.KEY_TIME to feed.time,
            Const.KEY_LINK to feed.link,
            Const.KEY_SAVED_USERS_LIST to feed.usersThatSavedPost,
            Const.KEY_LIKED_USERS_LIST to feed.usersThatLikedPost
        )
        db.collection(Const.FOLDER_POSTS)
            .add(data)
            .addOnSuccessListener {
                state.value = ViewModelState.Loaded
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun getFeedById(id: String) {
        state.value = ViewModelState.Loading
        db.collection(Const.FOLDER_POSTS)
            .document(id)
            .get()
            .addOnSuccessListener {
                state.value = ViewModelState.LoadedItem(ConverterUtils.convertSnapshotToFeed(it))
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun getSavedPosts(userId: String) {
        state.value = ViewModelState.Loading
        db.collection(Const.FOLDER_POSTS)
            .whereArrayContains(Const.KEY_SAVED_USERS_LIST, userId)
            .orderBy(Const.KEY_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                state.value =
                    ViewModelState.LoadedList(ConverterUtils.convertSnapshotToFeedList(it))
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun saveImage(feed: Feed) {
        state.value = ViewModelState.Loading
        val storageReference: StorageReference = storage.reference
        feed.id?.let { it1 ->
            feed.image?.let {
                storageReference.child(Const.FOLDER_POSTS)
                    .child(it1)
                    .child(Const.AVATAR_FILENAME)
                    .putFile(Uri.parse(it))
                    .addOnFailureListener {
                        state.value = ViewModelState.Error(it.localizedMessage)
                    }
                    .addOnSuccessListener {
                        getImageURl(feed, it)
                    }
            }
        }
    }

    private fun getImageURl(feed: Feed, taskSnapshot: UploadTask.TaskSnapshot) {
        val downloadUri = taskSnapshot.getStorage().getDownloadUrl()
        downloadUri
            .addOnSuccessListener {
                val generatedFilePath = downloadUri.getResult().toString()
                feed.image = generatedFilePath
                updateFeedImage(feed)
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }


    fun updateFeedImage(feed: Feed) {
        val data = hashMapOf(
            Const.KEY_IMAGE to feed.image
        )
        db.collection(Const.FOLDER_POSTS)
            .document(feed.id ?: "")
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                state.value = ViewModelState.Loaded
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun getFeedList(company: String) {
        state.value = ViewModelState.Loading
        db.collection(Const.FOLDER_POSTS)
            .whereEqualTo(Const.KEY_COMPANY, company)
            .orderBy(Const.KEY_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                state.value =
                    ViewModelState.LoadedList(ConverterUtils.convertSnapshotToFeedList(it))
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun createComment(text: String, user: User, postId: String) {
        state.value = ViewModelState.Loading
        val comment = Comment(
            "",
            text,
            user.name,
            user.avatarUrl.toString(),
            postId
        )
        val data = hashMapOf(
            Const.KEY_TEXT to text,
            Const.KEY_USER_NAME to user.name,
            Const.KEY_USER_AVATAR to user.avatarUrl,
            Const.KEY_POST_ID to postId
        )
        db.collection(Const.FOLDER_COMMENTS)
            .add(data)
            .addOnSuccessListener {
                state.value = ViewModelState.LoadedItem(comment)
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun getCommentsForPost(feed: Feed) {
        state.value = ViewModelState.Loading
        db.collection(Const.FOLDER_COMMENTS)
            .whereEqualTo(Const.KEY_POST_ID, feed.id)
            .get()
            .addOnSuccessListener {
                state.value =
                    ViewModelState.LoadedList(ConverterUtils.convertSnapshotToCommentsList(it))
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun addOrRemoveFeedAsSaved(feed: Feed) {
        val update = hashMapOf(
            Const.KEY_SAVED_USERS_LIST to feed.usersThatSavedPost
        )
        feed.id?.let {
            db.collection(Const.FOLDER_POSTS)
                .document(it)
                .set(update, SetOptions.merge())
                .addOnSuccessListener {
                    state.value = ViewModelState.Loaded
                }
                .addOnFailureListener {
                    state.value = ViewModelState.Error(it.localizedMessage)
                }
        }
    }

    fun likeOrUnLikeFeed(feed: Feed) {
        val update = hashMapOf(
            Const.KEY_LIKED_USERS_LIST to feed.usersThatLikedPost
        )
        feed.id?.let {
            db.collection(Const.FOLDER_POSTS)
                .document(it)
                .set(update, SetOptions.merge())
                .addOnSuccessListener {
                    state.value = ViewModelState.Loaded
                }
                .addOnFailureListener {
                    state.value = ViewModelState.Error(it.localizedMessage)
                }
        }
    }

}