package com.naufal.koshka_app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var id:String?="",
    var name:String?="",
    var email:String?="",
    var phone:String?="",
    var avatar:String?="",
    var address:String?="",
    var role:String?="",
):Parcelable