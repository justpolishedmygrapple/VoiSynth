package com.example.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Voice
import com.example.myapplication.data.VoiceAdapter
import com.example.myapplication.data.VoiceResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoiceListActivity : AppCompatActivity() {

    private val voiceservice = VoiceInterface.create()

    private val voiceAdapter = VoiceAdapter(::onVoiceItemClick)


    private lateinit var voiceResultsRV: RecyclerView

    private val voiceViewModel: ListOfVoicesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_list)

        voiceResultsRV = findViewById(R.id.rv_voice_list)

        voiceResultsRV.layoutManager = LinearLayoutManager(this)
        voiceResultsRV.setHasFixedSize(true)


        voiceResultsRV.adapter = voiceAdapter


        // Makes sure data for voice list is only called once
        if(voiceViewModel.voiceListResults.value == null){
            voiceViewModel.loadHistorySearchResults()
        }

        voiceViewModel.voiceListResults.observe(this){ results->
            voiceAdapter.addVoice(results?.voices)
        }

    }

    fun onVoiceItemClick(voice: Voice) {

        Log.d("onVoiceItemClick", "voice item clicked")
        Log.d("voiceID", voice.voice_id)

        val intent = Intent(this, VoiceGeneratorActivity::class.java).apply {
            putExtra(EXTRA_CHARACTER_VOICE, voice)
        }

        startActivity(intent)

    }
}
