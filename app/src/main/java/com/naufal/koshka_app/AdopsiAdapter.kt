package com.naufal.koshka_app

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.naufal.koshka_app.model.Adopsi
import kotlinx.android.synthetic.main.activity_adopsi_form.*
import java.io.File

class AdopsiAdapter (private var adopsi: ArrayList<Adopsi>,
private val listener:(Adopsi)->Unit):RecyclerView.Adapter<AdopsiAdapter.ViewHolder>(){
    lateinit var contextAdapter: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdopsiAdapter.ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        contextAdapter=parent.context
        val inflatedView=layoutInflater.inflate(R.layout.row_item_adopsi,parent,false)
        return AdopsiAdapter.ViewHolder(inflatedView)
    }
    override fun onBindViewHolder(holder: AdopsiAdapter.ViewHolder, position: Int) {
        holder.bindItem(adopsi[position],listener,contextAdapter)
    }

    override fun getItemCount(): Int =adopsi.size
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val tvNama=view.findViewById<TextView>(R.id.tv_nama_adopsi)
        private val tvRas=view.findViewById<TextView>(R.id.tv_ras_adopsi)
        private val tvLokasi=view.findViewById<TextView>(R.id.tv_lokasi_adopsi)
        private val tvGender=view.findViewById<TextView>(R.id.tv_gender_adopsi)
        private val ivPhoto=view.findViewById<ImageView>(R.id.iv_image_adopsi)


        fun bindItem(adopsi: Adopsi, listener: (Adopsi) -> Unit, contextAdapter: Context){
            val storageRef= FirebaseStorage.getInstance().reference.child("adopsi/${adopsi.image}")
            val localFile= File.createTempFile("tempImage","jpeg")

            tvNama.setText(adopsi.name)
            tvRas.setText(adopsi.ras)
            tvLokasi.setText(adopsi.kecamatan)
            tvGender.setText(adopsi.gender)

            storageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                ivPhoto.setImageBitmap(bitmap)
            }.addOnFailureListener{
                Toast.makeText(contextAdapter,"Failed to get Image",Toast.LENGTH_LONG).show()
            }
            itemView.setOnClickListener {
                listener(adopsi)
            }
        }
    }

}
