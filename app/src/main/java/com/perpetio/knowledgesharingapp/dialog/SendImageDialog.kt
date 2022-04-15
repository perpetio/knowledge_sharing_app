package com.perpetio.knowledgesharingapp.dialog

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.bumptech.glide.Glide
import com.perpetio.knowledgesharingapp.R

class SendImageDialog(context: Context, val text: String, val url: String) :
    AppCompatDialog(context) {
    private lateinit var textView: TextView
    private lateinit var btnSend: TextView
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_send_image)
        textView = findViewById(R.id.text)!!
        image = findViewById(R.id.image)!!
        btnSend = findViewById(R.id.btn_send)!!

        textView.text = text
        Glide.with(context)
            .load(url)
            .centerCrop()
            .into(image)

        btnSend.setOnClickListener {
            dismiss()
        }
    }
}