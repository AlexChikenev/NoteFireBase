package com.example.notefirebase.fragments.test

import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.media.Ringtone
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notefirebase.databinding.FragmentBlankBinding
import com.example.notefirebase.receivers.TestAlarmReceiver


class BlankFragment : Fragment() {

    private lateinit var binding: FragmentBlankBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTest.setOnClickListener {
            val notification = Intent(requireContext(), TestAlarmReceiver::class.java)
            notification.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, notification,  PendingIntent.FLAG_IMMUTABLE)
            pendingIntent.send()
        }
    }

    companion object {
        fun newInstance() = BlankFragment()
    }
}