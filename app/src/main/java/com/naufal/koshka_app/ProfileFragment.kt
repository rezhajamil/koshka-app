package com.naufal.koshka_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedPreferences=requireContext().getSharedPreferences("User",0)
        editor=sharedPreferences.edit()
        Log.v("login2",sharedPreferences.getString("email","").toString())
        iv_logout_profile.setOnClickListener {
            Firebase.auth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("722547354607-l02pcib5d3bq4h8i497u0ug5tqarb4k3.apps.googleusercontent.com")
                .requestEmail()
                .build()
            mGoogleSignInClient= GoogleSignIn.getClient(requireContext(),gso)
            mGoogleSignInClient.signOut().addOnCompleteListener {
                editor.clear().commit()
                Log.v("login3",sharedPreferences.getString("email","").toString())
                Toast.makeText(context, "Logout Account", Toast.LENGTH_SHORT).show()
                startActivity(Intent(context, LoginActivity::class.java))
                activity?.finishAffinity()
            }
        }
    }
}