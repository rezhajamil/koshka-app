package com.naufal.koshka_app

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Adopsi
import kotlinx.android.synthetic.main.activity_adopsi_form.*
import kotlinx.android.synthetic.main.activity_edit_adopsi.*
import kotlinx.android.synthetic.main.activity_edit_diskusi.*
import java.text.SimpleDateFormat
import java.util.*

class EditAdopsiActivity : AppCompatActivity() {
    private lateinit var ImageUri: Uri
    private lateinit var adopsi:Adopsi
    private lateinit var progressDialog: ProgressDialog
    var filename=""
    var refAdopsi=FirebaseDatabase.getInstance().getReference("Adopsi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_adopsi)
        adopsi= intent.getParcelableExtra<Adopsi>("data")!!
        var storageRef= FirebaseStorage.getInstance().getReference("adopsi/${adopsi.image}")
        storageRef.downloadUrl.addOnCompleteListener {
            Glide.with(this)
                .load(it.result)
                .into(iv_edit_upload_adopsi)
        }
        tv_cancel_edit_adopsi.setOnClickListener {
            finish()
        }

        iv_edit_upload_adopsi.setOnClickListener {
            selectImage()
        }

        et_edit_nama_adopsi.setText(adopsi.name)
        et_edit_umur_adopsi.setText(adopsi.age)
        et_edit_deskripsi_adopsi.setText(adopsi.deskripsi)

        tv_post_edit_adopsi.setOnClickListener {
            adopsi.name=et_edit_nama_adopsi.text.toString()
            adopsi.age=et_edit_umur_adopsi.text.toString()
            adopsi.ras=sp_edit_ras_form.selectedItem.toString()
            adopsi.gender=sp_edit_gender_form.selectedItem.toString()
            adopsi.kecamatan=sp_edit_kecamatan_form.selectedItem.toString()
            adopsi.deskripsi=et_edit_deskripsi_adopsi.text.toString()
            if (validateInput()){
                progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Menyimpan Data...")
                progressDialog.setCancelable(false)
                progressDialog.show()
                if (filename!=""){
                    storageRef.delete()
                    adopsi.image=filename
                    FirebaseStorage.getInstance().getReference("adopsi/${filename}").putFile(ImageUri).addOnSuccessListener {
                        refAdopsi.child(adopsi.id!!).setValue(adopsi)
                        progressDialog.dismiss()
                        finish()
                    }
                }else{
                    refAdopsi.child(adopsi.id!!).setValue(adopsi)
                    progressDialog.dismiss()
                    finish()
                }
            }


//            startActivity(Intent(this,MainActivity::class.java).putExtra("fragment","adopsi"))

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
            iv_edit_upload_adopsi.setImageURI(ImageUri)
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            filename = formatter.format(now)

            Glide.with(this)
                .load(ImageUri)
                .into(iv_edit_upload_adopsi)
        }
    }

    fun validateInput():Boolean{
        if (et_edit_nama_adopsi.text.toString()==""){
            et_edit_nama_adopsi.setError("Isi Nama")
            et_edit_nama_adopsi.requestFocus()
            return false
        }else if (et_edit_umur_adopsi.text.toString()==""){
            et_edit_umur_adopsi.setError("Isi Umur")
            et_edit_umur_adopsi.requestFocus()
            return false
        }else if (sp_edit_ras_form.selectedItemPosition==0){
            sp_edit_ras_form.requestFocus()
            Toast.makeText(this,"Silahkan Pilih Ras", Toast.LENGTH_LONG).show()
            return false
        }else if (sp_edit_gender_form.selectedItemPosition==0){
            sp_edit_gender_form.requestFocus()
            Toast.makeText(this,"Silahkan Jenis Kelamin", Toast.LENGTH_LONG).show()
            return false
        }else if (sp_edit_kecamatan_form.selectedItemPosition==0){
            sp_edit_kecamatan_form.requestFocus()
            Toast.makeText(this,"Silahkan Pilih Domisili", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}