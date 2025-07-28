package com.kraaft.video.manager.ui.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.DialogLoadingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    private var loaderDialog: Dialog? = null
    private var appContext: Context? = null

    private fun initDialog() {
        appContext?.let {
            loaderDialog = Dialog(it, R.style.NormalDialog)
            val loadingBinding: DialogLoadingBinding = DialogLoadingBinding.inflate(layoutInflater)
            loaderDialog?.let { dialog ->
                dialog.setContentView(loadingBinding.getRoot())
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }

    fun showLoadingDialog() {
        loaderDialog ?: initDialog()
        loaderDialog?.show()
    }

    fun hideLoadingDialog() {
        loaderDialog?.let {
            if (it.isShowing)
                it.dismiss()
        }
    }

    fun Context.setMainContext() {
        appContext = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}