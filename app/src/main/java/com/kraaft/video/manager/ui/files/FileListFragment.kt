package com.kraaft.video.manager.ui.files

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kraaft.video.manager.R
import com.kraaft.video.manager.databinding.ActivityMainBinding
import com.kraaft.video.manager.databinding.FragmentFileListBinding
import com.kraaft.video.manager.ui.base.BaseFragment

class FileListFragment : BaseFragment() {

    private var binding: FragmentFileListBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.setMainContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileListBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}