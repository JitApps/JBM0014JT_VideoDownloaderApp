package com.kraaft.video.manager.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityGlobalBinding
import com.kraaft.video.manager.ui.adapter.PagerFragmentAdapter
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.fragment.FileListFragment
import com.kraaft.video.manager.ui.viewmodels.StatusViewModel
import com.kraaft.video.manager.utils.beVisible
import com.kraaft.video.manager.utils.getDownloadsPath
import com.kraaft.video.manager.utils.gotoActivity
import com.kraaft.video.manager.utils.isNotEmpty
import com.kraaft.video.manager.utils.onSingleClick
import com.kraaft.video.manager.utils.showDownloadDialog
import com.kraaft.video.manager.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class GlobalActivity : BaseActivity() {

    private var binding: ActivityGlobalBinding? = null
    private var fileListFragment: FileListFragment? = null

    private val viewModel: StatusViewModel by viewModels()
    private var downloadType = ""

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        onClick()
    }

    fun getIntentData() {
        downloadType = intent.getStringExtra("download") ?: ""
        when (downloadType) {
            "josh" -> binding?.ivLogo?.setImageResource(R.drawable.icon_josh)
            "chingari" -> binding?.ivLogo?.setImageResource(R.drawable.icon_chingari)
            else -> binding?.ivLogo?.setImageResource(R.drawable.start_logo)
        }
    }

    private fun onClick() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding?.apply {
            includedToolbar.btnBack.beVisible()
            includedToolbar.btnBack.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            btnDownload.onSingleClick {
                if (binding?.etUrl?.isNotEmpty(this@GlobalActivity) == true) {
                    downloadFile(binding?.etUrl?.text.toString())
                }
            }
        }
    }

    private fun downloadFile(url: String) {
        showLoadingDialog(getString(R.string.kk_fetching_please_wait))
        lifecycleScope.launch {
            val myDoc = Jsoup.connect(url).get()
            val result =
                myDoc.select("meta[property=\"og:video:secure_url\"]").last()?.attr("content") ?: ""
            withContext(Dispatchers.Main)
            {
                hideLoadingDialog()
                if (result.isNotEmpty()) {
                    this@GlobalActivity.showDownloadDialog(
                        folderPath = getDownloadsPath(),
                        filePath = result
                    ) {

                    }
                } else {
                    showToast("Download Failed")
                }
            }
        }
    }
}