package com.perpetio.knowledgesharingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.utils.ConverterUtils
import kotlinx.coroutines.flow.MutableStateFlow

open class BaseLogicViewModel : ViewModel() {
    val auth: FirebaseAuth = Firebase.auth

    val db = Firebase.firestore
    val storage: FirebaseStorage = Firebase.storage

    val state: MutableStateFlow<ViewModelState?> = MutableStateFlow(null)

    fun getUserInfo(uid: String) {
        db.collection(Const.FOLDER_USERS).document(uid)
            .get()
            .addOnSuccessListener {
                state.value =
                    ViewModelState.LoadedUser(ConverterUtils.convertSnapshotToUser(it))
            }
            .addOnFailureListener {

            }
    }

    fun getCompanyUsers(company: String) {
        state.value = ViewModelState.Loading
        db.collection(Const.FOLDER_USERS)
            .whereEqualTo(Const.KEY_COMPANY, company)
            .get()
            .addOnSuccessListener {
                state.value =
                    ViewModelState.LoadedList(ConverterUtils.convertSnapshotToUserList(it))
            }
            .addOnFailureListener {
                state.value = ViewModelState.Error(it.localizedMessage)
            }
    }

}