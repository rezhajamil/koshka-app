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
import com.naufal.koshka_app.model.Diskusi
import kotlinx.android.synthetic.main.activity_adopsi_form.*
import kotlinx.android.synthetic.main.activity_diskusi_form.*
import java.text.SimpleDateFormat
import java.util.*

class DiskusiFormActivity : AppCompatActivity() {
    private lateinit var ImageUri: Uri
    private lateinit var data: Diskusi
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    val refDiskusi= FirebaseDatabase.getInstance().getReference("Diskusi")
    var filename=""
    var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diskusi_form)
        sharedPreferences=getSharedPreferences("User",0)

        tv_cancel_diskusi.setOnClickListener {
            finish()
        }

        btn_add_photo_diskusi.setOnClickListener {
            selectImage()
        }

        iv_upload_disksui.setOnClickListener {
            selectImage()
        }

        tv_post_diskusi.setOnClickListener {
            if (validateInput()){
                id=refDiskusi.push().key.toString()
                data= Diskusi()
                data.id=id
                data.user_id=sharedPreferences.getString("id","")
                data.caption=et_caption_diskusi.text.toString()
                data.image=filename

                progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Menyimpan Data...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                if (!filename.equals("")){
                    val storageReference = FirebaseStorage.getInstance().getReference("diskusi/$filename")
                    storageReference.putFile(ImageUri)
                        .addOnSuccessListener {
                            refDiskusi.child(id).setValue(data)
                            Toast.makeText(this,"Berhasil Upload", Toast.LENGTH_LONG).show()
                            progressDialog.dismiss()
//                        startActivity(Intent(this,MainActivity::class.java).putExtra("fragment","adopsi"))
                            finish()
                        }.addOnFailureListener{
                            Toast.makeText(this,"Gagal Uplaod Gambar", Toast.LENGTH_LONG).show()
                            progressDialog.dismiss()
                        }
                }else{
                    refDiskusi.child(id).setValue(data)
                    Toast.makeText(this,"Berhasil Upload", Toast.LENGTH_LONG).show()
                    finish()
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
            iv_upload_disksui.visibility= View.VISIBLE
            iv_upload_disksui.setImageURI(ImageUri)
            btn_add_photo_diskusi.visibility= View.INVISIBLE
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            filename = formatter.format(now)

            Glide.with(this)
                .load(ImageUri)
                .into(iv_upload_disksui)

        }
    }

    fun validateInput():Boolean{
        if (et_caption_diskusi.text.toString().equals("")) {
            et_caption_diskusi.setError("Isi Nama")
            et_caption_diskusi.requestFocus()
            return false
        }
        return true
    }
}