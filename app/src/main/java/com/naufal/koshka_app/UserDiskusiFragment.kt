package com.naufal.koshka_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.naufal.koshka_app.model.Diskusi
import kotlinx.android.synthetic.main.fragment_user_adopsi.*

class UserDiskusiFragment(diskusiList: ArrayList<Diskusi>) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_diskusi, container, false)
    }


}