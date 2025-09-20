package com.kraaft.video.manager.ui.chingari

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kraaft.driver.manager.ui.main.PagerFragmentAdapter
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityChingariBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.common.StatusViewModel
import com.kraaft.video.manager.ui.downloads.DownloadActivity
import com.kraaft.video.manager.ui.files.FileListFragment
import com.kraaft.video.manager.utils.downloadFile
import com.kraaft.video.manager.utils.getChingariPath
import com.kraaft.video.manager.utils.gotoActivity
import com.kraaft.video.manager.utils.isNotEmpty
import com.kraaft.video.manager.utils.onSingleClick
import com.kraaft.video.manager.utils.showDownloadDialog
import com.kraaft.video.manager.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class ChingariActivity : BaseActivity() {

    private var binding: ActivityChingariBinding? = null
    private var fileListFragment: FileListFragment? = null

    private val viewModel: StatusViewModel by viewModels()

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChingariBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        loadFragment()
        onClick()
    }

    private fun onClick() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding?.apply {
            includedToolbar.btnBack.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            btnGallery.onSingleClick {
                gotoActivity(DownloadActivity::class.java, false)
            }
            btnDownload.onSingleClick {
                if (binding?.etUrl?.isNotEmpty(this@ChingariActivity) == true) {
                    downloadFile(binding?.etUrl?.text.toString())
                }
            }
        }
    }

    private fun loadFragment() {
        fileListFragment = FileListFragment.getInstance(
            getChingariPath(),
            false
        )
        fileListFragment?.let { fragment ->
            binding?.viewPager?.apply {
                adapter = PagerFragmentAdapter(supportFragmentManager, lifecycle).apply {
                    addFragment(fragment, "")
                }
            }
        }
    }

    private fun downloadFile(url: String) {
        showLoadingDialog(getString(R.string.kk_fetching_please_wait))
        lifecycleScope.launch(Dispatchers.IO) {
            val myDoc = Jsoup.connect(url).get()
            val result =
                myDoc.select("meta[property=\"og:video:secure_url\"]").last()?.attr("content")?:""
            withContext(Dispatchers.Main)
            {
                hideLoadingDialog()
                if (result.isNotEmpty()) {
                    this@ChingariActivity.showDownloadDialog(
                        folderPath = getChingariPath(),
                        filePath = result
                    ) {
                        viewModel.fetchDownloads(getChingariPath())
                    }
                } else {
                    showToast("Download Failed")
                }
            }
        }
    }
}