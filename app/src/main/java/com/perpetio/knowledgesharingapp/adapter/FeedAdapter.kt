package com.perpetio.knowledgesharingapp.adapter

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.model.Feed

class FeedAdapter(val listener: FeedClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var feedList: MutableList<Feed> = mutableListOf()

    fun submitList(list: List<Feed>) {
        feedList.clear()
        feedList.addAll(list)
        notifyDataSetChanged()
    }

    fun addNewItem(feed: Feed) {
        feedList.add(feed)
        notifyItemInserted(feedList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return feedList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(feedList.get(position))
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView
        private val image: ImageView
        private val userName: TextView
        private val title: TextView
        private val description: TextView
        private val btnLike: ImageView
        private val btnSave: ImageView
        private val link: TextView
        var isPostSaved: Boolean = false
        var isPostLiked: Boolean = false

        init {
            userAvatar = itemView.findViewById(R.id.user_avatar)
            userName = itemView.findViewById(R.id.user_name)
            title = itemView.findViewById(R.id.feed_title)
            description = itemView.findViewById(R.id.feed_description)
            image = itemView.findViewById(R.id.image)
            btnLike = itemView.findViewById(R.id.btn_like)
            btnSave = itemView.findViewById(R.id.btn_save)
            link = itemView.findViewById(R.id.link)
        }

        fun bind(model: Feed) {
            if (model.id != null) {
                setupSaveLogic(model)
                setupLikeLogic(model)
                if (model.link != "") {
                    link.text = model.link
                    link.movementMethod = LinkMovementMethod.getInstance()
                }
                model.userImage.let {
                    Glide.with(itemView.context)
                        .load(model.userImage)
                        .placeholder(R.drawable.user_placeholder)
                        .circleCrop()
                        .into(userAvatar)
                }
                if (model.image != null) {
                    image.isVisible = true
                    Glide.with(itemView.context)
                        .load(model.image)
                        .placeholder(R.drawable.placeholder_photo)
                        .into(image)
                } else {
                    image.isVisible = false
                }
                userName.text = model.userName
                title.text = model.title
                description.text = model.description
                itemView.setOnClickListener {
                    listener.onFeedClick(model)
                }
            }
        }

        private fun setupSaveLogic(model: Feed) {
            val userId = PrefUtils.with(itemView.context).getUser()?.id
            val list = model.usersThatSavedPost
            isPostSaved = list.contains(userId)
            if (isPostSaved) {
                btnSave.setImageResource(R.drawable.ic_bookmark_selected)
            } else {
                btnSave.setImageResource(R.drawable.ic_bookmark_unselected)
            }

            if (userId != null) {
                btnSave.setOnClickListener {
                    if (isPostSaved) {
                        list.remove(userId)
                        model.usersThatSavedPost = list
                        btnSave.setImageResource(R.drawable.ic_bookmark_unselected)
                        listener.onSavedPost(model)
                        isPostSaved = false
                    } else {
                        list.add(userId)
                        model.usersThatSavedPost = list
                        btnSave.setImageResource(R.drawable.ic_bookmark_selected)
                        listener.onSavedPost(model)
                        isPostSaved = true
                    }
                }
            }
        }

        fun setupLikeLogic(model: Feed) {
            val userId = PrefUtils.with(itemView.context).getUser()?.id
            val list = model.usersThatLikedPost
            isPostLiked = list.contains(userId)
            if (isPostLiked) {
                btnLike.setImageResource(R.drawable.ic_like_selected)
            } else {
                btnLike.setImageResource(R.drawable.ic_like_unselected)
            }

            if (userId != null) {
                btnLike.setOnClickListener {
                    if (isPostLiked) {
                        list.remove(userId)
                        model.usersThatLikedPost = list
                        btnLike.setImageResource(R.drawable.ic_like_unselected)
                        listener.onLikedPost(model)
                        isPostLiked = false
                    } else {
                        list.add(userId)
                        model.usersThatLikedPost = list
                        btnLike.setImageResource(R.drawable.ic_like_selected)
                        listener.onLikedPost(model)
                        isPostLiked = true
                    }
                }
            }
        }
    }

    interface FeedClickListener {
        fun onFeedClick(feed: Feed)
        fun onSavedPost(feed: Feed)
        fun onLikedPost(feed: Feed)
    }

}