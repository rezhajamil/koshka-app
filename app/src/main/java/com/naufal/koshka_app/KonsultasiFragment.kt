package com.naufal.koshka_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_adopsi.*
import kotlinx.android.synthetic.main.fragment_konsultasi.*

class KonsultasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_konsultasi, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_hewania.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://hewania.com/dokter-hewan/")))
        }

        btn_satwagia.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://satwagia.com/konsultasi-online-dokter-hewan-gratis/")))
        }

    }
}