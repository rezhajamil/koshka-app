package com.naufal.koshka_app

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.naufal.koshka_app.model.Adopsi
import com.naufal.koshka_app.model.Diskusi
import kotlinx.android.synthetic.main.fragment_adopsi.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var progressDialog: ProgressDialog

    private var dataList=ArrayList<Diskusi>()
    var refDiskusi= FirebaseDatabase.getInstance().getReference("Diskusi")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fab_add_diskusi.setOnClickListener {
            startActivity(Intent(context,DiskusiFormActivity::class.java))
        }

        rv_diskusi.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Mengambil Data...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        getAllData()

    }

    private fun getAllData() {
        refDiskusi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for(getSnapshot in snapshot.children){
                    var data=getSnapshot.getValue(Diskusi::class.java)
                    dataList.add(data!!)
                }

                rv_diskusi.adapter=DiskusiAdapter(dataList)
                if (dataList.size==0){
                    iv_null_dikusi.visibility=View.VISIBLE
                    tv_null_diskusi.visibility=View.VISIBLE
                }else{
                    iv_null_dikusi.visibility=View.GONE
                    tv_null_diskusi.visibility=View.GONE
                }
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(context,""+error.message, Toast.LENGTH_LONG).show()
            }

        })

    }

}