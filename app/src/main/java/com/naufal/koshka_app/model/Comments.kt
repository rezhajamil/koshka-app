package com.naufal.koshka_app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comments (
    var id:String?="",
    var user_id:String?="",
    var diskusi_id:String?="",
    var comment:String?=""
): Parcelable