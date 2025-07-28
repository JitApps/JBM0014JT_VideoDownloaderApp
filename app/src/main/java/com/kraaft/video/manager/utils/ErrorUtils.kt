package com.kraaft.video.manager.utils

import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.LayoutErrorBinding
import org.json.JSONObject

fun EditText.isNotEmpty(context: Context): Boolean {
    if (text.isEmpty()) {
        error = context.getString(R.string.kk_error_empty_edit)
    }
    return text.isNotEmpty()
}

fun Context.getErrorMessage(message: String): String {
    return message.ifEmpty {
        getString(R.string.kk_error_unknown)
    }
}

fun Context.showErrorToast(data: String) {
    try {
        val response = JSONObject(data)
        showToast(getErrorMessage(response.optString("message")))
    } catch (e: Exception) {
        showToast(data.ifEmpty { resources.getString(R.string.kk_error_unknown) })
    }
}


fun LayoutErrorBinding.showLoading(cvMain: ConstraintLayout) {
    cvLoading.visibility = View.VISIBLE
    cvMain.visibility = View.GONE
    cvError.visibility = View.GONE
}

fun LayoutErrorBinding.showRetry(
    message: String,
    cvMain: ConstraintLayout,
    action: () -> Unit
) {
    cvMain.visibility = View.GONE
    cvLoading.visibility = View.GONE
    tvMessage.text = message
    cvError.visibility = View.VISIBLE
    btnRetry.onSingleClick {
        showLoading(cvMain)
        action.invoke()
    }
}

fun LayoutErrorBinding.showError(cvMain: ConstraintLayout) {
    cvMain.visibility = View.GONE
    cvLoading.visibility = View.GONE
    cvError.visibility = View.GONE
    btnRetry.visibility = View.VISIBLE
}

fun LayoutErrorBinding.showPage(cvMain: ConstraintLayout) {
    cvError.visibility = View.GONE
    cvMain.visibility = View.VISIBLE
}
