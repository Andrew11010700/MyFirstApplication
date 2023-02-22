package ua.itvdn.myfirstapplication.testofpiechart

import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import kotlin.math.min

class AvatarImageViewShader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private val viewRect = Rect()

    private val avatarPaint = Paint()
    private val borderPaint = Paint()
    private val backgroundPaint = Paint()
    private val initailsPaint = Paint()

    private var borderWidth = 50f

    private var isAvatarMode = true
    private var initials = "??"

    init {

        setOnLongClickListener {
            isAvatarMode = !isAvatarMode
            invalidate()
            true
        }

        scaleType = ScaleType.CENTER_CROP
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
        val half = (borderWidth/2).toInt()
        viewRect.inset(half, half)
        setupPaints()
        prepareShader(width, height)
    }

    override fun onDraw(canvas: Canvas?) {

        if (isAvatarMode) {
            canvas?.drawOval(viewRect.toRectF(), avatarPaint)
        } else {
            canvas?.drawOval(viewRect.toRectF(), backgroundPaint)
            val offset = (initailsPaint.ascent() + initailsPaint.descent())/2
            val textBounds = Rect()
            initailsPaint.getTextBounds(initials, 0, initials.length, textBounds)
            canvas?.drawText(initials, viewRect.exactCenterX() - textBounds.width()/2, viewRect.exactCenterY() - offset, initailsPaint)
        }

        canvas?.drawOval(viewRect.toRectF(), borderPaint)
    }

    private fun prepareShader(width: Int, height: Int) {
        val srcBitmap = drawable.toBitmap(width, height, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    private fun setupPaints() {
        with(avatarPaint) {
            color = Color.RED
            style = Paint.Style.FILL
        }

        with(borderPaint) {
            style = Paint.Style.STROKE
            color = Color.YELLOW
            strokeWidth = borderWidth
        }
        with(backgroundPaint) {
            style = Paint.Style.FILL
            color = Color.CYAN
        }

        with(initailsPaint) {
            style = Paint.Style.FILL
            color = Color.WHITE
            textSize = height*0.33f
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        val savedState = SavedState(superState)

        savedState.isAvatarMode = isAvatarMode
        savedState.initials = initials

        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState

        super.onRestoreInstanceState(state.superState)

        isAvatarMode = savedState.isAvatarMode
        initials = savedState.initials
    }

    class SavedState: BaseSavedState {

        var isAvatarMode = true
        var initials = ""
        constructor(parcelable: Parcelable?): super(parcelable)

        constructor(parcel: Parcel?): super(parcel) {
            isAvatarMode = parcel?.readByte() == 1.toByte()
            initials = parcel?.readString() ?: ""
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeByte(if (isAvatarMode) 1 else 0)
            out?.writeString(initials)
        }

        @JvmField
        val CREATOR: Creator<SavedState> = object : Creator<SavedState> {
            override fun createFromParcel(source: Parcel): SavedState {
                return SavedState(source)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }

        }

    }

}