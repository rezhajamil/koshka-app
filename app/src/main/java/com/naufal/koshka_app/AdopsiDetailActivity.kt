package com.naufal.koshka_app

import android.app.ProgressDialog
import android.content.Intent
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
    private lateinit var user: User
    private lateinit var progressDialog: ProgressDialog
    var refData= FirebaseDatabase.getInstance().getReference("User")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adopsi_detail)
        val data=intent.getParcelableExtra<Adopsi>("data")
        val storageRef= FirebaseStorage.getInstance().reference.child("adopsi/${data?.image}")
        val localFile= File.createTempFile("tempImage","jpeg")

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
        }

        iv_back_detail_adopsi.setOnClickListener {
            finish()
        }
    }

    private fun getUserData(user_id: String) {
        refData.child(user_id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user = snapshot.getValue(User::class.java)

                Log.v("gagal1",user.toString())
                Log.v("gagal1",user?.name.toString())
                Log.v("gagal2",user?.avatar.toString())

                val storageRefUser= FirebaseStorage.getInstance().reference.child("avatar/${user?.avatar}")
                storageRefUser.downloadUrl.addOnSuccessListener {
                    Log.v("gagal3url",it.toString())
                    iv_user_profile_adopsi.setBackgroundColor(resources.getColor(R.color.white))
                    Glide.with(this@AdopsiDetailActivity)
                        .load(it)
                        .apply(RequestOptions.circleCropTransform())
                        .into(iv_user_profile_adopsi)
                }

                tv_user_name_adopsi.text=user?.name.toString()
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