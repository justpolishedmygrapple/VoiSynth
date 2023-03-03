package com.example.myapplication

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R.*
import com.example.myapplication.data.Voice
import com.example.myapplication.data.VoiceAdapter
import okhttp3.OkHttpClient
import org.chromium.net.CronetEngine
import org.chromium.net.UrlRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Headers
import java.util.concurrent.Executor

const val ELEVEN_LABS_API = BuildConfig.ELEVEN_LABS_API

class MainActivity : AppCompatActivity() {
    private val service = UserInterface.create()




    private val mediaPlayer = MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)





        val dummyVoices = mutableListOf<Voice>()

        val dummyVoice: Voice = Voice("1234", "Obama")
        val dummyVoice2: Voice = Voice("5678", "Darth Vader")
        val dummyVoice3: Voice = Voice("12123", "Trump")
        val dummyVoice4: Voice = Voice("1313133", "Eddie Van Halen")

        dummyVoices.add(dummyVoice)
        dummyVoices.add(dummyVoice2)
        dummyVoices.add(dummyVoice3)
        dummyVoices.add(dummyVoice4)


        val voiceRV: RecyclerView = findViewById(R.id.rv_voice_list)

        voiceRV.layoutManager = LinearLayoutManager(this)
        voiceRV.setHasFixedSize(true)

        val adapter = VoiceAdapter()
        voiceRV.adapter = adapter

        for(voice in dummyVoices){
            adapter.addVoice(voice)
        }
    }

    override fun onResume() {
        super.onResume()
        val serviceTest = service.search().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("apicall", response.body().toString())
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            }
        )

        //        Testing playing a music file streamed from the internet... works.
        //Now, how to apply headers?



        //Simply playing one single audio file in the main activity for now
        val header = HashMap<String,String>()

        header["xi-api-key"] = ELEVEN_LABS_API

        val uri = urlSchemeBuilder("YBvxnRxzT37PBA2MJqbP")


        val context = this
        mediaPlayer.setDataSource(this, uri, header)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    //Allows for the mediaPlayer to play the whole audio clip
    //Error I was getting before: 'MediaPlayer finalized without being released'
    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
        mediaPlayer.release()
    }


    //Function that will be useful later to dynamically get the path to a particular audio file
    private fun urlSchemeBuilder(audioId: String): Uri{
        return Uri.parse("https://api.elevenlabs.io/v1/history/${audioId}/audio")
    }


}