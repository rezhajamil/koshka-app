package com.naufal.koshka_app

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naufal.koshka_app.model.Chatlist
import com.naufal.koshka_app.model.User
import kotlinx.android.synthetic.main.activity_message_list.*

class MessageListActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user_id:String
    private lateinit var progressDialog: ProgressDialog
    var chatList=ArrayList<Chatlist>()
    var userList=ArrayList<User>()
    var refChatList=FirebaseDatabase.getInstance().getReference("Chatlist")
    var refUser=FirebaseDatabase.getInstance().getReference("User")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)
        sharedPreferences=getSharedPreferences("User",0)
        user_id=sharedPreferences.getString("id","").toString()

        rv_chat.setHasFixedSize(true)
        rv_chat.setLayoutManager(LinearLayoutManager(this))
        val dividerItemDecoration =
            DividerItemDecoration(rv_chat.getContext(), DividerItemDecoration.VERTICAL)
        rv_chat.addItemDecoration(dividerItemDecoration)

        iv_back_chat.setOnClickListener {
            finish()
        }


        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Mengambil Data...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        refChatList.child(user_id).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapshot in snapshot.children){
                    var chat=dataSnapshot.getValue(Chatlist::class.java)
                    chatList.add(chat!!)
                }

                getChatList(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
            }

        })
    }

    private fun getChatList(chatList: ArrayList<Chatlist>) {
        refUser.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    for (chat in chatList) {
                        if (chat.id==user?.id){
                            userList.add(user!!)
                        }
                    }
                }

                rv_chat.adapter=MessageListAdapter(userList){
                    startActivity(Intent(this@MessageListActivity,MessageActivity::class.java).putExtra("receiver",it))
                }

                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
            }

        })
    }
}