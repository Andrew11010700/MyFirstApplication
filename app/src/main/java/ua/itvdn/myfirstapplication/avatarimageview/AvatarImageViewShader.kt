package ua.itvdn.myfirstapplication.avatarimageview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import kotlin.math.min

class AvatarImageViewShader@JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
): androidx.appcompat.widget.AppCompatImageView(context, attr, defStyle) {

    private val borderPaint = Paint()
    private val avatarPaint = Paint()
    private val rectBounds = Rect()

    private var borderWidth = 20f

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHight)
        rectBounds.set(0, 0, width, height)
        val offset = (borderWidth/2).toInt()
        rectBounds.inset(offset, offset)
        setupPaint()
        prepareShader(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawOval(rectBounds.toRectF(), avatarPaint)
        canvas.drawOval(rectBounds.toRectF(), borderPaint)
    }

    private fun setupPaint() {
        with(avatarPaint) {
            style = Paint.Style.FILL
            color = Color.RED
        }

        with(borderPaint) {
            style = Paint.Style.STROKE
            color = Color.GREEN
            strokeWidth = borderWidth
        }
    }

    private fun prepareShader(width: Int, height: Int) {
        val srcBitmap = drawable.toBitmap(width, height, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

}