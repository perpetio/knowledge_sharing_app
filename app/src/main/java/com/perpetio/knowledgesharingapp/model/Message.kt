package com.perpetio.knowledgesharingapp.model

data class Message(
    var id: String?,
    val text: String?,
    val chatId: String?,
    var image: String?,
    val userId: String?,
    val time: Long
) : BaseModel()