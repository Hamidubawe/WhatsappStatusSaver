package com.freestatusaver

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.freestatusaver.Fragments.PhotosFragment
import com.freestatusaver.Fragments.VideosFragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private var mAdView: AdView? = null
    private var mInterstitialAd: InterstitialAd? = null
    private val adRequest: AdRequest = AdRequest.Builder().build()
    var mViewPager: ViewPager? = null
    private var pagerAdapter: PagerAdapter? = null
    private var tabLayout: TabLayout? = null
    val REQUEST_CODE = 1
    private val TIME_INTERVAL = 2000
    private var mBackPressed: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bottomNavigation)

        pagerAdapter = PagerAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.chatViewPager)
        tabLayout = findViewById(R.id.chat_tab)

        //initializing ads
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

            override fun onAdLoaded() {
                super.onAdLoaded()
                mInterstitialAd!!.show()
            }

        }
        if (askForPermissions()){
            setUpViewPager()
        }
        /*bottomNav.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {


                R.id.photos -> {
                    PhotosFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, PhotosFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                }

                R.id.videos -> {
                    VideosFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, VideosFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                }
            }
            true

        }*/

       /* Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {


            override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {

                /*supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, PhotosFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

                 */
                setUpViewPager()


            }

            override fun onPermissionRationaleShouldBeShown(
                list: List<PermissionRequest>,
                permissionToken: PermissionToken
            ) {

                permissionToken.continuePermissionRequest()

            }
        }).check()*/

    }

    override fun onBackPressed() {

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis() || fragmentManager.backStackEntryCount != 0) {
            super.onBackPressed()
            return
        } else {
            Toast.makeText(baseContext, "Tap back button in order to exit", Toast.LENGTH_SHORT)
                .show()
        }
        mBackPressed = System.currentTimeMillis()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        return true


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.share -> {

                if (mInterstitialAd!!.isLoaded) {
                    showAd()
                    mInterstitialAd!!.loadAd(adRequest)
                }

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Hello try this awesome status saver, You can save all statuses even when you are offline " +
                                "Download the app for free on play store https://play.google.com/store/apps/details?id=com.freestatusaver"
                    )
                    type = "text/plain"
                }

                val sharedIntent = Intent.createChooser(intent, null)
                startActivity(sharedIntent)
            }

            R.id.whatsappBusiness -> {
                val i = Intent(this, WhatsAppBusinessActivity::class.java)
                startActivity(i)
            }
        }

        return true


    }

    private fun showAd() {

        if (mInterstitialAd!!.isLoaded) mInterstitialAd!!.show()
    }

    class PagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {

        private val fragments: ArrayList<Fragment> = ArrayList()
        private val titles: ArrayList<String> = ArrayList()


        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String){
            fragments.add(fragment)
            titles.add(title)

        }

        override fun getPageTitle(postion: Int): CharSequence? {
            return titles[postion]
        }
    }

    private fun setUpViewPager(){

        pagerAdapter!!.addFragment(PhotosFragment(), "Photos")
        pagerAdapter!!.addFragment(VideosFragment(), "Videos")

        mViewPager!!.adapter = pagerAdapter
        tabLayout!!.setupWithViewPager(mViewPager)
    }

    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            arrayOf(Manifest.permission_group()).toString()
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission_group().toString()
                )
                ) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE
                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                    setUpViewPager()
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings",
                DialogInterface.OnClickListener { _, _ ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel", null)
            .show()
    }
}