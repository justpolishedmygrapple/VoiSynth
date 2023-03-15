package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R.*
import com.example.myapplication.data.TextToVoiceQuery
import com.example.myapplication.data.Voice
import com.example.myapplication.data.VoiceAdapter
import com.example.myapplication.data.VoiceResponse
import com.example.myapplication.ui.HistoryViewActivity
import com.example.myapplication.ui.VoiceInterface
import com.example.myapplication.ui.VoiceListActivity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.io.path.writeBytes

const val ELEVEN_LABS_API = BuildConfig.ELEVEN_LABS_API


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
    }

}