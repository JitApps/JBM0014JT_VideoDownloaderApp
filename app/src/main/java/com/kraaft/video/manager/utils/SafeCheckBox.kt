package com.kraaft.video.manager.utils

import android.R.attr.checked
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox

class SafeCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.checkboxStyle
) : AppCompatCheckBox(context, attrs, defStyleAttr) {

    private var previousState = false
    private var userCheckedChangeListener:
            ((button: SafeCheckBox, isChecked: Boolean) -> Unit)? = null

    fun setSafeOnCheckedChangeListener(
        listener: (button: SafeCheckBox, isChecked: Boolean) -> Unit
    ) {
        userCheckedChangeListener = listener
    }

    override fun setChecked(checked: Boolean) {
        val wasPressed = isPressed
        super.setChecked(checked)

        if (previousState == checked)
            return

        previousState = checked
        // Call only if change came from user interaction
        if (wasPressed) {
            userCheckedChangeListener?.invoke(this, checked)
        }
    }
}
