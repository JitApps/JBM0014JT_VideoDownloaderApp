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
    cvRootError.visibility = View.VISIBLE
    cvLoading.visibility = View.VISIBLE
    cvMain.visibility = View.GONE
    cvError.visibility = View.GONE
}

fun LayoutErrorBinding.showRetry(
    message: String,
    cvMain: ConstraintLayout,
    buttonText: String = "Retry",
    action: () -> Unit
) {
    cvRootError.visibility = View.VISIBLE
    cvMain.visibility = View.GONE
    cvLoading.visibility = View.GONE
    cvError.visibility = View.VISIBLE
    btnRetry.visibility = View.VISIBLE
    tvMessage.text = message
    btnRetry.text = buttonText
    btnRetry.onSingleClick {
        action.invoke()
    }
}

fun LayoutErrorBinding.showError(cvMain: ConstraintLayout) {
    cvRootError.visibility = View.VISIBLE
    cvMain.visibility = View.GONE
    cvLoading.visibility = View.GONE
    cvError.visibility = View.VISIBLE
    btnRetry.visibility = View.GONE
}

fun LayoutErrorBinding.showPage(cvMain: ConstraintLayout) {
    cvRootError.visibility = View.GONE
    cvMain.visibility = View.VISIBLE
}
