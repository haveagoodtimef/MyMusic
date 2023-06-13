package com.fhz.music_automotive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fhz.music_automotive.databinding.ActivityAutomotiveMainBinding

class AutomotiveMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAutomotiveMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutomotiveMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.play.setOnClickListener{

        }
    }
}