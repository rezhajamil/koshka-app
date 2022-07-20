package com.naufal.koshka_app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Likes (
    var id:String?="",
    var user_id:String?="",
    var diskusi_id:String?="",
): Parcelable