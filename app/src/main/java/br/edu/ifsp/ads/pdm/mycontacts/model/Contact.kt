package br.edu.ifsp.ads.pdm.mycontacts.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    var id: Int,
    var name: String,
    var address: String,
    var phone: String,
    var email: String,
): Parcelable
