package com.freestatusaver

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

class PhotosAdapter(private val context: Context, private val list: List<StoyrModel>) :
    RecyclerView.Adapter<PhotosAdapter.Holder>() {

    private var mInterstitialAd: InterstitialAd? = null
    private val adRequest: AdRequest = AdRequest.Builder().build()

    class Holder(itemVIew: View) : RecyclerView.ViewHolder(itemVIew) {

        val statusPhoto = itemView.findViewById<ImageView>(R.id.statusPhoto)!!
        val statusDownload = itemView.findViewById<ImageView>(R.id.downLoadBtn)!!
        val statusShareBt = itemView.findViewById<ImageView>(R.id.shareBtn)!!
        val statusPlay = itemVIew.findViewById<ImageView>(R.id.statusPlay)!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false)

        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val i = "Try this awesome status saver, You can save and share all statuses even when you are offline " +
                "Download the app for free on play store" + " https://play.google.com/store/apps/details?id=com.freestatusaver"


        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd!!.adUnitId = "ca-app-pub-9047571753426746/6995255825"
        mInterstitialAd!!.loadAd(adRequest)

        val story: StoyrModel = list[position]
        if (story.uri.toString().endsWith(".mp4")) {
            holder.statusPlay.visibility = View.VISIBLE
        }
        val file = File(story.path)

        val imageUri = FileProvider.getUriForFile(
            context,
            "com.freestatusaver.fileprovider",  //(use your app signature + ".provider" )
            file
        )

        Glide.with(context).load(story.uri).into(holder.statusPhoto)

        holder.statusDownload.setOnClickListener {

            checkFolder()
            val path: String = list[position].path
            val file = File(path)

            val destinationPath = Environment.getExternalStorageDirectory().absolutePath +
                    Constants.SAVE_FOLDER
            val destFile = File(destinationPath)

            try {
                FileUtils.copyFileToDirectory(file, destFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            MediaScannerConnection.scanFile(context,
                arrayOf(destinationPath + list[position].filename),
                arrayOf("*/*"),

                object : MediaScannerConnectionClient {
                    override fun onMediaScannerConnected() {

                    }

                    override fun onScanCompleted(path: String, uri: Uri) {

                    }
                })
            Toast.makeText(context, "Saved to $destinationPath+${story.name}", Toast.LENGTH_SHORT)
                .show()

        }

        holder.statusShareBt.setOnClickListener {

            if (story.uri.toString().endsWith(".mp4")){

                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, i)
                    type = "video/*"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share With"))


            }
            else{

                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, i)
                    type = "image/*"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share With"))

            }


        }

        holder.itemView.setOnClickListener {

            if (mInterstitialAd!!.isLoaded){
                mInterstitialAd!!.show()
            }

            if (story.uri.toString().endsWith(".mp4")) {
                val i = Intent(context, VideoActivity::class.java)
                i.putExtra("photoUri", story.uri.toString())
                i.putExtra("path", story.path)
                i.putExtra("filename", story.filename)
                i.putExtra("name", story.name)
                context.startActivity(i)
            }
            else {

                val i = Intent(context, PhotoActivity::class.java)
                i.putExtra("photoUri", story.uri.toString())
                i.putExtra("path", story.path)
                i.putExtra("filename", story.filename)
                i.putExtra("name", story.name)
                context.startActivity(i)

            }
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

    override fun getItemCount(): Int {
        return list.size
    }


}