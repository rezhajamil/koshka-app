package com.naufal.koshka_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Diskusi
import com.naufal.koshka_app.model.User

class DiskusiAdapter (private var diskusi: ArrayList<Diskusi>): RecyclerView.Adapter<DiskusiAdapter.ViewHolder>(){
    lateinit var contextAdapter: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiskusiAdapter.ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        val inflatedView=layoutInflater.inflate(R.layout.row_item_diskusi,parent,false)
        return DiskusiAdapter.ViewHolder(inflatedView)
    }
    override fun onBindViewHolder(holder: DiskusiAdapter.ViewHolder, position: Int) {
        holder.bindItem(diskusi[position],contextAdapter)
    }

    override fun getItemCount(): Int =diskusi.size
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val tvCaption=view.findViewById<TextView>(R.id.tv_caption_diskusi)
        private val tvComment=view.findViewById<TextView>(R.id.tv_comment)
        private val ivPhoto=view.findViewById<ImageView>(R.id.iv_diskusi)
        private val ivProfile=view.findViewById<ImageView>(R.id.iv_profile_diskusi)
        private val tvName=view.findViewById<TextView>(R.id.tv_name_diskusi)
        private val card=view.findViewById<CardView>(R.id.card_diskusi)
        private val btnComment=view.findViewById<ImageButton>(R.id.btn_comment)


        fun bindItem(diskusi: Diskusi, contextAdapter: Context){
            if (!diskusi.image.equals("")){
                val storageRef= FirebaseStorage.getInstance().reference.child("diskusi/${diskusi.image}")
                storageRef.downloadUrl.addOnCompleteListener {
                    Glide.with(contextAdapter)
                        .load(it.result)
                        .into(ivPhoto)
                    ivPhoto.setBackgroundColor(contextAdapter.resources.getColor(R.color.white))
                }
            }else{
                card.visibility=View.GONE
            }

            tvCaption.setText(diskusi.caption)
            diskusi.user_id?.let { getUser(it,tvName,ivProfile,contextAdapter) }
            getComment(diskusi.id.toString(),tvComment)
        }

        private fun getComment(id: String, tvComment: TextView) {
            var refComment=FirebaseDatabase.getInstance().getReference("Comment").child(id)
            refComment.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count=0
                    for (data in snapshot.children){
                        count++
                    }

                    tvComment.setText(count.toString()+" Comment")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        private fun getUser(
            userId: String,
            tvName: TextView,
            ivProfile: ImageView,
            contextAdapter: Context
        ) {
            var refUser=FirebaseDatabase.getInstance().getReference("User")
                refUser.child(userId).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var user=snapshot.getValue(User::class.java)
                        tvName.setText(user?.name)
                        var storageRefUser= FirebaseStorage.getInstance().reference.child("avatar/${user?.avatar}")
                        storageRefUser.downloadUrl.addOnCompleteListener {
                            Glide.with(contextAdapter)
                                .load(it.result)
                                .into(ivProfile)
                            ivProfile.setBackgroundColor(contextAdapter.resources.getColor(R.color.white))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }
    }