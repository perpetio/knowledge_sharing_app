package com.perpetio.knowledgesharingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.model.Comment

class CommentsAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var commentsList: MutableList<Comment> = mutableListOf()

    fun submitList(list: List<Comment>) {
        commentsList.clear()
        commentsList.addAll(list)
        notifyDataSetChanged()
    }

    fun addComment(comment: Comment) {
        commentsList.add(comment)
        notifyItemInserted(commentsList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(commentsList.get(position))
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvatar: ImageView
        private val userName: TextView
        private val comment: TextView

        init {
            userAvatar = itemView.findViewById(R.id.user_avatar)
            userName = itemView.findViewById(R.id.user_name)
            comment = itemView.findViewById(R.id.user_comment)
        }

        fun bind(model: Comment) {
            userName.text = model.userName
            Glide.with(itemView.context)
                .load(model.userAvatar)
                .placeholder(R.drawable.user_placeholder)
                .circleCrop()
                .into(userAvatar)
            comment.text = model.text
        }
    }
}