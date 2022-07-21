package com.naufal.koshka_app

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Comments
import com.naufal.koshka_app.model.User

class CommentAdapter(
    private var comment: ArrayList<Comments>,
    val user_id: String,
    val user_role: String
): RecyclerView.Adapter<CommentAdapter.ViewHolder>(){
    lateinit var contextAdapter: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        val inflatedView=layoutInflater.inflate(R.layout.row_item_comment,parent,false)
        return CommentAdapter.ViewHolder(inflatedView)
    }
    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        holder.bindItem(comment[position],user_id,user_role,contextAdapter)
    }

    override fun getItemCount(): Int =comment.size
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
//        private val tvCaption=view.findViewById<TextView>(R.id.tv_caption_comment)
        private val tvComment=view.findViewById<TextView>(R.id.tv_comment_comment)
//        private val ivPhoto=view.findViewById<ImageView>(R.id.iv_comment)
        private val ivProfile=view.findViewById<ImageView>(R.id.iv_profile_comment)
        private val tvName=view.findViewById<TextView>(R.id.tv_name_comment)
//        private val card=view.findViewById<CardView>(R.id.card_comment)
//        private val btnComment=view.findViewById<ImageButton>(R.id.btn_comment)


        fun bindItem(comment: Comments, user_id: String, user_role: String, contextAdapter: Context){
            tvComment.setText(comment.comment)
            getUser(comment.user_id!!,tvName,ivProfile,contextAdapter)
            var alert= AlertDialog.Builder(contextAdapter)
            alert.setTitle("Hapus Komentar")
            alert.setPositiveButton("Hapus", DialogInterface.OnClickListener{
                    dialog, which ->
                FirebaseDatabase.getInstance().getReference("Comment").child(comment.diskusi_id.toString()).child(comment.id.toString()).removeValue()
                Toast.makeText(contextAdapter,"Komentar telah dihapus", Toast.LENGTH_SHORT).show()
            })
            alert.setNegativeButton("Batal", DialogInterface.OnClickListener{
                    dialog, which ->
                dialog.cancel()
                dialog.dismiss()
            })
            alert.create()
            itemView.setOnClickListener {
                if (user_id==comment.user_id||user_role=="Admin"){
                    alert.show()
                }
            }
        }

        private fun getUser(
            userId: String,
            tvName: TextView,
            ivProfile: ImageView,
            contextAdapter: Context
        ) {
            var refUser= FirebaseDatabase.getInstance().getReference("User")
            refUser.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
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