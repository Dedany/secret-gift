package com.dedany.secretgift.presentation.helpers

import android.widget.ImageView
import com.dedany.secretgift.R

fun ImageView.setMailStatusIcon(status: String?) {
    when (status) {
        "delivered" -> setImageResource(R.drawable.mail_delivered)
        "failed" -> setImageResource(R.drawable.mail_failed)
        else -> setImageResource(R.drawable.unknown)
    }
}