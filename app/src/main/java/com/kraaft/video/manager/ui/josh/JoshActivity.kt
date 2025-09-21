package com.kraaft.video.manager.ui.josh

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.kraaft.driver.manager.ui.main.PagerFragmentAdapter
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityJoshBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.ui.common.StatusViewModel
import com.kraaft.video.manager.ui.downloads.DownloadActivity
import com.kraaft.video.manager.ui.files.FileListFragment
import com.kraaft.video.manager.utils.getJoshPath
import com.kraaft.video.manager.utils.gotoActivity
import com.kraaft.video.manager.utils.isNotEmpty
import com.kraaft.video.manager.utils.onSingleClick
import com.kraaft.video.manager.utils.showDownloadDialog
import com.kraaft.video.manager.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup

class JoshActivity : BaseActivity() {
    private var binding: ActivityJoshBinding? = null
    private var fileListFragment: FileListFragment? = null
    private val viewModel: StatusViewModel by viewModels()

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoshBinding.inflate(layoutInflater)
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
                if (binding?.etUrl?.isNotEmpty(this@JoshActivity) == true) {
                    downloadFile(binding?.etUrl?.text.toString())
                }
            }
        }
    }

    private fun loadFragment() {
        fileListFragment = FileListFragment.getInstance(
            getJoshPath(),
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
                myDoc.select("script[id=\"__NEXT_DATA__\"]").last()?.html() ?: ""
            val jsonObject = JSONObject(url)
            val vidUrl = jsonObject.getJSONObject("props")
                .getJSONObject("pageProps").getJSONObject("detail")
                .getJSONObject("data").getString("mp4_url").toString()
            withContext(Dispatchers.Main)
            {
                hideLoadingDialog()
                if (vidUrl.isNotEmpty()) {
                    this@JoshActivity.showDownloadDialog(
                        folderPath = getJoshPath(),
                        filePath = result
                    ) {
                        fileListFragment?.viewModel?.fetchDownloads(getJoshPath())
                    }
                } else {
                    showToast("Download Failed")
                }
            }
        }
    }
}