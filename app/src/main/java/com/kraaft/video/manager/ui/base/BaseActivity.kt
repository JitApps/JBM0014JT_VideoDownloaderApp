package com.kraaft.video.manager.ui.base

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.DialogLoadingBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    private var loaderDialog: Dialog? = null

    private fun initDialog(message: String) {
        loaderDialog = Dialog(this, R.style.NormalDialog)
        val loadingBinding: DialogLoadingBinding = DialogLoadingBinding.inflate(layoutInflater)
        loadingBinding.tvMessage.text = message
        loaderDialog?.let {
            it.setContentView(loadingBinding.getRoot())
            it.setCancelable(false)
            it.setCanceledOnTouchOutside(false)
            it.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            it.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    private fun hideSystemBars() {
        val controller =
            WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    fun showLoadingDialog(message: String = getString(R.string.kk_loading_please_wait)) {
        try {
            if (!isDestroyed && !isFinishing) {
                loaderDialog ?: initDialog(message)
                loaderDialog?.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
    }

    fun hideLoadingDialog() {
        try {
            loaderDialog?.let {
                if (it.isShowing)
                    it.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}