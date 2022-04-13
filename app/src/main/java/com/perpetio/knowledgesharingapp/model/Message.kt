package com.perpetio.knowledgesharingapp.model

data class Message(
    val id: String?,
    val text: String?,
    val chatId: String?,
    var image: String?,
    val userId: String?,
    val time: Long
) : BaseModel()