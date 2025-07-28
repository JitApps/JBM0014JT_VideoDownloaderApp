package com.kraaft.video.manager.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.DialogCommonBinding

fun Context.showCommonDialog(
    message: String = resources.getString(R.string.kk_error_unknown),
    buttonText: String = resources.getString(R.string.kk_ok), action: () -> Unit
) {
    val dialogN = Dialog(this)
    val commonBinding: DialogCommonBinding =
        DialogCommonBinding.inflate(LayoutInflater.from(this))
    dialogN.setContentView(commonBinding.getRoot())
    if (dialogN.window != null) {
        dialogN.window!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#802E2E2E")))
        dialogN.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
    dialogN.setCancelable(false)
    dialogN.setCanceledOnTouchOutside(false)
    commonBinding.btnCancel.visibility = View.GONE
    commonBinding.btnSubmit.text = buttonText
    commonBinding.btnSubmit.onSingleClick {
        dialogN.setOnDismissListener { action.invoke() }
        dialogN.dismiss()
    }
    commonBinding.tvMessage.text = message
    dialogN.show()
}

fun Context.showConfirmDialog(
    message: String = resources.getString(R.string.kk_error_unknown),
    btnYesText: String = resources.getString(R.string.kk_ok),
    btnNoText: String = resources.getString(R.string.kk_cancel),
    action: () -> Unit
) {
    try {
        val dialog = Dialog(this)
        val binding: DialogCommonBinding =
            DialogCommonBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(binding.getRoot())
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#802E2E2E")))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        binding.tvMessage.text = message
        binding.btnCancel.text = btnNoText
        binding.btnSubmit.text = btnYesText
        binding.btnSubmit.onSingleClick {
            dialog.setOnDismissListener { action.invoke() }
            dialog.dismiss()
        }
        binding.btnCancel.onSingleClick { dialog.dismiss() }
        dialog.show()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun Context.showForceDialog(
    message: String = resources.getString(R.string.kk_error_unknown),
    btnYesText: String = resources.getString(R.string.kk_ok),
    action: () -> Unit
) {
    try {
        val dialog = Dialog(this)
        val binding: DialogCommonBinding =
            DialogCommonBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(binding.getRoot())
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#802E2E2E")))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        binding.tvMessage.text = message
        binding.btnCancel.visibility = View.GONE
        binding.btnSubmit.text = btnYesText
        binding.btnSubmit.onSingleClick {
            dialog.setOnDismissListener { action.invoke() }
            dialog.dismiss()
        }
        binding.btnCancel.onSingleClick { dialog.dismiss() }
        dialog.show()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}