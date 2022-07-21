package com.naufal.koshka_app

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Diskusi
import kotlinx.android.synthetic.main.activity_diskusi_detail.*
import kotlinx.android.synthetic.main.activity_edit_adopsi.*
import kotlinx.android.synthetic.main.activity_edit_diskusi.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.text.SimpleDateFormat
import java.util.*

class EditDiskusiActivity : AppCompatActivity() {
    private lateinit var ImageUri: Uri
    private lateinit var diskusi: Diskusi
    private lateinit var progressDialog:ProgressDialog
    var filename=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diskusi)
        diskusi=intent.getParcelableExtra<Diskusi>("data")!!
        var storageRef=FirebaseStorage.getInstance().getReference("diskusi/${diskusi.image}")
        var refDiskusi=FirebaseDatabase.getInstance().getReference("Diskusi").child(diskusi.id!!)

        if (diskusi.image==""){
            Log.v("gagal","gadak")
            iv_upload_edit_disksui.visibility=View.GONE
        }else{
            Log.v("gagal","ada")
            storageRef.downloadUrl.addOnCompleteListener {
                Glide.with(this)
                    .load(it.result)
                    .into(iv_upload_edit_disksui)
            }
        }
        et_edit_caption_diskusi.setText(diskusi.caption)
        tv_cancel_edit_diskusi.setOnClickListener {
            finish()
        }

        iv_upload_edit_disksui.setOnClickListener {
            selectImage()
        }

        tv_post_edit_diskusi.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Menyimpan Data...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            if (et_edit_caption_diskusi.text.toString()!=""){
                if (filename!=""){
                    storageRef.delete()
                    diskusi.image=filename
                    diskusi.caption=et_edit_caption_diskusi.text.toString()
                    FirebaseStorage.getInstance().getReference("diskusi/$filename").putFile(ImageUri).addOnSuccessListener {
                        refDiskusi.setValue(diskusi)
                    }
                    progressDialog.dismiss()
                    finish()
                }else{
                    diskusi.caption=et_edit_caption_diskusi.text.toString()
                    refDiskusi.setValue(diskusi)
                    progressDialog.dismiss()
                    finish()
                }
            }else{
                et_edit_caption_diskusi.requestFocus()
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
            iv_upload_edit_disksui.setImageURI(ImageUri)
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            filename = formatter.format(now)

            Glide.with(this)
                .load(ImageUri)
                .into(iv_upload_edit_disksui)
        }
    }


}