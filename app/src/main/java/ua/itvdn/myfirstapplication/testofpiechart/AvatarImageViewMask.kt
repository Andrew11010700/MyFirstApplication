package ua.itvdn.myfirstapplication.testofpiechart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import kotlin.math.min

class AvatarImageViewMask @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private val viewRect = Rect()

    private var resultBm: Bitmap? = null
    private var maskBitmap: Bitmap? = null
    private var srcBitmap: Bitmap? = null

    private val maskPaint = Paint()
    private val borderPaint = Paint()

    private var borderWidth= 50f

    init {
        scaleType = ScaleType.CENTER_CROP
        setup()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val initialSize = min(width, height)
        Log.d("AvatarSize", "onMeasure: width = $width, height = $height")
        setMeasuredDimension(initialSize, initialSize)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        viewRect.set(0, 0, width, height)
        val half = (borderWidth / 2).toInt()
        viewRect.inset(half, half)
        prepareBitmap(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        if (resultBm != null) {
            canvas?.drawBitmap(resultBm!!, viewRect, viewRect, null)
        }

        canvas?.drawOval(viewRect.toRectF(), borderPaint)
    }

    private fun prepareBitmap(width: Int, height: Int) {
        maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)

        val maskCanvas = Canvas(maskBitmap!!)
        maskCanvas.drawOval(viewRect.toRectF(), maskPaint)

        resultBm = maskBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        srcBitmap = drawable.toBitmap(width, height, Bitmap.Config.ARGB_8888)

        val resultCanvas = Canvas(resultBm!!)
        resultCanvas.drawBitmap(maskBitmap!!, viewRect, viewRect, null)
        resultCanvas.drawBitmap(srcBitmap!!, viewRect, viewRect, maskPaint)
    }

    private fun setup() {
        with(maskPaint) {
            color = Color.RED
            style = Paint.Style.FILL
        }

        with(borderPaint) {
            style = Paint.Style.STROKE
            color = Color.YELLOW
            strokeWidth = borderWidth
        }
    }

}