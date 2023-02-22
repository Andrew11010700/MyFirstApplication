package ua.itvdn.myfirstapplication.custom_toolbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import ua.itvdn.myfirstapplication.R
import ua.itvdn.myfirstapplication.databinding.ViewCustomToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): ConstraintLayout(context, attrs, defStyle) {

    private val binding: ViewCustomToolbarBinding =
        ViewCustomToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar, defStyle, 0)
        val title: String? = typedArray.getString(R.styleable.CustomToolbar_ctbTitle)
        val color = typedArray.getColor(R.styleable.CustomToolbar_ctbColor, 0)
        val isBtnStartEnabled = typedArray.getBoolean(R.styleable.CustomToolbar_ctbIsBtnStart, true)
        val isBtnEndEnabled = typedArray.getBoolean(R.styleable.CustomToolbar_ctbIsBtnEnd, false)
        val drawableRipple: Drawable? = typedArray.getDrawable(R.styleable.CustomToolbar_ctbBackgroundDrawable)
        val startDrawable = typedArray.getResourceId(R.styleable.CustomToolbar_ctbStartDrawable, DEFAULT_START_DRAWABLE)
        val endDrawable = typedArray.getResourceId(R.styleable.CustomToolbar_ctbEndDrawable, 0)
        setTitle(title)
        setColor(color)
        setBtnStartEnabled(isBtnStartEnabled)
        setBtnEndEnabled(isBtnEndEnabled)
        setStartDrawable(startDrawable)
        setEndDrawable(endDrawable)
        setRipple(drawableRipple)
    }

    fun setTitle(title: String?) {
        binding.tvTitle.text = title
    }

    fun setColor(color: Int) {
        if (color != 0) {
            binding.tvTitle.setTextColor(color)
            binding.btnStart.drawable.mutate().setTint(color)
        }
    }

    fun setBtnStartEnabled(isEnabled: Boolean) {
        binding.btnStart.visibility = if (isEnabled) View.VISIBLE else View.GONE
    }

    fun setBtnEndEnabled(isEnabled: Boolean) {
        binding.btnEnd.visibility = if (isEnabled) View.VISIBLE else View.GONE
    }

    fun setStartDrawable(drawableId: Int) {
        binding.btnStart.setImageDrawable(ContextCompat.getDrawable(context, drawableId))
    }

    fun setEndDrawable(drawableId: Int) {
        if (drawableId != 0) {
            binding.btnEnd.setImageDrawable(ContextCompat.getDrawable(context, drawableId))
        }
    }

    fun setRipple(drawable: Drawable?) {
        drawable ?: return
        binding.btnStart.background = drawable
        binding.btnEnd.background = drawable
    }

    companion object {
        const val DEFAULT_START_DRAWABLE = R.drawable.ic_back
    }

}