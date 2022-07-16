package com.naufal.koshka_app

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.User
import kotlinx.android.synthetic.main.activity_register.*
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient:GoogleSignInClient
    private lateinit var ImageUri: Uri
    private lateinit var mDatabase: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    var filename=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mDatabase= FirebaseDatabase.getInstance().getReference("User")
        sharedPreferences=getSharedPreferences("User",0)
        editor=sharedPreferences.edit()

        val email=intent.getStringExtra("email")

        iv_logout_register.setOnClickListener{
            Firebase.auth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("722547354607-l02pcib5d3bq4h8i497u0ug5tqarb4k3.apps.googleusercontent.com")
                .requestEmail()
                .build()
            mGoogleSignInClient=GoogleSignIn.getClient(this,gso)
            mGoogleSignInClient.signOut().addOnCompleteListener {
                Toast.makeText(this, "Logout Account", Toast.LENGTH_SHORT)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            editor.clear()
        }

        iv_upload_avatar.setOnClickListener{
            selectImage()
        }

        et_email.setText(email)

        btn_submit_register.setOnClickListener{
            if (validateInput()){
                val user=User()
                user.id=mDatabase.push().key.toString()
                user.email=et_email.text.toString()
                user.name=et_name.text.toString()
                user.phone=et_phone.text.toString()
                user.address=et_address.text.toString()
                user.avatar=filename
                user.role="user"

                if (filename!="")uploadImage()
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
    }

    private fun uploadImage() {
//
        val storageReference = FirebaseStorage.getInstance().getReference("avatar/$filename")

        storageReference.putFile(ImageUri)
            .addOnSuccessListener {
//
                Toast.makeText(this,"Berhasil",Toast.LENGTH_LONG).show()
            }.addOnFailureListener{
//            if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
            }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==100 &&resultCode== RESULT_OK){
            ImageUri=data?.data!!
            iv_upload_avatar.setImageURI(ImageUri)
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            filename = formatter.format(now)

            Glide.with(this)
                .load(ImageUri)
                .into(iv_upload_avatar)

        }
    }

    private fun validateInput(): Boolean {
        if (et_email.text?.length==0)
        {
            et_email.setError("Email tidak boleh kosong")
            et_email.requestFocus()
            return false
        }
        else if (et_name.text?.length==0)
        {
            et_name.setError("Nama tidak boleh kosong")
            et_name.requestFocus()
            return false
        }
        else if (et_phone.text?.length==0)
        {
            et_phone.setError("Nomor Telepon tidak boleh kosong")
            et_phone.requestFocus()
            return false
        }
        else if (et_address.text?.length==0)
        {
            et_address.setError("Alamat tidak boleh kosong")
            et_address.requestFocus()
            return false
        }
        return true
    }
}