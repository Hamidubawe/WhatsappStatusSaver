package com.freestatusaver

import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_photo.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

class VideoActivity : AppCompatActivity() {
    private lateinit var paths: String
    private lateinit var uri: String
    private lateinit var fileName: String
    private lateinit var name: String
    private lateinit var statusDownload: FloatingActionButton
    private lateinit var videoView: VideoView
    private var mediaController: android.widget.MediaController? = null
    private lateinit var file: File
    private var mAdView: AdView? = null
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adRequest: AdRequest
    private lateinit var floatingActionMenu: FloatingActionMenu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        supportActionBar!!.hide()

        floatingActionMenu = findViewById(R.id.floatBtn)

        //initializing ads
        adRequest = AdRequest.Builder().build()
        mAdView = findViewById(R.id.adView)
        mAdView!!.loadAd(adRequest)

        //initialize inter ads
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd!!.adUnitId = Constants.InsterstialAd
        mInterstitialAd!!.loadAd(adRequest)

        mInterstitialAd!!.adListener = object : AdListener(){

            override fun onAdClosed() {
                super.onAdClosed()
                mInterstitialAd!!.loadAd(AdRequest.Builder().build())

            }

        }

        paths = intent.getStringExtra("path")!!
        uri = intent.getStringExtra("photoUri")!!
        fileName = intent.getStringExtra("filename")!!
        name = intent.getStringExtra("name")!!
        videoView = findViewById(R.id.videoPlayer)

        //statusDownload = findViewById(R.id.saveButton)


        file = File(paths)

        val path = intent.getStringExtra("path")!!
        val file = File(path)

        val imageUri = FileProvider.getUriForFile(
            this,
            "com.freestatusaver.fileprovider",  //(use your app signature + ".provider" )
            file
        )

        if (mediaController == null) {
            mediaController = android.widget.MediaController(this)
            mediaController!!.setAnchorView(videoView)

        }

        videoView.setMediaController(mediaController)

        try {
            videoView.setVideoPath(file.toString())
            videoView.requestFocus()
            videoView.start()
        } catch (e: NullPointerException) {
            e.printStackTrace()

        }catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }


        videoView.setOnCompletionListener {
            videoView.setVideoPath(paths)
            videoView.start()
        }

        val i = "Try this awesome status saver, You can save and share all statuses even when you are offline " +
                "Download the app for free on play store" + " https://play.google.com/store/apps/details?id=com.freestatusaver"


        saveTODevice.setOnClickListener {

            checkFolder()

            val destinationPath = Environment.getExternalStorageDirectory().absolutePath +
                    Constants.SAVE_FOLDER
            val destFile = File(destinationPath)

            try {
                if (mInterstitialAd!!.isLoaded){
                    mInterstitialAd!!.show()
                }

                FileUtils.copyFileToDirectory(file, destFile)
                MediaScannerConnection.scanFile(this,
                    arrayOf(destinationPath + fileName),
                    arrayOf("*/*"),
                    object : MediaScannerConnection.MediaScannerConnectionClient {
                        override fun onMediaScannerConnected() {

                        }

                        override fun onScanCompleted(path: String, uri: Uri) {


                        }
                    })
                Toast.makeText(this, "Saved to $destinationPath+$name", Toast.LENGTH_SHORT)
                    .show()

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to Save", Toast.LENGTH_SHORT).show()
            }


        }

        sharePhoto.setOnClickListener {

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, i)
                type = "video/*"
            }
            startActivity(Intent.createChooser(shareIntent, "Share With"))

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        videoView.stopPlayback()
        if (mInterstitialAd!!.isLoaded) {
            mInterstitialAd!!.show()
        }
    }

    private fun checkFolder() {

        val path: String =
            Environment.getExternalStorageDirectory().absolutePath + Constants.SAVE_FOLDER
        val directory = File(path)
        val directoryExist = directory.exists()

        if (!directoryExist) {
            directory.mkdir()
        }
    }

}