package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.example.myapplication.R
import com.example.myapplication.VoiceInterface
import com.example.myapplication.data.HistoryAdapter
import com.example.myapplication.data.Voice
import com.example.myapplication.data.VoiceResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryView : AppCompatActivity() {

    private lateinit var arrayAdapter: HistoryAdapter

    private lateinit var voiceSpinner: Spinner

    private lateinit var voiceNames: List<String>

    private lateinit var spinner: Spinner

    private lateinit var spinnerAdapter: HistoryAdapter

    private val voiceArray = mutableListOf<Voice>()

    val voiceservice = VoiceInterface.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        queryVoices(voiceArray)
        setContentView(R.layout.activity_history_view)

        spinner = findViewById<Spinner>(R.id.spinner_history)

        queryVoices(voiceArray)




        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedVoice = p0?.getItemAtPosition(p2) as? Voice

                selectedVoice?.let {
                    Toast.makeText(this@HistoryView, "Selected item: ${it.voice_id}", Toast.LENGTH_SHORT).show()
                }

                Log.d("selection", selectedVoice!!.name)

            }
        }

    }

    override fun onStart() {
        super.onStart()

//        if(voiceArray.size == 0){
//
//            queryVoices(voiceArray)
//
//        }

        Log.d("hello", "hello")


    }





    private fun queryVoices(voiceList: List<Voice>){
        val serviceTest = voiceservice.getVoices().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("apicall", response.body().toString())

                    if(response.isSuccessful){
                        val moshi = Moshi.Builder().build()

                        val jsonAdapter: JsonAdapter<VoiceResponse> =
                            moshi.adapter(VoiceResponse::class.java)

                        val voiceSearchResults = jsonAdapter.fromJson(response.body())

                        for(i in voiceSearchResults!!.voices){
                            voiceArray.add(0,i)
                        }

                        voiceNames = voiceArray.map { it.name }

                        Log.d("help", voiceSearchResults.toString())

                        spinner.adapter = HistoryAdapter(this@HistoryView, voiceArray)



                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("Error", "Error making API call: ${t.message}")
                }
            })
    }



}