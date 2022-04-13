package com.perpetio.knowledgesharingapp.viewmodel

import com.perpetio.knowledgesharingapp.model.*

sealed class ViewModelState {
    object Loading : ViewModelState()
    object Loaded : ViewModelState()
    data class LoadedList(val list: List<BaseModel>) : ViewModelState()
    data class LoadedItem(val item: BaseModel) : ViewModelState()
    data class LoadedUser(val user: User) : ViewModelState()
    data class Error(val message: String) : ViewModelState()
    class LoadedUserWithResult(val user: User) : ViewModelState() {

    }
}