package com.perpetio.knowledgesharingapp.model

data class Feed(
    val id: String?,
    val title: String?,
    val description: String?,
    val userImage: String?,
    val userName: String?,
    val userId: String?,
    var image: String?,
    val company: String?,
    val time: Long,
    val link: String?,
    var usersThatSavedPost: MutableList<String>,
    var usersThatLikedPost: MutableList<String>
) : BaseModel()