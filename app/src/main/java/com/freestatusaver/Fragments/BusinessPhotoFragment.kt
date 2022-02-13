package com.freestatusaver.Fragments

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.freestatusaver.Constants
import com.freestatusaver.PhotosAdapter
import com.freestatusaver.R
import com.freestatusaver.StoyrModel
import java.io.File

class BusinessPhotoFragment : Fragment() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var files: Array<File>
    private var list: ArrayList<StoyrModel> = ArrayList()
    private lateinit var adapter: PhotosAdapter

    private lateinit var targetPath : String
    private lateinit var directoryFile : File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_business_photo, container, false)

        recyclerView = view.findViewById(R.id.photosRecycler)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        setUpRefreshLayout()

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            setUpRefreshLayout()

            Handler().postDelayed(Runnable {
                swipeRefresh.isRefreshing = false
                Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show()
            }, 2000)
        }


        return view

    }

    private fun setUpRefreshLayout() {

        list.clear()
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        adapter = PhotosAdapter(context!!, getData())
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()



    }

    private fun getData(): List<StoyrModel> {

        val android10 = Environment.getExternalStorageDirectory()
            .absolutePath + Constants.ANDROID10 + "media/com.whatsapp.w4b/WhatsApp/Media/.Statuses"

        var model: StoyrModel

        val targetPath : String = Environment.getExternalStorageDirectory()
            .absolutePath + Constants.BUSINESS_FOLDER_NAME + "Media/.Statuses"

        /*directoryFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ){

            File(android10)

        } else {
            File(targetPath)
        }

         */

        try {
            directoryFile = File(targetPath)
            files = directoryFile.listFiles()!!
            if ((files.size) > 0) {

                for (i in files.indices) {

                    val file = files[i]
                    model = StoyrModel()
                    model.uri = Uri.fromFile(file)
                    model.path = file.absolutePath
                    model.filename = file.name

                    if (!model.uri.toString().endsWith(".nomedia")
                        && (model.uri.toString().endsWith(".jpg") || model.uri.toString().endsWith(".png"))){
                            list.add(model)

                    }
                }

            }
        }
        catch (e:NullPointerException){
            Toast.makeText(context, "WhatsApp Business Folder in root directory is empty", Toast.LENGTH_SHORT).show()
        }
        catch (e:Exception){
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        }





        return list
    }

}