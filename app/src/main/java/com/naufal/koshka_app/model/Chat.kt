package com.naufal.koshka_app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chat (
    var id:String?="",
    var sender:String?="",
    var receiver:String?="",
    var message:String?="",
    var time:String?="",
): Parcelable