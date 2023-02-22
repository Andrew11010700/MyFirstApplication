package ua.itvdn.myfirstapplication.avatarimageview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import kotlin.math.min

class AvatarImageViewMask @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
): androidx.appcompat.widget.AppCompatImageView(context, attr, defStyle) {

    private val maskedPaint = Paint()
    private val borderPaint = Paint()
    private val rectBounds = Rect()

    private var resultBitmap: Bitmap? = null
    private var maskedBitmap: Bitmap? = null
    private var srcBitmap: Bitmap? = null

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
        prepareBitmap(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        resultBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, rectBounds, rectBounds, null)
        }

        canvas.drawOval(rectBounds.toRectF(), borderPaint)
    }

    private fun setupPaint() {
        with(maskedPaint) {
            style = Paint.Style.FILL
            color = Color.RED
        }

        with(borderPaint) {
            style = Paint.Style.STROKE
            color = Color.GREEN
            strokeWidth = borderWidth
        }
    }

    private fun prepareBitmap(width: Int, height: Int) {
        maskedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)

        maskedBitmap?.let { bitmap ->
            val maskedCanvas = Canvas(bitmap)
            maskedCanvas.drawOval(rectBounds.toRectF(), maskedPaint)
        }

        resultBitmap = maskedBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        srcBitmap = drawable.toBitmap(width, height, Bitmap.Config.ARGB_8888)

        maskedPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        resultBitmap?.let { bitmap ->
            val resultCanvas = Canvas(bitmap)
            if (srcBitmap != null)
                resultCanvas.drawBitmap(srcBitmap!!, rectBounds, rectBounds, maskedPaint)
        }

    }

}