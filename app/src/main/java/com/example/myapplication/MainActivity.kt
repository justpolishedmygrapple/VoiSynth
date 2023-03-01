package com.example.myapplication

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
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
        setContentView(R.layout.activity_main)

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

        val url = urlSchemeBuilder("HwVUitQTLIxLzWQTCkhA")

        val uri = Uri.parse(url)



        val context = this
        mediaPlayer.setDataSource(this, Uri.parse(url), header)
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
    private fun urlSchemeBuilder(audioId: String): String{
        return "https://api.elevenlabs.io/v1/history/${audioId}/audio"
    }


}