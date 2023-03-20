package com.example.myapplication.data

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter


class ViewPagerAdapter(var context: Context) : PagerAdapter() {
    var images = intArrayOf(
        com.example.myapplication.R.drawable.main_icon,
        com.example.myapplication.R.drawable.onboard_slide1,
        com.example.myapplication.R.drawable.onboarding_slide2,
        com.example.myapplication.R.drawable.onboard_slide3
    )
    var headings = intArrayOf(
        com.example.myapplication.R.string.onboard_heading1,
        com.example.myapplication.R.string.onboard_heading2,
        com.example.myapplication.R.string.onboard_heading3,
        com.example.myapplication.R.string.onboard_heading4
    )
    var description = intArrayOf(
        com.example.myapplication.R.string.onboard_desc1,
        com.example.myapplication.R.string.onboard_desc2,
        com.example.myapplication.R.string.onboard_desc3,
        com.example.myapplication.R.string.onboard_desc4
    )

    override fun getCount(): Int {
        return headings.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(com.example.myapplication.R.layout.onboarding_slider, container, false)
        val slidetitleimage = view.findViewById<View>(com.example.myapplication.R.id.titleImage) as ImageView
        val slideHeading = view.findViewById<View>(com.example.myapplication.R.id.texttitle) as TextView
        val slideDesciption = view.findViewById<View>(com.example.myapplication.R.id.textdeccription) as TextView
        slidetitleimage.setImageResource(images[position])
        slideHeading.setText(headings[position])
        slideDesciption.setText(description[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}