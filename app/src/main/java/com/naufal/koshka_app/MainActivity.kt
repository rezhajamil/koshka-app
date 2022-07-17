package com.naufal.koshka_app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_message_list.*

class MainActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user_email:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences=getSharedPreferences("User",0)
        user_email= sharedPreferences.getString("email","").toString()
        Log.v("login",user_email)

        var intentFragment=intent.getStringExtra("fragment")

        if (user_email.equals("")){
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        if (intentFragment?.toString().equals("home")){
            setFragment(HomeFragment())
            tv_title.setText("Home")
            bottom_navigation.selectedItemId=R.id.nav_home
        }else if (intentFragment?.toString().equals("adopsi")){
            setFragment(AdopsiFragment())
            tv_title.setText("Adopsi")
            bottom_navigation.selectedItemId=R.id.nav_adopsi
        }else if (intentFragment?.toString().equals("konsultasi")){
            setFragment(KonsultasiFragment())
            tv_title.setText("Konsultasi")
            bottom_navigation.selectedItemId=R.id.nav_konsul
        }else if (intentFragment?.toString().equals("profile")){
            setFragment(ProfileFragment())
            tv_title.setText("Profile")
            bottom_navigation.selectedItemId=R.id.nav_profile
        }else{
            setFragment(HomeFragment())
            tv_title.setText("Home")
            bottom_navigation.selectedItemId=R.id.nav_home
        }

        bottom_navigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    setFragment(HomeFragment())
                    tv_title.setText("Home")
                    true
                }
                R.id.nav_adopsi -> {
                    setFragment(AdopsiFragment())
                    tv_title.setText("Adopsi")
                    true
                }
                R.id.nav_konsul -> {
                    setFragment(KonsultasiFragment())
                    tv_title.setText("Konsultasi")
                    true
                }
                R.id.nav_profile -> {
                    setFragment(ProfileFragment())
                    tv_title.setText("Profile")
                    true
                }
                else -> {
                    false
                }
            }
        }

        iv_chat.setOnClickListener {
            startActivity(Intent(this,MessageListActivity::class.java))
        }
    }

    private fun setFragment(fragment: Fragment,){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.commit()
    }
}