package com.naufal.koshka_app

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.naufal.koshka_app.model.Chat
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private var message: ArrayList<Chat>,
                     private var sender:String,
                     private var receiverAvatar: Uri
                    ): RecyclerView.Adapter<MessageAdapter.ViewHolder>(){
    lateinit var contextAdapter: Context

    private val msg_left = 0
    private val msg_right = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        Log.v("message2",message.toString())
        if (viewType==msg_right){
            val inflatedView=layoutInflater.inflate(R.layout.chat_item_right,parent,false)
            return ViewHolder(inflatedView)
        }else{
            val inflatedView=layoutInflater.inflate(R.layout.chat_item_left,parent,false)
            return ViewHolder(inflatedView)
        }

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(message[position],contextAdapter,receiverAvatar)
    }

    override fun getItemCount(): Int =message.size
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var show_message = itemView.findViewById<TextView?>(R.id.show_message)
        var profile_image = itemView.findViewById<android.widget.ImageView?>(R.id.profile_image)
        var txt_seen = itemView.findViewById<TextView?>(R.id.txt_seen)
        var time_tv = itemView.findViewById<TextView?>(R.id.time_tv)
        fun bindItem(chat: Chat, contextAdapter: Context, avatar: Uri){
            show_message.setText(chat.message)
            time_tv.setText(chat.time?.let { convertTime(it) })

            Log.v("avatar",avatar.toString())
            Glide.with(contextAdapter)
                .load(avatar)
                .into(profile_image)
            profile_image.setBackgroundColor(contextAdapter.resources.getColor(R.color.white))

        }

        fun convertTime(time: String): String? {
            val formatter = SimpleDateFormat("h:mm a")
            return formatter.format(Date(time.toLong()))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (message.get(position).sender==sender){
            Log.v("message","right")
            return msg_right
        }else{
            Log.v("message","left")
            return msg_left
        }
    }

}