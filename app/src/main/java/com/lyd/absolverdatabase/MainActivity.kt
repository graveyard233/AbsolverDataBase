package com.lyd.absolverdatabase

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

import androidx.databinding.DataBindingUtil
import com.lyd.absolverdatabase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mainBinding : ActivityMainBinding ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        mainBinding?.lifecycleOwner = this

    }


}