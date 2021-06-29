package com.example.wwubricks

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity2 : AppCompatActivity() {
    lateinit var camera:CameraFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val bitmap = intent.getParcelableExtra("BitmapImage") as Bitmap?


        if(bitmap != null){
            camera = CameraFragment(bitmap)
            supportFragmentManager.beginTransaction().apply {
                add(R.id.frame_body, camera)
                commit()
            }
        }else {
            var num = intent.getIntExtra("picNum", 1)
            var homePageFragment = HomePageFragment()
            homePageFragment.picNum = num
            homePageFragment.url = "https://www.hippyclipper.dev/pics/${num}.jpg"
            supportFragmentManager.beginTransaction().apply {
                add(R.id.frame_body, homePageFragment)
                commit()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
    }

}