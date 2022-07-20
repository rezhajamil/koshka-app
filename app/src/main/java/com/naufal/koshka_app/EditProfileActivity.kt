package com.naufal.koshka_app

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var ImageUri: Uri
    private lateinit var mDatabase: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var progressDialog: ProgressDialog
    private lateinit var avatar:String
    private lateinit var user:User
    var filename=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        mDatabase= FirebaseDatabase.getInstance().getReference("User")
        sharedPreferences=getSharedPreferences("User",0)
        editor=sharedPreferences.edit()
        val storageRefOld=FirebaseStorage.getInstance().getReference("avatar/${sharedPreferences.getString("avatar","")}")

        storageRefOld.downloadUrl.addOnCompleteListener {
            Glide.with(this)
                .load(it.result)
                .into(iv_avatar_edit)
        }

        et_name_edit.setText(sharedPreferences.getString("name",""))
        et_phone_edit.setText(sharedPreferences.getString("phone",""))
        et_address_edit.setText(sharedPreferences.getString("address",""))
        iv_avatar_edit.setOnClickListener{
            selectImage()
        }

        iv_back_edit.setOnClickListener {
            finish()
        }

        btn_submit_profile.setOnClickListener {
            if (validateInput()){
                if (filename.equals("")){
                    avatar=sharedPreferences.getString("avatar","").toString()
                }else{
                    avatar=filename
                }

                progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Menyimpan Data...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                user= User()
                user.id=sharedPreferences.getString("id","")
                user.email=sharedPreferences.getString("email","")
                user.name=et_name_edit.text.toString()
                user.phone=et_phone_edit.text.toString()
                user.address=et_address_edit.text.toString()
                user.avatar=avatar
                user.role=sharedPreferences.getString("role","")

                if (filename.equals("")){
                    updateData()
                    progressDialog.dismiss()
                    finish()
                }else{
                    val storageRefNew=FirebaseStorage.getInstance().getReference("avatar/$avatar")

                    storageRefOld.delete()
                    storageRefNew.putFile(ImageUri).addOnCompleteListener {
                        updateData()
                        progressDialog.dismiss()
                        Toast.makeText(this,"Berhasil",Toast.LENGTH_LONG).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this,"Gagal",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type="image/*"
        intent.action= Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==100 &&resultCode== RESULT_OK){
            ImageUri=data?.data!!
            iv_avatar_edit.setImageURI(ImageUri)
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            filename = formatter.format(now)

            Glide.with(this)
                .load(ImageUri)
                .into(iv_avatar_edit)
        }
    }

    private fun validateInput(): Boolean {
        if (et_name_edit.text?.length==0)
        {
            et_name_edit.setError("Nama tidak boleh kosong")
            et_name_edit.requestFocus()
            return false
        }
        else if (et_phone_edit.text?.length==0)
        {
            et_phone_edit.setError("Nomor Telepon tidak boleh kosong")
            et_phone_edit.requestFocus()
            return false
        }
        else if (et_address_edit.text?.length==0)
        {
            et_address_edit.setError("Alamat tidak boleh kosong")
            et_address_edit.requestFocus()
            return false
        }
        return true
    }

    private fun updateData(){
        mDatabase.child(user.id.toString()).setValue(user)
        editor.putString("id",user.id)
        editor.putString("email",user.email)
        editor.putString("avatar",user.avatar)
        editor.putString("name",user.name)
        editor.putString("phone",user.phone)
        editor.putString("address",user.address)
        editor.putString("role",user.role)
        editor.commit()
    }
}