package com.example.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.VoiceInterface
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_list)

        voiceResultsRV = findViewById(R.id.rv_voice_list)

        voiceResultsRV.layoutManager = LinearLayoutManager(this)
        voiceResultsRV.setHasFixedSize(true)


        voiceResultsRV.adapter = voiceAdapter


        queryVoices()


    }

    fun onVoiceItemClick(voice: Voice){

        Log.d("onVoiceItemClick", "voice item clicked")
        Log.d("voiceID", voice.voice_id)

        val intent = Intent(this, VoiceGeneratorActivity::class.java).apply {
            putExtra(EXTRA_CHARACTER_VOICE, voice)
        }

        startActivity(intent)

    }

    private fun queryVoices(){
        val serviceTest = voiceservice.getVoices().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("apicall", response.body().toString())

                    if(response.isSuccessful){
                        val moshi = Moshi.Builder().build()

                        val jsonAdapter: JsonAdapter<VoiceResponse> =
                            moshi.adapter(VoiceResponse::class.java)

                        val voiceSearchResults = jsonAdapter.fromJson(response.body())


                        voiceAdapter.addVoice(voiceSearchResults?.voices)

                        Log.d("help", voiceSearchResults.toString())

                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("Error", "Error making API call: ${t.message}")
                }
            })
    }
}