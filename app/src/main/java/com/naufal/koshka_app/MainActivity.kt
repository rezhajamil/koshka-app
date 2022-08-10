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
    private lateinit var user_role:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences=getSharedPreferences("User",0)
        user_email= sharedPreferences.getString("email","").toString()
        user_role=sharedPreferences.getString("role","").toString()

        var intentFragment=intent.getStringExtra("fragment")



        if (user_email.equals("")){
            setFragment(AdopsiFragment(user_email))
            tv_title.setText("Adopsi")
            bottom_navigation.selectedItemId=R.id.nav_adopsi
        }
        else{
            setFragment(HomeFragment())
            tv_title.setText("Home")
            bottom_navigation.selectedItemId=R.id.nav_home
        }

        if (!user_role.equals("Admin")){
            bottom_navigation.menu.removeItem(R.id.nav_users)
        }else{
            bottom_navigation.menu.removeItem(R.id.nav_konsul)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        if (intentFragment?.toString().equals("home")){
            if (checkLogin()){
            setFragment(HomeFragment())
            tv_title.setText("Home")
            bottom_navigation.selectedItemId=R.id.nav_home
            }
        }else if (intentFragment?.toString().equals("adopsi")){
            setFragment(AdopsiFragment(user_email))
            tv_title.setText("Adopsi")
            bottom_navigation.selectedItemId=R.id.nav_adopsi
        }else if (intentFragment?.toString().equals("konsultasi")){
            if (checkLogin()){
            setFragment(KonsultasiFragment())
            tv_title.setText("Konsultasi")
            bottom_navigation.selectedItemId=R.id.nav_konsul
            }
        }else if (intentFragment?.toString().equals("users")){
            if (checkLogin()){
            setFragment(UsersFragment())
            tv_title.setText("Users")
            bottom_navigation.selectedItemId=R.id.nav_users
            }
        }else if (intentFragment?.toString().equals("profile")){
            if (checkLogin()){
            setFragment(ProfileFragment())
            tv_title.setText("Profile")
            bottom_navigation.selectedItemId=R.id.nav_profile
            }
        }

        bottom_navigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    if (checkLogin()){
                    setFragment(HomeFragment())
                    tv_title.setText("Home")
                       return@setOnItemSelectedListener true
                    }
                    false
                }
                R.id.nav_adopsi -> {
                    setFragment(AdopsiFragment(user_email))
                    tv_title.setText("Adopsi")
                    true
                }
                R.id.nav_konsul -> {
                    if (checkLogin()){
                    setFragment(KonsultasiFragment())
                    tv_title.setText("Konsultasi")
                        return@setOnItemSelectedListener  true
                    }
                    false
                }
                R.id.nav_users->{
                    if (checkLogin()){
                    setFragment(UsersFragment())
                    tv_title.setText("Users")
                        return@setOnItemSelectedListener  true
                    }
                    false
                }
                R.id.nav_profile -> {
                    if (checkLogin()){
                    setFragment(ProfileFragment())
                    tv_title.setText("Profile")
                        return@setOnItemSelectedListener  true
                    }
                    false
                }
                else -> {
                    false
                }
            }
        }

        iv_chat.setOnClickListener {
            if (checkLogin()){
                startActivity(Intent(this,MessageListActivity::class.java))
            }
        }
    }

    private fun setFragment(fragment: Fragment,){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,fragment)
        fragmentTransaction.commit()
    }

    private fun checkLogin(): Boolean {
        if (user_email.equals("")){
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
        }else{
            return true
        }

        return false
    }
}