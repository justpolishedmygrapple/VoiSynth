package com.example.myapplication.ui

import android.content.Intent
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

//        queryVoices(voiceArray)

        val button: Button = findViewById(R.id.button_history)

        if (historySearchViewModel.historySearchResults.value == null) {
            historySearchViewModel.loadHistorySearchResults()
        }

        historySearchViewModel.historySearchResults.observe(this) { histResults ->
            if (histResults != null) {
                historyItems = histResults

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


}












//    private fun queryVoices(voiceList: List<Voice>){
//        val serviceTest = voiceservice.getVoices().enqueue(
//            object : Callback<String> {
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    Log.d("apicall", response.body().toString())
//
//                    if(response.isSuccessful){
//                        val moshi = Moshi.Builder().build()
//
//                        val jsonAdapter: JsonAdapter<VoiceResponse> =
//                            moshi.adapter(VoiceResponse::class.java)
//
//                        val voiceSearchResults = jsonAdapter.fromJson(response.body())
//
//                        for(i in voiceSearchResults!!.voices){
//                            voiceArray.add(0,i)
//                        }
//
//                        voiceNames = voiceArray.map { it.name }
//
//                        Log.d("help", voiceSearchResults.toString())
//
//                        spinner.adapter = HistoryAdapter(this@HistoryViewActivity, voiceArray)
//
//
//
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d("Error", "Error making API call: ${t.message}")
//                }
//            })
//    }



