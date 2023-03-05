package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.VoiceInterface
import com.example.myapplication.data.HistoryItem
import com.example.myapplication.data.HistoryResponse
import com.example.myapplication.data.HistoryTextAdapter
import com.example.myapplication.data.Voice

const val EXTRA_SELECTED_VOICE = "SELECTED_VOICE"

const val EXTRA_HISTORY_ITEMS = "HISTORY_ITEMS"

class HistoryBySelectedVoiceActivity : AppCompatActivity() {

    private var historyResponse: HistoryResponse? = null

    private var selectedVoice: Voice? = null

    private var historyResponseSearchResults: MutableList<HistoryItem>? = null

    private val voiceService = VoiceInterface.create()

    private lateinit var historyTextAdapter: HistoryTextAdapter

    private lateinit var historyTextRV: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_by_selected_voice)

        if(intent != null && intent.hasExtra(EXTRA_HISTORY_ITEMS)){
            historyResponse = intent.getSerializableExtra(EXTRA_HISTORY_ITEMS) as HistoryResponse
//            Log.d("HistoryBySelectedVoice", historyResponse.toString())
        }

        if(intent != null && intent.hasExtra(EXTRA_SELECTED_VOICE)){
            selectedVoice = intent.getSerializableExtra(EXTRA_SELECTED_VOICE) as Voice
            Log.d("HistoryBySelectedVoice", "The selected voice name is ${selectedVoice!!.name} " +
                    "and the id is ${selectedVoice!!.voice_id}")
        }

        historyResponseSearchResults = mutableListOf<HistoryItem>()

        if(historyResponse != null){
            for(historyItem in historyResponse!!.history){
                if(historyItem.voice_id == selectedVoice!!.voice_id){
                    historyResponseSearchResults!!.add(historyItem)
                }
            }

        }

        historyTextAdapter = HistoryTextAdapter()

        historyTextAdapter.addHistoryItem(historyResponseSearchResults)

        historyTextRV = findViewById(R.id.rv_history_generated_text)

        historyTextRV.layoutManager = LinearLayoutManager(this)
        historyTextRV.setHasFixedSize(true)

        historyTextRV.adapter = historyTextAdapter
    }
}