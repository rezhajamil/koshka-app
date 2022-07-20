package com.naufal.koshka_app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Diskusi (
    var id:String?="",
    var user_id:String?="",
    var caption:String?="",
    var image:String?=""
): Parcelable