package com.naufal.koshka_app

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Adopsi
import kotlinx.android.synthetic.main.activity_adopsi_form.*
import java.text.SimpleDateFormat
import java.util.*

class AdopsiFormActivity : AppCompatActivity() {
    private lateinit var ImageUri: Uri
    private lateinit var data:Adopsi
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    val refAdopsi=FirebaseDatabase.getInstance().getReference("Adopsi")
    var filename=""
    var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adopsi_form)
        sharedPreferences=getSharedPreferences("User",0)

        tv_cancel_adopsi.setOnClickListener {
            finish()
        }

        btn_add_photo_adopsi.setOnClickListener {
            selectImage()
        }

        iv_upload_adopsi.setOnClickListener {
            selectImage()
        }

        tv_post_adopsi.setOnClickListener {
            if (validateInput()){
                id=refAdopsi.push().key.toString()
                data= Adopsi()
                data.id=id
                data.user_id=sharedPreferences.getString("id","")
                data.name=et_nama_adopsi.text.toString()
                data.ras=sp_ras_form.selectedItem.toString()
                data.age=et_umur_adopsi.text.toString()
                data.gender=sp_gender_form.selectedItem.toString()
                data.kecamatan=sp_kecamatan_form.selectedItem.toString()
                data.deskripsi=et_deskripsi_adopsi.text.toString()
                data.image=filename

                progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Menyimpan Data...")
                progressDialog.setCancelable(false)
                progressDialog.show()
                val storageReference = FirebaseStorage.getInstance().getReference("adopsi/$filename")
                storageReference.putFile(ImageUri)
                    .addOnSuccessListener {
                        refAdopsi.child(id).setValue(data)
                        Toast.makeText(this,"Berhasil Upload", Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
//                        startActivity(Intent(this,MainActivity::class.java).putExtra("fragment","adopsi"))
                        finish()
                    }.addOnFailureListener{
                        Toast.makeText(this,"Gagal Uplaod Gambar", Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
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
            iv_upload_adopsi.visibility=View.VISIBLE
            iv_upload_adopsi.setImageURI(ImageUri)
            btn_add_photo_adopsi.visibility=View.INVISIBLE
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            filename = formatter.format(now)

            Glide.with(this)
                .load(ImageUri)
                .into(iv_upload_adopsi)

        }
    }

    fun validateInput():Boolean{
        if (filename.equals("")){
            Toast.makeText(this,"Silahkan Upload Gambar",Toast.LENGTH_LONG).show()
            return false
        }else if (et_nama_adopsi.text.toString().equals("")){
            et_nama_adopsi.setError("Isi Nama")
            et_nama_adopsi.requestFocus()
            return false
        }else if (et_umur_adopsi.text.toString().equals("")){
            et_umur_adopsi.setError("Isi Umur")
            et_umur_adopsi.requestFocus()
            return false
        }else if (sp_ras_form.selectedItemPosition==0){
            sp_ras_form.requestFocus()
            Toast.makeText(this,"Silahkan Pilih Ras",Toast.LENGTH_LONG).show()
            return false
        }else if (sp_gender_form.selectedItemPosition==0){
            sp_gender_form.requestFocus()
            Toast.makeText(this,"Silahkan Jenis Kelamin",Toast.LENGTH_LONG).show()
            return false
        }else if (sp_kecamatan_form.selectedItemPosition==0){
            sp_kecamatan_form.requestFocus()
            Toast.makeText(this,"Silahkan Pilih Domisili",Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}