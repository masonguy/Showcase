package com.example.wwubricks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity(), MyimageRecyclerViewAdapter.RecyclerViewListener{
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }
    private var mainPage:Boolean = true
    private var mainPageImage:Int = 1
    lateinit var homePageFragment: HomePageFragment
    lateinit var galleryFragment: imageFragment
    lateinit var byteArray: ByteArray
    var onCamera:Boolean = false
    val LAUNCH_SECOND_ACTIVITY = 3
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var cameraButton: ImageButton = findViewById(R.id.camera_button)
        var allImagesButton: ImageButton = findViewById(R.id.all_images_button)
        var homePageButton: ImageButton = findViewById(R.id.homepage_button)
        homePageFragment = HomePageFragment()
        galleryFragment = imageFragment()
        if (savedInstanceState != null) {
            mainPage = savedInstanceState.getBoolean("Current Instance", true)
            mainPageImage = savedInstanceState.getInt("Current Pic", 1)
            homePageFragment.picNum = mainPageImage
            homePageFragment.url = "https://www.hippyclipper.dev/pics/${mainPageImage}.jpg"
            Log.d("DEBUG CAMERA", savedInstanceState.getBoolean("Camera", false).toString())
            if(savedInstanceState.getBoolean("Camera", false)){
                var intent = Intent(this, MainActivity2::class.java)
                byteArray = savedInstanceState.getByteArray("Camera pic")!!
                with(intent){
                    putExtra("BitmapImage",BitmapFactory.decodeByteArray(byteArray,0,byteArray.size))
                    startActivity(this)
                }
            }
        }

        val dbman: DatabaseManager

        if (!doesDatabaseExist(this, "MyDB")) {
            dbman = DatabaseManager(this)
            var parser = ParseJSON(assets, "images_and_scores.json")
            parser.addToDatabase(dbman)
            Log.d("DBINSERT", "insert")
        } else {
            Log.d("DBFAILED", "FAILED")
        }

      /*  var intent = Intent(this, MainActivity2::class.java)
        with(intent){
            startActivity(this)
        }*/
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_body, galleryFragment)
            add(R.id.frame_body, homePageFragment)
            if(mainPage){
                hide(galleryFragment)
                homePageButton.setImageResource(R.drawable.ic_baseline_home_clicked)
            }else{
                hide(homePageFragment)
                allImagesButton.setImageResource(R.drawable.ic_round_view_clicked)
            }
            commit()
        }

        cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                onCamera = true
                Log.d("DEBUG CAMERA", "In camera")
                startActivityForResult(intent, CAMERA_REQUEST_CODE)

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }

        allImagesButton.setOnClickListener {
            mainPage = false
            allImagesButton.setImageResource(R.drawable.ic_round_view_clicked)
            homePageButton.setImageResource(R.drawable.ic_baseline_home_24)
            supportFragmentManager.beginTransaction().apply {
                hide(homePageFragment)
                show(galleryFragment)
                commit()

            }
            var adapter = galleryFragment.view.adapter as MyimageRecyclerViewAdapter
            adapter.listener = this
        }

        homePageButton.setOnClickListener {
            mainPage = true
            homePageButton.setImageResource(R.drawable.ic_baseline_home_clicked)
            allImagesButton.setImageResource(R.drawable.ic_round_view_module_24)
            supportFragmentManager.beginTransaction().apply {
                hide(galleryFragment)
                show(homePageFragment)
                commit()
            }


        }


    }

    override fun calledFromRecycler(position: Int) {

        Log.d("HI", "$position")
        val num: Int = position % 23 + 1

        val intent = Intent(this, MainActivity2::class.java)
        with(intent) {
            putExtra("picNum", num)
            startActivity(this)
        }
    }

    private fun doesDatabaseExist(context: Context, dbName: String): Boolean {
        val dbFile: File = context.getDatabasePath(dbName)
        return dbFile.exists()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }else{
                Toast.makeText(
                    this,
                    "Oops you just denied permission for camera",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA_REQUEST_CODE){
                val thumbNail: Bitmap = data!!.extras!!.get("data") as Bitmap
                val stream = ByteArrayOutputStream()
                thumbNail.compress(Bitmap.CompressFormat.PNG, 100, stream)
                byteArray = stream.toByteArray()
                var intent = Intent(this, MainActivity2::class.java)
                with(intent){
                    putExtra("BitmapImage", thumbNail)
                    startActivityForResult(this, LAUNCH_SECOND_ACTIVITY)
                }
            }
        }

        if (requestCode === LAUNCH_SECOND_ACTIVITY) {
            if (resultCode === RESULT_CANCELED) {
                Log.d("DEBUG CAMERA", "out camera")
                onCamera = false
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("Current Instance", mainPage)
        outState.putInt("Current Pic",  homePageFragment.picNum)
        outState.putBoolean("Camera", onCamera)
        if(onCamera && this::byteArray.isInitialized){
            outState.putByteArray("Camera pic", byteArray)
        }

    }

}

