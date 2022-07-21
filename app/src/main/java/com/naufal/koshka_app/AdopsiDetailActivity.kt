package com.naufal.koshka_app

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Adopsi
import com.naufal.koshka_app.model.User
import kotlinx.android.synthetic.main.activity_adopsi_detail.*
import kotlinx.android.synthetic.main.fragment_adopsi.*
import java.io.File
import java.lang.Exception

class AdopsiDetailActivity : AppCompatActivity() {
    private lateinit var popupMenu: PopupMenu
    private lateinit var progressDialog: ProgressDialog
    private lateinit var dataUser:User
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user_id: String
    private lateinit var user_role: String
    private lateinit var data:Adopsi
    var refData= FirebaseDatabase.getInstance().getReference("User")
    var refDataAdopsi= FirebaseDatabase.getInstance().getReference("Adopsi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adopsi_detail)
        data= intent.getParcelableExtra<Adopsi>("data")!!
        val storageRef= FirebaseStorage.getInstance().reference.child("adopsi/${data?.image}")
        val localFile= File.createTempFile("tempImage","jpeg")

        sharedPreferences=getSharedPreferences("User",0)
        user_id=sharedPreferences.getString("id","").toString()
        user_role=sharedPreferences.getString("role","").toString()


        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Mengambil Data...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        getUserData(data?.user_id.toString())

        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            iv_image_detail_adopsi.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(this,"Failed to get Image", Toast.LENGTH_LONG).show()
        }

        tv_name_detail_adopsi.text=data?.name.toString()
        tv_umur_detail_adopsi.text=data?.age.toString()+" Bulan"
        tv_gender_detail_adopsi.text=data?.gender.toString()
        tv_kecamatan_detail_adopsi.text=data?.kecamatan.toString()
        tv_deskripsi_detail_adopsi.text=data?.deskripsi.toString()

        popUpMenu()

        iv_more_detail_adopsi.setOnClickListener {
            try {
                val popUp=PopupMenu::class.java.getDeclaredField("mPopup")
                popUp.isAccessible=true
                val menu=popUp.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForce",Boolean::class.java)
                    .invoke(menu,true)
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                popupMenu.show()
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.edit_adopsi -> {
                        startActivity(Intent(this@AdopsiDetailActivity,EditAdopsiActivity::class.java).putExtra("data",data))
                        finish()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete_adopsi->{
                    var alert= AlertDialog.Builder(this)
                    alert.setTitle("Hapus Data")
                    alert.setPositiveButton("Hapus", DialogInterface.OnClickListener{
                            dialog, which ->
                        storageRef.delete().addOnSuccessListener {
                            refDataAdopsi.child(data?.id.toString()).removeValue()
                            finish()
                            Toast.makeText(this,"Data telah dihapus",Toast.LENGTH_SHORT).show()
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


        iv_back_detail_adopsi.setOnClickListener {
            finish()
        }

        btn_contact_user_adopsi.setOnClickListener {
            startActivity(Intent(this@AdopsiDetailActivity,MessageActivity::class.java).putExtra("receiver",dataUser))
        }
    }

    private fun getUserData(user: String) {
        refData.child(user).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user = snapshot.getValue(User::class.java)
                dataUser= User()
                val storageRefUser= FirebaseStorage.getInstance().reference.child("avatar/${user?.avatar}")
                storageRefUser.downloadUrl.addOnSuccessListener {
                    dataUser.id=user?.id
                    dataUser.name=user?.name
                    dataUser.phone=user?.phone
                    dataUser.avatar=user?.avatar
                    iv_user_profile_adopsi.setBackgroundColor(resources.getColor(R.color.white))
                    Glide.with(this@AdopsiDetailActivity)
                        .load(it)
                        .apply(RequestOptions.circleCropTransform())
                        .into(iv_user_profile_adopsi)
                }

                tv_user_name_adopsi.text=user?.name.toString()
                Log.v("sama",(user?.id.toString()==user_id).toString())
                Log.v("sama",user?.id.toString().toString())
                Log.v("sama",user_id.toString())

                if (user?.id.toString()==user_id||user_role=="Admin"){
                    if (user?.id.toString()==user_id){
                        btn_contact_user_adopsi.visibility=View.INVISIBLE
                    }
                    iv_more_detail_adopsi.visibility=View.VISIBLE
                }else{
                    btn_contact_user_adopsi.visibility=View.VISIBLE
                    iv_more_detail_adopsi.visibility=View.INVISIBLE
                }
                progressDialog.dismiss()

            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(this@AdopsiDetailActivity,""+error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun popUpMenu() {
        popupMenu=PopupMenu(applicationContext,iv_more_detail_adopsi)
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