package com.freestatusaver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.freestatusaver.Fragments.BusinessPhotoFragment
import com.freestatusaver.Fragments.BusinessVideoFragment
import com.freestatusaver.Fragments.PhotosFragment
import com.freestatusaver.Fragments.VideosFragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class WhatsAppBusinessActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var mAdView: AdView? = null
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adRequest: AdRequest
    var mViewPager: ViewPager? = null
    private var pagerAdapter: MainActivity.PagerAdapter? = null
    private var tabLayout: TabLayout? = null


    private fun setUpViewPager(){


        pagerAdapter!!.addFragment(BusinessPhotoFragment(), "Photos")
        pagerAdapter!!.addFragment(BusinessVideoFragment(), "Videos")

        mViewPager!!.adapter = pagerAdapter
        tabLayout!!.setupWithViewPager(mViewPager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whats_app_business)

        pagerAdapter = MainActivity.PagerAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.chatViewPager)
        tabLayout = findViewById(R.id.chat_tab)
        bottomNav = findViewById(R.id.bottomNavigation)

        //initializing ads
        adRequest = AdRequest.Builder().build()
        mAdView = findViewById(R.id.adView)
        mAdView!!.loadAd(adRequest)

        supportActionBar!!.title = "WhatsApp Business"
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

        setUpViewPager()
    }

    private fun showAd() {
        if (mInterstitialAd!!.isLoaded) mInterstitialAd!!.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showAd()
    }
}