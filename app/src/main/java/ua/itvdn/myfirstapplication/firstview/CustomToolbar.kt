package ua.itvdn.myfirstapplication.firstview

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import ua.itvdn.myfirstapplication.R
import ua.itvdn.myfirstapplication.databinding.ViewCustomToolbarBinding
import ua.itvdn.myfirstapplication.extensions.dpToPx
import ua.itvdn.myfirstapplication.extensions.setMargins
import ua.itvdn.myfirstapplication.extensions.showOrGone


class CustomToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCustomToolbarBinding =
        ViewCustomToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    private var title: String? = null
    private var startDrawableId = 0
    private var endDrawableId = 0
    private var startListener: ((View) -> Unit)? = null
    private var isEditingMode = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar, defStyleAttr, 0)
        setColor(typedArray.getColor(R.styleable.CustomToolbar_ctbColor, 0))
        setTitle(typedArray.getString(R.styleable.CustomToolbar_ctbTitle))
        setBtnStartDrawable(
            typedArray.getResourceId(
                R.styleable.CustomToolbar_ctbStartDrawable,
                DEFAULT_START_DRAWABLE
            )
        )
        setBtnEndDrawable(typedArray.getDrawable(R.styleable.CustomToolbar_ctbEndDrawable))
        setBtnStartVisibility(typedArray.getBoolean(R.styleable.CustomToolbar_ctbIsBtnStart, true))
        setBtnEndVisibility(typedArray.getBoolean(R.styleable.CustomToolbar_ctbIsBtnEnd, false))
        setBtnBackground(
            typedArray.getResourceId(
                R.styleable.CustomToolbar_ctbBackgroundDrawable,
                DEFAULT_BACKGROUND_DRAWABLE
            )
        )
        val endBtnTint: Int = typedArray.getColor(R.styleable.CustomToolbar_ctbEndIconTint, 0)
        if (endBtnTint != 0) {
            binding.btnEnd.setColorFilter(endBtnTint, PorterDuff.Mode.SRC_IN)
        }
        typedArray.recycle()

        setOnEndListener {
            setTitle("Editing...")
            setBtnStartDrawable(R.drawable.ic_close)
            isEditingMode = true
            startListener ?: return@setOnEndListener
            setOnStartListener(startListener!!)
        }
    }

    fun setOnStartListener (listener: (View) -> Unit) {
        startListener = listener

        if (isEditingMode.not()) {
            binding.btnStart.setOnClickListener(startListener)
        } else {
            binding.btnStart.setOnClickListener {
                setTitle("Settings...")
                setBtnStartDrawable(DEFAULT_START_DRAWABLE)
                binding.btnStart.setOnClickListener(startListener)
            }
        }
    }

    fun setOnEndListener (listener: (View) -> Unit) {
        binding.btnEnd.setOnClickListener(listener)
    }

    fun setColor(color: Int) {
        if (color != 0) {
            with(binding) {
                tvTitle.setTextColor(color)
                btnStart.drawable.mutate().setTint(color)
            }
        }
    }

    fun setTitle(title: String?) {
        this.title = title
        binding.tvTitle.text = title
    }

    fun setBtnStartDrawable(drawableId: Int) {
        if (drawableId != 0) {
            startDrawableId = drawableId
            binding.btnStart.setImageDrawable(ContextCompat.getDrawable(context, drawableId))
        }
    }

    fun setBtnEndDrawable(drawable: Drawable?) {
        binding.btnEnd.setImageDrawable(drawable)
    }

    fun setBtnStartVisibility(isVisible: Boolean) {
        with(binding) {
            btnStart.showOrGone(isVisible)
            tvTitle.setMargins(left = if (isVisible) 4.dpToPx() else 24.dpToPx())
        }
    }

    fun setBtnEndVisibility(isVisible: Boolean) {
        with(binding) {
            btnEnd.showOrGone(isVisible)
            tvTitle.setMargins(right = if (isVisible) 4.dpToPx() else 24.dpToPx())
        }
    }

    fun setBtnBackground(drawable: Int) {
        with(binding) {
            btnStart.background = ContextCompat.getDrawable(context, drawable)
            btnEnd.background = ContextCompat.getDrawable(context, drawable)
        }
    }

    fun removeStartListener() {
        binding.btnStart.setOnClickListener(null)
    }

    fun removeEndListener() {
        binding.btnEnd.setOnClickListener(null)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        val myState = SavedState(superState)
        myState.title = title
        myState.startDrawableId = startDrawableId
        myState.isEditingMode = isEditingMode

        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState

        super.onRestoreInstanceState(savedState.superState)

        title = savedState.title
        startDrawableId = savedState.startDrawableId
        isEditingMode = savedState.isEditingMode

        setTitle(title)
        setBtnStartDrawable(startDrawableId)
        startListener ?: return
        setOnStartListener(startListener!!)
    }

    companion object {
        const val DEFAULT_BACKGROUND_DRAWABLE = R.drawable.bg_ripple_primary
        const val DEFAULT_START_DRAWABLE = R.drawable.ic_back
    }

    private class SavedState : BaseSavedState {
        var title: String? = null
        var startDrawableId = 0
        var isEditingMode = false

        internal constructor(superState: Parcelable?) : super(superState) {}
        constructor(`in`: Parcel) : super(`in`) {
            title = `in`.readString()
            startDrawableId = `in`.readInt()
            isEditingMode = `in`.readByte() == 1.toByte()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(title)
            out.writeInt(startDrawableId)
            out.writeByte( if (isEditingMode) 1 else 0)
        }

        @JvmField
        val CREATOR: Parcelable.Creator<SavedState?> =
            object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
    }

}