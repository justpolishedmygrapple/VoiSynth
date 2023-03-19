package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
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

class MainActivity : AppCompatActivity() {
    var mSLideViewPager: ViewPager? = null
    var mDotLayout: LinearLayout? = null
    lateinit var backbtn: Button
    lateinit var nextbtn: Button
    lateinit var skipbtn: Button
    lateinit var dots: Array<TextView?>
    var viewPagerAdapter: ViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val onBoard = prefs.getString(getString(R.string.pref_onboard_key), "Show Tutorial")

        if(onBoard == "Show Tutorial") {

            setContentView(R.layout.activity_onboarding)
            backbtn = findViewById(R.id.backbtn)
            nextbtn = findViewById(R.id.nextbtn)
            skipbtn = findViewById(R.id.skipBtn)
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
            viewPagerAdapter = ViewPagerAdapter(this)
            mSLideViewPager!!.adapter = viewPagerAdapter
            setUpindicator(0)
            mSLideViewPager!!.addOnPageChangeListener(viewListener)
        } else if (onBoard == "Tutorial Disabled") {
            val i = Intent(this@MainActivity, MainScreen::class.java)
            startActivity(i)
            finish()
        }
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
        dots[position]!!.setTextColor(resources.getColor(R.color.LightBlue, applicationContext.theme))
    }

    var viewListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            setUpindicator(position)
            if (position > 0) {
                backbtn!!.visibility = View.VISIBLE
            } else {
                backbtn!!.visibility = View.INVISIBLE
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun getitem(i: Int): Int {
        return mSLideViewPager!!.currentItem + i
    }
}