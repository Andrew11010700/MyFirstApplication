package ua.itvdn.myfirstapplication.extensions

import android.content.res.Resources
import android.util.TypedValue

fun Int.dpToPx(): Float {
    val metrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)
}