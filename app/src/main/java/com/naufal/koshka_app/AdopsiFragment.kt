package com.naufal.koshka_app

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.naufal.koshka_app.model.Adopsi
import kotlinx.android.synthetic.main.fragment_adopsi.*


class AdopsiFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var progressDialog: ProgressDialog

    private var dataList=ArrayList<Adopsi>()
    var refData= FirebaseDatabase.getInstance().getReference("Adopsi")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adopsi, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv_adopsi.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Mengambil Data...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        getAllData()

        fab_add_adopsi.setOnClickListener{
            Log.v("halo","halo")
            startActivity(Intent(activity,AdopsiFormActivity::class.java))
        }

        sp_kecamatan.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                progressDialog.show()
                getFilterData(sp_kecamatan.selectedItem.toString(),sp_ras.selectedItem.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        sp_ras.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                progressDialog.show()
                getFilterData(sp_kecamatan.selectedItem.toString(),sp_ras.selectedItem.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun getAllData() {
        refData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for(getSnapshot in snapshot.children){
                    var data=getSnapshot.getValue(Adopsi::class.java)
                    dataList.add(data!!)
                }

                rv_adopsi.adapter=AdopsiAdapter(dataList){
                    var intent=Intent(context,AdopsiDetailActivity::class.java).putExtra("data",it)
                    startActivity(intent)
                }
                if (dataList.size==0){
                    iv_null_adopsi.visibility=View.VISIBLE
                    tv_null_adopsi.visibility=View.VISIBLE
                    tv_null_adopsi.text="Tidak Ada Kucing Untuk Diadopsi"
                }else{
                    iv_null_adopsi.visibility=View.GONE
                    tv_null_adopsi.visibility=View.GONE
                }
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(context,""+error.message, Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun getFilterData(kecamatan:String,ras:String) {
        refData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for(getSnapshot in snapshot.children){
                    var data=getSnapshot.getValue(Adopsi::class.java)
                    if (kecamatan.equals("Semua")&&ras.equals("Semua")){
                        dataList.add(data!!)
                    }else if (!kecamatan.equals("Semua")&&ras.equals("Semua")){
                        if (data?.kecamatan==kecamatan){
                            dataList.add(data!!)
                        }
                    }else if (kecamatan.equals("Semua")&&!ras.equals("Semua")){
                        if (data?.ras==ras){
                            dataList.add(data!!)
                        }
                    }else if (!kecamatan.equals("Semua")&&!ras.equals("Semua")){
                        if (data?.ras==ras&&data?.kecamatan==kecamatan){
                            dataList.add(data!!)
                        }
                    }

                    progressDialog.dismiss()
                }

                rv_adopsi.adapter=AdopsiAdapter(dataList){
                    var intent=Intent(context,AdopsiDetailActivity::class.java).putExtra("data",it)
                    startActivity(intent)
                }

                if (dataList.size==0){
                    iv_null_adopsi.visibility=View.VISIBLE
                    tv_null_adopsi.visibility=View.VISIBLE
                    tv_null_adopsi.text="Tidak Ada Pencarian Sesuai"
                }else{
                    iv_null_adopsi.visibility=View.GONE
                    tv_null_adopsi.visibility=View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(context,""+error.message, Toast.LENGTH_LONG).show()
            }

        })

    }

}