package com.perpetio.knowledgesharingapp.model

data class Comment(
    val id: String?,
    val text: String?,
    val userName: String?,
    val userAvatar: String?,
    val postId: String?
) : BaseModel()