package com.naufal.koshka_app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user_email:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences=getSharedPreferences("User",0)
        user_email=sharedPreferences.getString("email","").toString()

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
        }else if (intentFragment?.toString().equals("adopsi")){
            setFragment(AdopsiFragment())
        }else if (intentFragment?.toString().equals("konsultasi")){
            setFragment(KonsultasiFragment())
        }else if (intentFragment?.toString().equals("profile")){
            setFragment(ProfileFragment())
        }else{
            setFragment(HomeFragment())
        }

        bottom_navigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    setFragment(HomeFragment())
                    true
                }
                R.id.nav_adopsi -> {
                    setFragment(AdopsiFragment())
                    true
                }
                R.id.nav_konsul -> {
                    setFragment(KonsultasiFragment())
                    true
                }
                R.id.nav_profile -> {
                    setFragment(ProfileFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun setFragment(fragment: Fragment,){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.commit()
    }
}