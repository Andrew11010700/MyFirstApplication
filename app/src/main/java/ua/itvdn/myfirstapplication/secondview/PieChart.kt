package ua.itvdn.myfirstapplication.secondview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class PieChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val circle = RectF()
    private val bounds = RectF()

    private var centerX = 0f
    private var centerY = 0f

    private var lineWidth = 50f

    private var degreeOffset = 0.0f
    private var spaceOffset = 0.0f
    private var maxDegreesWithOffset = 0.0f

    private var touchX = 0f
    private var touchY = 0f

    private val touchSlop = android.view.ViewConfiguration.get(context).scaledTouchSlop

    private var touchRange = Pair(0.0f, 0.0f)

    private var radius = 0f

    private var defaultVector = Pair(0.0f, 0.0f)
    private var moduleDefaultVector = 0.0f

    private val defaultPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    private val segments = mutableListOf<Segment>()

    private var selectedSegment: Segment? = null

    fun mapSegments() {
        segments.add(Segment(1, Color.RED, 0.5f))
        segments.add(Segment(2, Color.CYAN, 0.15f))
        segments.add(Segment(3, Color.YELLOW ,0.25f))
        segments.add(Segment(4, Color.GREEN ,0.1f))

        if (segments.isEmpty()) {
            segments.add(Segment(-1, Color.GRAY, 1f))
        }

        invalidate()
    }

    private fun setSegmentsOffsets() {
        if (segments.isEmpty() || segments.size == 1) {
            spaceOffset = 0.0f
            maxDegreesWithOffset = MAX_DEGREE
        } else {
            spaceOffset = degreeOffset
            maxDegreesWithOffset = MAX_DEGREE - segments.size * spaceOffset
        }

        var startDegrees = 0f

        segments.forEach { segment ->
            segment.startDegrees = startDegrees
            segment.endDegrees = startDegrees + maxDegreesWithOffset * segment.percent
            startDegrees = segment.endDegrees + spaceOffset
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d("onDraw", "onMeasure: ")
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)

        val ratio = min(width, height)

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            width = ratio
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            height = ratio
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val defaultPadding = 2 * lineWidth
        bounds.set(
            0.0f,
            0.0f,
            width - paddingEnd - paddingStart - defaultPadding,
            height - paddingBottom - paddingTop - defaultPadding
        )

        centerX = ((width + paddingStart - paddingEnd)/ 2).toFloat()
        centerY = ((height + paddingTop - paddingBottom) / 2).toFloat()
        val maxRadius = (min(bounds.width(), bounds.height())) / 2
        radius = maxRadius

        circle.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        degreeOffset = (MAX_DEGREE * lineWidth / (2 * Math.PI * maxRadius) * 1.5f).toFloat()
        defaultVector = Pair(circle.right - centerX, 0.0f)
        moduleDefaultVector = sqrt(defaultVector.first.pow(2) + defaultVector.second.pow(2))
        touchRange = Pair(radius - lineWidth, radius + lineWidth)
        setSegmentsOffsets()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var startAngle = 0f
        var offset : Float

        Log.d("onDraw", "onDraw: ")
        segments.forEach { segment ->
            defaultPaint.color = when (selectedSegment) {
                null -> segment.color
                segment -> segment.color
                else -> Color.GRAY
            }

            defaultPaint.strokeWidth =
                if (selectedSegment == segment) 2 * lineWidth
                else lineWidth

            offset = maxDegreesWithOffset * segment.percent
            canvas?.drawArc(circle, startAngle, offset, false, defaultPaint)
            startAngle += offset + spaceOffset
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y

        if (abs(event.x - touchX) <= touchSlop && abs(event.y - touchY) <= touchSlop) {
            onSegmentClick(event.x, event.y)
        }

        return super.onTouchEvent(event)
    }

    private fun onSegmentClick(x: Float, y: Float) {
        val touchVector = Pair(x - centerX, y - centerY)
        val scalarProduct = defaultVector.first * touchVector.first + defaultVector.second * touchVector.second
        val moduleTouchVector = sqrt(touchVector.first.pow(2) + touchVector.second.pow(2))
        if (moduleTouchVector >= touchRange.first && moduleTouchVector <= touchRange.second) {
            val cos = scalarProduct / (moduleDefaultVector * moduleTouchVector)
            val aCos = (acos(cos.toDouble()) * HALF_CIRCLE / Math.PI).toFloat()
            val degrees = if (y >= centerY) aCos else MAX_DEGREE - aCos
            for (segment in segments) {
                if (degrees >= segment.startDegrees && degrees <= segment.endDegrees
                    && segment.id != null && segment.id != DEFAULT_ID) {

                    if (segment == selectedSegment) {
                        unselectSegment()
                    } else {
                        selectSegment(segment)
                    }

                    break
                }
            }
        }
    }

    private fun selectSegment(segment: Segment) {
        Log.d("onDraw", "selectSegment: width = $width, height = $height")
        selectedSegment = segment
        invalidate()
        requestLayout()
    }

    private fun unselectSegment() {
        selectedSegment = null
        invalidate()
        requestLayout()
    }

    companion object {
        const val MAX_DEGREE = 360.0f
        const val DEFAULT_ID = -1L
        const val HALF_CIRCLE = 180.0f
    }

    private class Segment(
        val id: Long,
        val color: Int = Color.RED,
        val percent: Float,
        var startDegrees: Float = 0f,
        var endDegrees: Float = 0f
    )

}