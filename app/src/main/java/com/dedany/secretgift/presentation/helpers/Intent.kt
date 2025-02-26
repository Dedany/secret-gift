package com.dedany.secretgift.presentation.helpers

import android.content.Intent
import android.os.Build
import com.dedany.secretgift.domain.entities.Game
import java.io.Serializable

inline fun <reified T : Serializable> Intent.getCustomSerializable(key: String): T? {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.extras?.getSerializable(
            key,
            T::class.java
        )
    } else {
        this.extras?.getSerializable(key) as? T
    }
}