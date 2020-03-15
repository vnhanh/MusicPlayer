package com.vnhanh.musicplayer.ui.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.vnhanh.musicplayer.R

class VideoFragment : Fragment() {

    private lateinit var videoViewModel: VideoViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        videoViewModel =
                ViewModelProviders.of(this).get(VideoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_video, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        videoViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
