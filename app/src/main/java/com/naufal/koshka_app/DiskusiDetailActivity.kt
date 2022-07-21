package com.naufal.koshka_app

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Comments
import com.naufal.koshka_app.model.Diskusi
import com.naufal.koshka_app.model.User
import kotlinx.android.synthetic.main.activity_adopsi_detail.*
import kotlinx.android.synthetic.main.activity_diskusi_detail.*
import java.lang.Exception

class DiskusiDetailActivity : AppCompatActivity() {
    private lateinit var diskusi:Diskusi
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user_id:String
    private lateinit var user_role:String
    private lateinit var popupMenu: PopupMenu
    private lateinit var dataUser:User
    var commentList=ArrayList<Comments>()
    var refDataDiskusi= FirebaseDatabase.getInstance().getReference("Diskusi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diskusi_detail)
        sharedPreferences=getSharedPreferences("User",0)
        user_id=sharedPreferences.getString("id","").toString()
        user_role=sharedPreferences.getString("role","").toString()
        diskusi= intent.getParcelableExtra<Diskusi>("data")!!
        val storageRef= FirebaseStorage.getInstance().reference.child("diskusi/${diskusi?.image}")

        rv_comment.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_comment.addItemDecoration(DividerItemDecoration(rv_comment.context,DividerItemDecoration.VERTICAL))
        getUserData()
        getCommentData()
        popUpMenu()

        if (diskusi.user_id==user_id||user_role=="Admin"){
            iv_more_detail_diskusi.visibility=View.VISIBLE
            if (diskusi.user_id==user_id){
                btn_contact_user_diskusi.visibility=View.GONE
            }
        }else{
            iv_more_detail_diskusi.visibility=View.GONE
            btn_contact_user_diskusi.visibility=View.VISIBLE
        }

        tv_caption_detail_diskusi.setText(diskusi.caption)

        iv_back_detail_diskusi.setOnClickListener {
            finish()
        }

        iv_more_detail_diskusi.setOnClickListener {
            try {
                val popUp= PopupMenu::class.java.getDeclaredField("mPopup")
                popUp.isAccessible=true
                val menu=popUp.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForce",Boolean::class.java)
                    .invoke(menu,true)
            }catch (e: Exception){
                e.printStackTrace()
            }finally {
                popupMenu.show()
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.edit_adopsi -> {
                        startActivity(Intent(this,EditDiskusiActivity::class.java).putExtra("data",diskusi))
                        finish()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete_adopsi->{
                        var alert= AlertDialog.Builder(this)
                        alert.setTitle("Hapus Data")
                        alert.setPositiveButton("Hapus", DialogInterface.OnClickListener{
                                dialog, which ->
                            if (diskusi.image!=""){
                                storageRef.delete().addOnSuccessListener {
                                    refDataDiskusi.child(diskusi.id.toString()).removeValue()
                                    finish()
                                    Toast.makeText(this,"Data telah dihapus", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                refDataDiskusi.child(diskusi.id.toString()).removeValue()
                                finish()
                                Toast.makeText(this,"Data telah dihapus", Toast.LENGTH_SHORT).show()
                            }

                        })
                        alert.setNegativeButton("Batal", DialogInterface.OnClickListener{
                                dialog, which ->
                            dialog.cancel()
                            dialog.dismiss()
                        })
                        alert.create()
                        alert.show()
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        false
                    }
                }

            }
        }

        btn_send_comment.setOnClickListener {
            if (tv_send_comment.text.toString()!=""){
                var refComment=FirebaseDatabase.getInstance().getReference("Comment")
                var comments=Comments()
                comments.id=refComment.push().key.toString()
                comments.user_id=user_id
                comments.diskusi_id=diskusi.id
                comments.comment=tv_send_comment.text.toString()

                refComment.child(diskusi.id!!).child(comments.id!!).setValue(comments)
                tv_send_comment.setText("")
            }else{
                tv_send_comment.requestFocus()
            }
        }



        btn_contact_user_diskusi.setOnClickListener {
            startActivity(Intent(this,MessageActivity::class.java).putExtra("receiver",dataUser))
        }
    }

    private fun getCommentData() {
        var refComment=FirebaseDatabase.getInstance().getReference("Comment").child(diskusi.id!!)
        refComment.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                for (data in snapshot.children){
                    var comment=data.getValue(Comments::class.java)
                    commentList.add(comment!!)
                }

                rv_comment.adapter=CommentAdapter(commentList,user_id,user_role)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getUserData() {
        var refUser=FirebaseDatabase.getInstance().getReference("User").child(diskusi.user_id!!)
        refUser.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user=snapshot.getValue(User::class.java)
                tv_name_detail_diskusi.setText(user?.name)

                FirebaseStorage.getInstance().getReference("avatar/${user?.avatar}").downloadUrl.addOnCompleteListener {
                    Glide.with(this@DiskusiDetailActivity)
                        .load(it.result)
                        .into(iv_profile_detail_diskusi)
                    iv_profile_detail_diskusi.setBackgroundColor(resources.getColor(R.color.white))
                    dataUser=user!!
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun popUpMenu() {
        popupMenu=PopupMenu(applicationContext,iv_more_detail_diskusi)
        popupMenu.inflate(R.menu.popup_detail_adopsi)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.edit_adopsi->{
                    true
                }
                R.id.delete_adopsi->{
                    true
                }
                else -> {true}
            }
        }
    }
}