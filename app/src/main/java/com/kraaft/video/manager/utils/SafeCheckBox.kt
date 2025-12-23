package com.kraaft.video.manager.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox

class SafeCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.checkboxStyle
) : AppCompatCheckBox(context, attrs, defStyleAttr) {

    // Flag to prevent triggering listener on programmatic changes
    private var isProgrammaticChange = false

    private var userCheckedChangeListener: ((button: SafeCheckBox, isChecked: Boolean) -> Unit)? = null

    // Override setOnCheckedChangeListener
    fun setSafeOnCheckedChangeListener(listener: (button: SafeCheckBox, isChecked: Boolean) -> Unit) {
        this.userCheckedChangeListener = listener
    }

    override fun setChecked(checked: Boolean) {
        if (this.isChecked != checked) {
            isProgrammaticChange = true
            super.setChecked(checked)
            isProgrammaticChange = false
        }
    }

    override fun toggle() {
        isProgrammaticChange = false // ensure user toggle
        super.toggle()
    }

    init {
        super.setOnCheckedChangeListener { _, isChecked ->
            if (!isProgrammaticChange) {
                userCheckedChangeListener?.invoke(this, isChecked)
            }
        }
    }
}