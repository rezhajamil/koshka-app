package com.naufal.koshka_app

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naufal.koshka_app.model.User
import kotlinx.android.synthetic.main.fragment_users.*

class UsersFragment : Fragment() {
    private lateinit var progressDialog: ProgressDialog
    val refUser=FirebaseDatabase.getInstance().getReference("User")
    var userList=ArrayList<User>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rv_users.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Mengambil Data...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        refUser.orderByChild("email").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapshot in snapshot.children){
                    var user=dataSnapshot.getValue(User::class.java)
                    if (user?.role!="Super Admin"){
                        userList.add(user!!)
                    }
                }

                rv_users.adapter=UserAdapter(userList)
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(context,"Gagal Mengambil Data",Toast.LENGTH_SHORT).show()
            }

        })
    }
}