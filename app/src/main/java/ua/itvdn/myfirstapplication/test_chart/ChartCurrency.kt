package ua.itvdn.myfirstapplication.test_chart

import android.content.Context
import android.util.AttributeSet
import android.view.View

class ChartCurrency @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

//    private var chartHeight = 0
//    private var pxPerUnit = 0f
//    private var zeroY = 0f
//
//    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
//        super.onSizeChanged(width, height, oldWidth, oldHeight)
//        chartHeight = height
//    }
//
//    fun calcPositions(markers: List<Marker>) {
//        val max = markers.maxByOrNull { it.value }
//        val min = markers.minByOrNull { it.value }
//        max ?: return
//        min ?: return
//        pxPerUnit = chartHeight / (max.value - min.value)
//        zeroY = max.value * pxPerUnit + paddingTop
//
//        val step = (width - 2 * padding - scalesWidth) / (markers.size - 1)
//        for ((i, marker) in markers.withIndex()) {
//            val x = step * i + paddingLeft
//            val y = zeroY - entry.value * pxPerUnit
//            marker.position.x = x
//            marker.position.y = y
//        }
//    }

}