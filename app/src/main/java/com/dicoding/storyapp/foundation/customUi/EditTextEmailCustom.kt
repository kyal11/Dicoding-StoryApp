package com.dicoding.storyapp.foundation.customUi

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R

class EditTextEmailCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var clearButtonImage: Drawable

    init {
        setBackgroundResource(R.drawable.edittext_border)
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
        hideClearButton()
        setOnTouchListener(this)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    setError(context.getString(R.string.invalid_email), null)
                } else {
                    error = null
                }
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hint == null) {
            hint = context.getString(R.string.email)
        }
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawablesRelative[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            val isClearButtonClicked: Boolean

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                isClearButtonClicked = event.x < clearButtonEnd
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                isClearButtonClicked = event.x > clearButtonStart
            }

            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        text?.clear()
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            }
        }
        return false
    }
    override fun performClick(): Boolean {
        return super.performClick()
    }
}
