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
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Adopsi
import com.naufal.koshka_app.model.Diskusi
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var user_id:String
    var diskusiList=ArrayList<Diskusi>()
    var adopsiList=ArrayList<Adopsi>()
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
        user_id=sharedPreferences.getString("id","").toString()
        var email=sharedPreferences.getString("email","")
        var name=sharedPreferences.getString("name","")
        var avatar=sharedPreferences.getString("avatar","")

        var storageRef=FirebaseStorage.getInstance().reference.child("avatar/$avatar")
        storageRef.downloadUrl.addOnCompleteListener {
            Glide.with(requireContext())
                .load(it.result)
                .into(iv_user_profile)

            iv_user_profile.setBackgroundColor(requireContext().resources.getColor(R.color.white))
        }
        tv_email_profile.setText(email)
        tv_name_profile.setText(name)
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

        iv_edit_profile.setOnClickListener {
            startActivity(Intent(context,EditProfileActivity::class.java))
        }

        getData()
        setFragment(UserDiskusiFragment(diskusiList))
        tl_profile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0->{
                        setFragment(UserDiskusiFragment(diskusiList))
                    }
                    1->{
                        setFragment(UserAdopsiFragment(adopsiList))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun getData() {
        var refDiskusi=FirebaseDatabase.getInstance().getReference("Diskusi")
        var refAdopsi=FirebaseDatabase.getInstance().getReference("Adopsi")

        refDiskusi.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                diskusiList.clear()
                for (dataSnapshot in snapshot.children){
                    var diskusi=dataSnapshot.getValue(Diskusi::class.java)
                    if (diskusi?.user_id==user_id){
                        diskusiList.add(diskusi)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        refAdopsi.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                adopsiList.clear()
                for (dataSnapshot in snapshot.children){
                    var adopsi=dataSnapshot.getValue(Adopsi::class.java)
                    if (adopsi?.user_id==user_id){
                        adopsiList.add(adopsi)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setFragment(fragment: Fragment,){
        val fragmentManager=activity?.supportFragmentManager
        val fragmentTransaction=fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragment_container_profile,fragment)
        fragmentTransaction?.commit()
    }

}