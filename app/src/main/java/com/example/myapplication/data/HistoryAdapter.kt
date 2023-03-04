package com.example.myapplication.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HistoryAdapter(context: Context, objects: List<Voice>):
    ArrayAdapter<Voice>(context, android.R.layout.simple_spinner_dropdown_item, objects){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false)

        val item = getItem(position)

        val textView = view.findViewById<TextView>(android.R.id.text1)

        textView.text = item?.name

        return view


    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        val item = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = item?.name
        return view

    }




    }