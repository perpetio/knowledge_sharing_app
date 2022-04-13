package com.perpetio.knowledgesharingapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.perpetio.knowledgesharingapp.model.Feed
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.utils.ConverterUtils
import kotlinx.coroutines.flow.MutableStateFlow

class UserViewModel : BaseLogicViewModel() {

    fun saveImage(user: User) {
        state.value = ViewModelState.Loading
        val storageReference: StorageReference = storage.reference
        user.id?.let { it1 ->
            user.avatarUrl?.let {
                storageReference.child(Const.FOLDER_USERS)
                    .child(it1)
                    .child(Const.AVATAR_FILENAME)
                    .putFile(Uri.parse(it))
                    .addOnFailureListener {
                        state.value = ViewModelState.Error(it.localizedMessage)
                    }
                    .addOnSuccessListener {
                        getImageURl(user, it)
                    }


            }
        }
    }

    private fun getImageURl(user: User, it: UploadTask.TaskSnapshot?) {
        val downloadUri = it?.getStorage()?.getDownloadUrl()
        downloadUri
            ?.addOnSuccessListener {
                val generatedFilePath = downloadUri.getResult().toString()
                user.avatarUrl = generatedFilePath
                updateUserAvatar(user)
            }
            ?.addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun updateUser(user: User) {
        state.value = ViewModelState.Loading

        val update = hashMapOf(
            Const.KEY_NAME to user.name,
            Const.KEY_JOB to user.jobTitle
        )
        db.collection(Const.FOLDER_USERS)
            .document(user.id ?: "")
            .set(update, SetOptions.merge())
            .addOnSuccessListener {
                state.value = ViewModelState.LoadedUser(user)
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun updateUserAvatar(user: User) {
        val update = hashMapOf(
            Const.KEY_AVATAR_URL to user.avatarUrl
        )
        db.collection(Const.FOLDER_USERS)
            .document(user.id ?: "")
            .set(update, SetOptions.merge())
            .addOnSuccessListener {
                state.value = ViewModelState.LoadedUserWithResult(user)
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun updatePassword(password: String, newPassword: String) {
        state.value = ViewModelState.Loading
        val email = auth.currentUser?.email
        val credential: AuthCredential? =
            email?.let { EmailAuthProvider.getCredential(it, password) }
        if (credential != null) {
            auth.currentUser?.reauthenticate(credential)
                ?.addOnFailureListener {
                    state.value = ViewModelState.Error(it.localizedMessage)
                }
                ?.addOnSuccessListener {
                    auth.currentUser?.updatePassword(newPassword)
                        ?.addOnFailureListener {
                            state.value = ViewModelState.Error(it.localizedMessage)
                        }
                        ?.addOnSuccessListener {
                            state.value = ViewModelState.Loaded
                        }
                }
        } else {
            state.value = ViewModelState.Error("No email")
        }
    }

}
