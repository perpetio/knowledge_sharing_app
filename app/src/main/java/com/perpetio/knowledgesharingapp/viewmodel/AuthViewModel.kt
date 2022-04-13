package com.perpetio.knowledgesharingapp.viewmodel

import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const

class AuthViewModel:BaseLogicViewModel() {
    fun registerUser(user: User, password: String) {
        state.value = ViewModelState.Loading
        user.email?.let {
            auth.createUserWithEmailAndPassword(
                it, password
            ).addOnSuccessListener {
                val firebaseUser = auth.currentUser
                val uid = firebaseUser?.uid
                if (uid != null) {
                    user.id = uid
                    createCustomUserModel(uid, user)
                } else {
                    state.value =
                        ViewModelState.Error("Invalid user uid, cannot upload to Firebase Database")
                }

            }.addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
        }
    }

    private fun createCustomUserModel(
        uid: String,
        user: User
    ) {
        val data = hashMapOf(
            Const.KEY_ID to uid,
            Const.KEY_NAME to user.name,
            Const.KEY_EMAIL to user.email,
            Const.KEY_AVATAR_URL to null,
            Const.KEY_TYPE to user.type,
            Const.KEY_JOB to user.jobTitle,
            Const.KEY_COMPANY to user.company,
        )
        db.collection(Const.FOLDER_USERS)
            .document(uid)
            .set(data)
            .addOnSuccessListener {
                state.value = ViewModelState.LoadedUser(user)
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

    fun login(email: String, password: String) {
        state.value = ViewModelState.Loading
        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                val firebaseUser = auth.currentUser
                val uid = firebaseUser?.uid
                if (uid != null) {
                    getUserInfo(uid)
                }
            } else {
                state.value = it.exception?.localizedMessage?.let { it1 ->
                    ViewModelState.Error(
                        it1
                    )
                }
            }
        }
    }
}