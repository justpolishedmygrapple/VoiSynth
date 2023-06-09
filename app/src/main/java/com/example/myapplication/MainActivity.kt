package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

import androidx.preference.ListPreference
import androidx.preference.Preference

import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

import com.example.myapplication.data.ViewPagerAdapter
import com.example.myapplication.R
import java.util.*

class MainActivity : AppCompatActivity() {
    var mSLideViewPager: ViewPager? = null
    var mDotLayout: LinearLayout? = null
    lateinit var backbtn: Button
    lateinit var nextbtn: Button
    lateinit var skipbtn: Button
    lateinit var backbtnbg: ImageView
    lateinit var dots: Array<TextView?>
    var viewPagerAdapter: ViewPagerAdapter? = null

    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val onBoard = prefs.getString(getString(R.string.pref_onboard_key), "Show Tutorial")

        val lang = prefs.getString(getString(R.string.pref_language_key), "English") ?: "English"

        changeLang(lang)

        if(onBoard == "Show Tutorial" || onBoard == "Ja") {

            setContentView(R.layout.activity_onboarding)
            backbtn = findViewById(R.id.backbtn)
            nextbtn = findViewById(R.id.nextbtn)
            skipbtn = findViewById(R.id.skipBtn)
            backbtnbg = findViewById(R.id.ellipse1)
            backbtn.setOnClickListener(View.OnClickListener {
                if (getitem(0) > 0) {
                    mSLideViewPager!!.setCurrentItem(getitem(-1), true)
                }
            })
            nextbtn.setOnClickListener(View.OnClickListener {
                if (getitem(0) < 3) mSLideViewPager!!.setCurrentItem(getitem(1), true) else {
                    with(prefs.edit()) {
                        putString(getString(R.string.pref_onboard_key), getString(R.string.pref_onboard_off))
                        apply()
                    }
                    val i = Intent(this@MainActivity, MainScreen::class.java)
                    startActivity(i)
                    finish()
                }
            })
            skipbtn.setOnClickListener(View.OnClickListener {
                with(prefs.edit()) {
                    putString(getString(R.string.pref_onboard_key), getString(R.string.pref_onboard_off))
                    apply()
                }
                val i = Intent(this@MainActivity, MainScreen::class.java)
                startActivity(i)
                finish()
            })
            mSLideViewPager = findViewById<View>(R.id.slideViewPager) as ViewPager
            mDotLayout = findViewById<View>(R.id.indicator_layout) as LinearLayout
            viewPagerAdapter = ViewPagerAdapter(this, lang)
            mSLideViewPager!!.adapter = viewPagerAdapter
            setUpindicator(0)
            backbtn!!.visibility = View.INVISIBLE
            backbtnbg!!.visibility = View.INVISIBLE
            mSLideViewPager!!.addOnPageChangeListener(viewListener)
        } else if (onBoard == "Tutorial Disabled" || onBoard == "Nej") {
            val i = Intent(this@MainActivity, MainScreen::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun changeLang(lang: String){
        val resources = this.resources
        val configuration = resources.configuration
        if(lang == "English"){
            configuration.setLocale(Locale("en", "US"))
        }
        else{
            configuration.setLocale(Locale("sv", "SE"))
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun setUpindicator(position: Int) {
        dots = arrayOfNulls(4)
        mDotLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(resources.getColor(R.color.DarkGray, applicationContext.theme))
            mDotLayout!!.addView(dots[i])
        }
        dots[position]!!.setTextColor(resources.getColor(R.color.Purple, applicationContext.theme))
    }

    var viewListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            setUpindicator(position)
            if (position > 0) {
                backbtn!!.visibility = View.VISIBLE
                backbtnbg!!.visibility = View.VISIBLE
            } else {
                backbtn!!.visibility = View.INVISIBLE
                backbtnbg!!.visibility = View.INVISIBLE
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun getitem(i: Int): Int {
        return mSLideViewPager!!.currentItem + i
    }

}