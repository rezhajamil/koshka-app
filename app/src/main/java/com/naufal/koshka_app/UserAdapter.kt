package com.naufal.koshka_app

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.User

class UserAdapter (private var users: ArrayList<User>): RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    lateinit var contextAdapter: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        val inflatedView=layoutInflater.inflate(R.layout.row_item_users,parent,false)
        return UserAdapter.ViewHolder(inflatedView)
    }
    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        holder.bindItem(users[position],contextAdapter)
    }

    override fun getItemCount(): Int =users.size
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val tvEmail=view.findViewById<TextView>(R.id.tv_email_users)
        private val tvRole=view.findViewById<TextView>(R.id.tv_role_users)
        private val ivProfile=view.findViewById<ImageView>(R.id.iv_profile_users)
        private val tvName=view.findViewById<TextView>(R.id.tv_name_users)
        private val btnRole=view.findViewById<Button>(R.id.btn_change_role)


        fun bindItem(users: User, contextAdapter: Context){
            val storageRef= FirebaseStorage.getInstance().reference.child("avatar/${users.avatar}")
            var refUser=FirebaseDatabase.getInstance().getReference("User").child(users.id!!)
            storageRef.downloadUrl.addOnCompleteListener {
                Glide.with(contextAdapter)
                    .load(it.result)
                    .into(ivProfile)
            }

            tvName.setText(users.name)
            tvEmail.setText(users.email)
            tvRole.setText(users.role)

            btnRole.setOnClickListener {
                var alert= AlertDialog.Builder(contextAdapter)
                alert.setTitle("Ubah Role")
                alert.setPositiveButton("Ubah", DialogInterface.OnClickListener{
                        dialog, which ->
                    var progressDialog=ProgressDialog(contextAdapter)
                    progressDialog.setMessage("Mengubah Role...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    if (users.role=="User"){
                        users.role="Admin"
                    }else if (users.role=="Admin"){
                        users.role="User"
                    }

                    refUser.setValue(users)
                    Toast.makeText(contextAdapter,"Berhasil Ubah Role",Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                })
                alert.setNegativeButton("Batal", DialogInterface.OnClickListener{
                        dialog, which ->
                    dialog.cancel()
                    dialog.dismiss()
                })
                alert.create()
                alert.show()
            }

        }

    }
}