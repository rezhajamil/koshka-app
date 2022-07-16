package com.naufal.koshka_app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Adopsi (
    var id:String?="",
    var user_id:String?="",
    var name:String?="",
    var ras:String?="",
    var age:String?="",
    var gender:String?="",
    var kecamatan:String?="",
    var deskripsi:String?="",
    var image:String?=""
):Parcelable