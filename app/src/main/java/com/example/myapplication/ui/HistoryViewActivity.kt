package com.example.myapplication.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import com.example.myapplication.R
import com.example.myapplication.data.*

class HistoryViewActivity : AppCompatActivity() {

    private lateinit var arrayAdapter: HistoryAdapter

    private lateinit var voiceSpinner: Spinner

    private lateinit var voiceNames: List<String>

    private lateinit var spinner: Spinner

    private lateinit var spinnerAdapter: HistoryAdapter

    private val voiceArray = mutableListOf<Voice>()


    private val voiceViewModel: ListOfVoicesViewModel by viewModels()

    private val historySearchViewModel: HistorySearchViewModel by viewModels()

    private lateinit var voiceAdapter: VoiceAdapter

    private var historyItems: HistoryResponse? = null

    private val TAG = "HistoryViewActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        queryVoices(voiceArray)
        setContentView(R.layout.activity_history_view)

        spinner = findViewById<Spinner>(R.id.spinner_history)



        val button: Button = findViewById(R.id.button_history)

        if (historySearchViewModel.historySearchResults.value == null) {
            historySearchViewModel.loadHistorySearchResults()
        }

        historySearchViewModel.historySearchResults.observe(this) { histResults ->
            if (histResults != null) {
                historyItems = histResults

                for(hist in historyItems!!.history){
                    hist.url = uriSchemeBuilder(hist.history_item_id)
                    Log.d("histurl", hist.url.toString())
                }

            }
        }







        Log.d("history", historyItems.toString())


        if (voiceViewModel.voiceListResults.value == null) {

            voiceViewModel.loadListOfVoices()

        }

        voiceViewModel.voiceListResults.observe(this) { results ->

            if (results != null) {
                for (voice in results.voices) {
                    voiceArray.add(voice)
                    Log.d("NewVoiceAdded", voice.toString())
                }
                spinner.adapter = HistoryAdapter(this@HistoryViewActivity, voiceArray)
            }
        }

        voiceNames = voiceArray.map { it.name }


        button.setOnClickListener {




            val intent =
                Intent(this@HistoryViewActivity, HistoryBySelectedVoiceActivity::class.java).apply {
                    putExtra(EXTRA_SELECTED_VOICE, spinner.selectedItem as Voice)
                    putExtra(EXTRA_HISTORY_ITEMS, historyItems)
                }

            startActivity(intent)
        }



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedVoice = p0?.getItemAtPosition(p2) as? Voice

                selectedVoice?.let {
                    Toast.makeText(
                        this@HistoryViewActivity,
                        "Selected item: ${it.voice_id}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Log.d("selection", selectedVoice!!.name)

            }


        }









    }


    override fun onStart() {
        super.onStart()


    }

    private fun uriSchemeBuilder(audioId: String): Uri {
        return Uri.parse("https://api.elevenlabs.io/v1/history/${audioId}/audio")
    }


}













