package com.rpn.exchangebook.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText

fun EditText.getHour() : Int? {
    return if (this.text.toString().isEmpty())  null
    else this.text.toString().trim().split(":").firstOrNull()?.toInt()
}
fun EditText.getMinute() : Int? {
    return if (this.text.toString().isEmpty())  null
    else this.text.toString().trim().split(":").lastOrNull()?.toInt()
}

