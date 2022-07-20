package com.naufal.koshka_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.naufal.koshka_app.model.Adopsi

class EditAdopsiActivity : AppCompatActivity() {
    private lateinit var adopsi:Adopsi
    var refAdopsi=FirebaseDatabase.getInstance().getReference("Adopsi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_adopsi)
        adopsi= intent.getParcelableExtra<Adopsi>("data")!!


    }
}