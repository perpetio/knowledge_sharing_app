package com.perpetio.knowledgesharingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.model.Chat
import com.perpetio.knowledgesharingapp.model.Message
import com.perpetio.knowledgesharingapp.model.User

class MessagesAdapter(val listener: MessageClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var messagesList: MutableList<Message> = mutableListOf()
    var recipientUser: User? = null

    fun submitList(list: List<Message>) {
        messagesList.clear()
        messagesList.addAll(list)
        notifyDataSetChanged()
    }

    fun addNewItem(message: Message) {
        messagesList.add(message)
        notifyItemInserted(messagesList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(messagesList.get(position))
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView
        private val userName: TextView
        private val layoutRightMessage: ViewGroup
        private val layoutLeftMessage: ViewGroup
        private val text: TextView
        private val textRight: TextView
        private val image: ImageView
        private val imageRight: ImageView

        init {
            userAvatar = itemView.findViewById(R.id.user_avatar)
            userName = itemView.findViewById(R.id.user_name)
            layoutLeftMessage = itemView.findViewById(R.id.layout_left_message)
            layoutRightMessage = itemView.findViewById(R.id.layout_right_message)
            text = itemView.findViewById(R.id.text)
            textRight = itemView.findViewById(R.id.textRight)
            image = itemView.findViewById(R.id.image)
            imageRight = itemView.findViewById(R.id.imageRight)
        }

        fun bind(model: Message) {
            val userId = model.userId
            val oponentUserId = recipientUser?.id
            if (userId.equals(oponentUserId)) {
                layoutLeftMessage.isVisible = true
                layoutRightMessage.isVisible = false
                if (model.image == null) {
                    image.isVisible = false
                } else {
                    val url: String? = model.image
                    image.isVisible = true
                    Glide.with(itemView.context)
                        .load(url)
                        .placeholder(R.drawable.placeholder_photo)
                        .into(image)
                    image.setOnClickListener {
                        if (url != null) {
                            listener.onImageClick(url)
                        }
                    }
                }
                text.text = model.text

            } else {
                layoutLeftMessage.isVisible = false
                layoutRightMessage.isVisible = true
                if (model.image == null) {
                    imageRight.isVisible = false
                } else {
                    val url = model.image
                    imageRight.isVisible = true
                    Glide.with(itemView.context)
                        .load(url)
                        .placeholder(R.drawable.placeholder_photo)
                        .into(imageRight)
                    imageRight.setOnClickListener {
                        if (url != null) {
                            listener.onImageClick(url)
                        }
                    }
                }
                textRight.text = model.text
            }
            userName.text = recipientUser?.name
            Glide.with(itemView.context)
                .load(recipientUser?.avatarUrl)
                .placeholder(R.drawable.user_placeholder)
                .circleCrop()
                .into(userAvatar)

            userAvatar.setOnClickListener {
                if (oponentUserId != null) {
                    listener.onProfileClick(oponentUserId)
                }
            }

        }
    }

    interface MessageClickListener {
        fun onImageClick(url: String)
        fun onProfileClick(userId: String)
    }

}