package com.naufal.koshka_app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Chat
import com.naufal.koshka_app.model.User

class MessageListAdapter (private var user: ArrayList<User>,
                     private val listener:(User)->Unit): RecyclerView.Adapter<MessageListAdapter.ViewHolder>(){
    lateinit var contextAdapter: Context
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sender:String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListAdapter.ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        sharedPreferences=contextAdapter.getSharedPreferences("User",0)
        sender=sharedPreferences.getString("id","").toString()
        val inflatedView=layoutInflater.inflate(R.layout.row_chat_item,parent,false)
        return MessageListAdapter.ViewHolder(inflatedView)
    }
    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        holder.bindItem(user[position],listener,contextAdapter,sender)
    }

    override fun getItemCount(): Int =user.size
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val tvNama=view.findViewById<TextView>(R.id.tv_name_chat)
        private val tvLast=view.findViewById<TextView>(R.id.tv_last_msg)
        private val ivPhoto=view.findViewById<ImageView>(R.id.profile_image)


        fun bindItem(user: User, listener: (User) -> Unit, contextAdapter: Context, sender: String){
            val storageRef= FirebaseStorage.getInstance().reference.child("avatar/${user.avatar}")
            storageRef.downloadUrl.addOnCompleteListener {
                Glide.with(contextAdapter)
                    .load(it.result)
                    .into(ivPhoto)
            }

            lastMessage(sender,user.id!!,tvLast)

            tvNama.setText(user.name)

            itemView.setOnClickListener {
                listener(user)
            }
        }

        private fun lastMessage(sender:String,receiver: String, tvLast: TextView) {
            var refChat=FirebaseDatabase.getInstance().getReference("Chat")
            var theLastMessage=""
            refChat.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot in snapshot.children) {
                        val chat = dataSnapshot.getValue(Chat::class.java)
                        if (chat != null) {
                            if (chat.receiver.equals(receiver) && chat.sender
                                    .equals(sender) ||
                                chat.receiver.equals(sender) && chat.sender
                                    .equals(receiver)
                            ) {
                                theLastMessage = chat.message.toString()
                                Log.v("gagal7",theLastMessage)
                            }
                        }
                    }

                    tvLast.setText(theLastMessage)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

}