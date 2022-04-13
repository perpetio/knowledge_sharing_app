package com.perpetio.knowledgesharingapp.adapter

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
import com.perpetio.knowledgesharingapp.model.User

class UserAdapter(val listener: UserClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var userList: MutableList<User> = mutableListOf()

    fun submitList(list: List<User>) {
        userList.clear()
        userList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(userList.get(position))
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView
        private val userName: TextView
        private val userJobTitle: TextView
        private val btnCreate: ImageView

        init {
            userAvatar = itemView.findViewById(R.id.user_avatar)
            userName = itemView.findViewById(R.id.user_name)
            userJobTitle = itemView.findViewById(R.id.user_job_title)
            btnCreate = itemView.findViewById(R.id.btn_create_chat)
        }

        fun bind(model: User) {
            val currentUser = PrefUtils.with(itemView.context).getUser()
            if (currentUser != null) {
                if (currentUser.type == 1L) {
                    btnCreate.isVisible = false
                } else {
                    btnCreate.isVisible = true
                }
            }
            userName.text = model.name
            userJobTitle.text = model.jobTitle
            Glide.with(itemView.context)
                .load(model.avatarUrl)
                .placeholder(R.drawable.user_placeholder)
                .circleCrop()
                .into(userAvatar)
            itemView.setOnClickListener {
                listener.userClicked(model)
            }
        }
    }

    interface UserClickListener {
        fun userClicked(user: User)
    }

}