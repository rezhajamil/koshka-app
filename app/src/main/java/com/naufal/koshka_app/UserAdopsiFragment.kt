package com.naufal.koshka_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.naufal.koshka_app.model.Adopsi
import kotlinx.android.synthetic.main.fragment_user_adopsi.*

class UserAdopsiFragment(adopsiList: ArrayList<Adopsi>) : Fragment() {
    val adopsi=adopsiList
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_adopsi, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv_user_adopsi.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        rv_user_adopsi.adapter=AdopsiAdapter(adopsi){
            startActivity(Intent(context,AdopsiDetailActivity::class.java).putExtra("data",it))
        }
    }

}