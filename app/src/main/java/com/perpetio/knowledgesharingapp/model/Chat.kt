package com.perpetio.knowledgesharingapp.model

data class Chat(
    val id: String?,
    val name: String?,
    val image: String?,
    val users: List<String>
): BaseModel()