package com.perpetio.knowledgesharingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.model.Chat
import com.perpetio.knowledgesharingapp.model.User

class ChatAdapter(val listener: ChatClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var chatList: MutableList<Chat> = mutableListOf()
    var currentUser: User? = null

    fun setUser(user: User) {
        currentUser = user
    }

    fun submitList(list: List<Chat>) {
        chatList = list as MutableList<Chat>
        notifyDataSetChanged()
    }

    fun addNewItem(chat: Chat) {
        chatList.add(chat)
        notifyItemInserted(chatList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(chatList.get(position))
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView
        private val userName: TextView

        init {
            userAvatar = itemView.findViewById(R.id.user_avatar)
            userName = itemView.findViewById(R.id.user_name)
        }

        fun bind(model: Chat) {
            val currentUserName = currentUser?.name
            val listNames = model.name?.split("_")
            if (listNames != null) {
                for (name in listNames) {
                    if (!name.equals(currentUserName)) {
                        userName.text = name
                    }
                }
            }

            val currentUserAvatar = currentUser?.avatarUrl
            val listAvatars = model.image?.split("_")
            if (listAvatars != null) {
                for (image in listAvatars) {
                    if (!image.equals(currentUserAvatar)) {
                        Glide.with(itemView.context)
                            .load(image)
                            .placeholder(R.drawable.user_placeholder)
                            .circleCrop()
                            .into(userAvatar)
                    }
                }
            }
            itemView.setOnClickListener { listener.chatClick(model) }
        }
    }

    interface ChatClickListener {
        fun chatClick(chat: Chat)
    }

}