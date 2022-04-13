package com.perpetio.knowledgesharingapp.model

data class User(
    var id: String?,
    val email: String?,
    var name: String?,
    var avatarUrl: String?,
    val type: Long?,
    var jobTitle: String?,
    val company: String?,
): BaseModel()