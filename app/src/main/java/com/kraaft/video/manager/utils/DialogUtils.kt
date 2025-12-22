package com.kraaft.video.manager.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.utils.Utils
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
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

@SuppressLint("SetTextI18n")
fun Context.showDownloadDialog(
    folderPath: String = "",
    filePath: String = "",
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
            dialog.window!!.setBackgroundDrawable("#802E2E2E".toColorInt().toDrawable())
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val fileName = System.currentTimeMillis().toString() + ".png"
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener {
            downloadFile(
                binding.btnSubmit,
                binding.tvMessage,
                folderPath,
                filePath,
                fileName,
                action
            )
        }
        binding.tvMessage.text = message
        binding.btnCancel.text = btnNoText
        binding.btnSubmit.text = btnYesText
        binding.btnSubmit.visibility = View.GONE
        binding.btnSubmit.onSingleClick {
            binding.btnSubmit.visibility = View.GONE
            downloadFile(
                binding.btnSubmit,
                binding.tvMessage,
                folderPath,
                filePath,
                fileName,
                action
            )
        }
        binding.btnCancel.onSingleClick { dialog.dismiss() }
        dialog.show()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun Context.downloadFile(
    btnRetry: MaterialButton,
    tvMessage: TextView,
    folderPath: String,
    filePath: String,
    fileName: String,
    callback: () -> Unit
) {
    PRDownloader.download(filePath, folderPath, fileName)
        .build()
        .setOnProgressListener { progress ->
            val pro =
                (((progress.currentBytes.toDouble() / progress.totalBytes) * 100.0).toInt())
            tvMessage.text = "Downloading $pro %"
        }.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                showToast("Download Completed")
                callback.invoke()
            }

            override fun onError(error: com.downloader.Error) {
                tvMessage.text = "Download Failed"
                showToast("Download Failed")
                btnRetry.visibility = View.VISIBLE
            }
        })
}

fun Context.downloadFile(
    fileUrl: String,
    folderPath: String,
    fileName: String,
    progressCallBack: (Long, Long) -> Unit,
    callback: (Boolean, String) -> Unit
) {
    PRDownloader.download(fileUrl, folderPath, fileName)
        .build()
        .setOnProgressListener { progress ->
            progressCallBack.invoke(progress.currentBytes, progress.totalBytes)
        }.start(object : OnDownloadListener {
            override fun onDownloadComplete() {
                callback.invoke(true, "")
            }

            override fun onError(error: com.downloader.Error) {
                callback.invoke(false, "Download Failed")
            }
        })
}