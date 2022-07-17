package com.naufal.koshka_app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naufal.koshka_app.model.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var status:String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user_email:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences=getSharedPreferences("User",0)
        user_email=sharedPreferences.getString("email","").toString()

        if (!user_email.equals("")){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("722547354607-l02pcib5d3bq4h8i497u0ug5tqarb4k3.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener{
            signInGoogle()
        }
    }
    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }
    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Log.v("gagal1",e.toString())
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.v("akun",account.email.toString())
                checkRegister(account.email.toString())
            }else{
                Log.v("gagal2",task.exception.toString())
            }
        }
    }

    private fun checkRegister(email:String){
        FirebaseDatabase.getInstance().getReference("User").addValueEventListener(object :ValueEventListener{
            var usernull=true
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    var user=snapshot.getValue(User::class.java)
                    if (user?.email==email){
                        Log.v("usernull1", user.email.toString())
                        val sharedPreference =  getSharedPreferences("User",0)
                        var editor=sharedPreference.edit()
                        editor.putString("id",user?.id)
                        editor.putString("email",user?.email)
                        editor.putString("name",user?.name)
                        editor.putString("phone",user?.phone)
                        editor.putString("avatar",user?.avatar)
                        editor.putString("address",user?.address)
                        editor.putString("role",user?.role)
                        editor.commit()

                        usernull=false
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        finish()
                        break
                    }
                }

                if (usernull){
                    Log.v("usernull",email)
                    startActivity(Intent(this@LoginActivity,RegisterActivity::class.java).putExtra("email",email))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            Log.v("akun",GoogleSignIn.getLastSignedInAccount(this).toString())
            Log.v("akun2",firebaseAuth.currentUser.toString())
        }
    }

}