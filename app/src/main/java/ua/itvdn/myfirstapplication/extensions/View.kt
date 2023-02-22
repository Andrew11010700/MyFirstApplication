package ua.itvdn.myfirstapplication.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.showOrGone(isShow: Boolean) {
    this.visibility = if (isShow) View.VISIBLE else View.GONE
}

fun View.setMargins(
    left: Float = this.marginLeft.toFloat(),
    top: Float = this.marginTop.toFloat(),
    right: Float = this.marginRight.toFloat(),
    bottom: Float = this.marginBottom.toFloat()
) {
    val params: ViewGroup.MarginLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
}