package com.naufal.koshka_app

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Chat
import com.naufal.koshka_app.model.User
import kotlinx.android.synthetic.main.activity_message.*
import java.io.File

class MessageActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var messageList:ArrayList<Chat>
    private lateinit var messageAdapter: MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        var dataReceiver=intent.getParcelableExtra<User>("receiver")
        val storageRef= FirebaseStorage.getInstance().reference.child("avatar/${dataReceiver?.avatar}")
        val localFile= File.createTempFile("tempImage","jpeg")

        sharedPreferences=getSharedPreferences("User",0)
        var user_id=sharedPreferences.getString("id","")

        setSupportActionBar(toolbar_message)
        supportActionBar?.setTitle("")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_message.setNavigationOnClickListener {
            finish()
        }

        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            iv_receiver_profile.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(this,"Failed to get Image", Toast.LENGTH_LONG).show()
        }

        tv_receiver_name.setText(dataReceiver?.name)

        btn_send.setOnClickListener {
            if (tv_send.text.toString()==""){
                tv_send.requestFocus()
            }else{
                sendMessage(user_id,dataReceiver?.id,tv_send.text.toString(),tv_send,System.currentTimeMillis().toString())
            }
        }

        rv_message.setHasFixedSize(true)
        var layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        layoutManager.stackFromEnd=true
        rv_message.layoutManager=layoutManager

        storageRef.downloadUrl.addOnCompleteListener{
            getMessage(user_id!!,dataReceiver,it.result)
        }

    }

    private fun getMessage(sender: String, dataReceiver: User?, avatar: Uri) {
        var refMessage=FirebaseDatabase.getInstance().getReference("Chat")
        messageList= ArrayList<Chat>()
        refMessage.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (dataSnapshot in snapshot.children){
                    var message=dataSnapshot.getValue(Chat::class.java)
                    if (message?.sender.equals(sender) && message?.receiver.equals(dataReceiver?.id)||message?.sender.equals(dataReceiver?.id)&&message?.receiver.equals(sender)){
                        messageList.add(message!!)
                    }
                }

                if (dataReceiver != null) {
                    rv_message.adapter=MessageAdapter(messageList,sender,avatar)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun sendMessage(
        sender: String?,
        receiver: String?,
        message: String,
        tvSend: EditText?,
        time:String
    ) {
        var refChat=FirebaseDatabase.getInstance().getReference("Chat")
        var refSender=FirebaseDatabase.getInstance().getReference("Chatlist").child(sender!!).child(receiver!!)
        var refReceiver=FirebaseDatabase.getInstance().getReference("Chatlist").child(receiver!!).child(sender!!)
        var chat=Chat()
        chat.id=refChat.push().key.toString()
        chat.sender=sender
        chat.receiver=receiver
        chat.message=message
        chat.time=time

        refChat.child(chat.id!!).setValue(chat)

        refSender.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    refSender.child("id").setValue(receiver)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        refReceiver.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    refReceiver.child("id").setValue(sender)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        tvSend?.setText("")

    }
}