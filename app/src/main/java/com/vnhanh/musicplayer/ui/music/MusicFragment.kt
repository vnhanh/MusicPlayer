package com.vnhanh.musicplayer.ui.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.vnhanh.musicplayer.R

class MusicFragment : Fragment() {

    private lateinit var musicViewModel: MusicViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        musicViewModel =
                ViewModelProviders.of(this).get(MusicViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_music, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        musicViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
