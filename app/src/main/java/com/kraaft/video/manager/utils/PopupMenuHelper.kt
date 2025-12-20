package com.kraaft.video.manager.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.kraaft.video.manager.R
import kotlin.math.roundToInt

object PopupMenuHelper {

    data class Option(
        val id: Int,
        val title: String
    )

    private var popupWindow: PopupWindow? = null

    fun show(
        anchor: View,
        options: List<Option>,
        onClick: (Option) -> Unit
    ) {
        popupWindow?.dismiss()

        val context = anchor.context

        val margin =
            (context.resources.getDimension(R.dimen.popup_margin) / context.resources.displayMetrics.density).roundToInt()
        val newTextSize =
            (context.resources.getDimension(R.dimen.popup_text_size) / context.resources.displayMetrics.density)

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_round_shape_10) // rounded bg
            elevation = 12f
            setPadding(margin * 2, margin, margin * 2, margin)
        }

        options.forEach { option ->
            val item = TextView(context).apply {
                text = option.title
                textSize = newTextSize
                setTextColor(Color.BLACK)
                setPadding(margin, margin, margin, margin)
                setOnClickListener {
                    popupWindow?.dismiss()
                    onClick(option)
                }
            }
            container.addView(item)
        }

        container.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupHeight = container.measuredHeight
        val popupWidth = container.measuredWidth

        popupWindow = PopupWindow(
            container,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = 12f
        }

        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        val anchorY = location[1]
        val screenHeight = context.resources.displayMetrics.heightPixels
        val spaceBelow = screenHeight - (anchorY + anchor.height)

        val marginPx = -margin * 3
        val xOffset = -(popupWidth + marginPx)

        val yOffset = if (spaceBelow < popupHeight) {
            -popupHeight + anchor.height
        } else {
            0
        }
        popupWindow?.showAsDropDown(anchor, xOffset, yOffset)

    }

    fun dismiss() {
        popupWindow?.dismiss()
    }
}
