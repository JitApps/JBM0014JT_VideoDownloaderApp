package com.kraaft.video.manager.ui.activity

import android.content.Intent
import android.os.Bundle
import com.kraaft.video.manager.databinding.ActivityMainBinding
import com.kraaft.video.manager.ui.base.BaseActivity
import com.kraaft.video.manager.utils.PERMISSION_SOUND
import com.kraaft.video.manager.utils.PERMISSION_VIDEO
import com.kraaft.video.manager.utils.askPermissions
import com.kraaft.video.manager.utils.gotoActivity
import com.kraaft.video.manager.utils.gotoIntent
import com.kraaft.video.manager.utils.onSingleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        onClick()
    }

    private fun onClick() {
        binding?.apply {
            btnStatus.onSingleClick {
                gotoActivity(StatusActivity::class.java, false)
            }

            btnDownloader.onSingleClick {
                gotoActivity(HomeActivity::class.java, false)
            }

            btnDownloads.onSingleClick {
                gotoActivity(DownloadActivity::class.java, false)
            }

            btnVideoPlayer.onSingleClick {
                askPermissions(listOf(PERMISSION_VIDEO)) {
                    gotoActivity(MediaListActivity::class.java, false)
                }
            }

            btnMusicPlayer.onSingleClick {
                askPermissions(listOf(PERMISSION_SOUND)) {
                    gotoIntent(Intent(this@MainActivity, MediaListActivity::class.java).apply {
                        putExtra("isSound", true)
                    }, false)
                }
            }
        }
    }

}