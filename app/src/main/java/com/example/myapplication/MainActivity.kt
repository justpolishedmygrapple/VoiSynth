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
//    private val voiceservice = VoiceInterface.create()
//
////    private val voiceAdapter = VoiceAdapter()
//

    //
//    private lateinit var voiceResponse: VoiceResponse
//    private lateinit var voiceResultsRV: RecyclerView
//
//
//
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
    }

//    val voiceservice = VoiceInterface.create()
//
//    voiceservice.generateVoiceAudio("AZnzlk1XvdvUeBnXmlld", generateTextQuery("blah blah blah")).enqueue(
//
//
//        object: Callback<ResponseBody>{
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: Response<ResponseBody>
//            ) {
//
//                if(!response.isSuccessful){
//                    Log.d("VoiceGeneratorActy", "response failure")
//                }
//                val audioBytes = response.body()?.bytes()
//                val tempMP3 = kotlin.io.path.createTempFile("audio", ".mp3")
//                tempMP3.writeBytes(audioBytes ?: byteArrayOf())
//
////                    mediaPlayer.setDataSource(tempMP3.pathString)
////                    mediaPlayer.prepare()
////                    mediaPlayer.start()
//
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.d("onfailure", t.message.toString())
//            }
//        }
//    )}


//
//        //Creating dir for app voice file data
//        val audioFolder = filesDir
//        val folder = File(audioFolder, "ai_voice")
//        folder.mkdir()
//
//
//
//        voiceResultsRV = findViewById(R.id.rv_voice_list)
//
//        voiceResultsRV.layoutManager = LinearLayoutManager(this)
//        voiceResultsRV.setHasFixedSize(true)
//
//
//        voiceResultsRV.adapter = voiceAdapter
//
//        queryVoices()


//        val testQuery = TextToVoiceQuery("this is a test")
////
//        voiceservice.generateVoiceAudio("pAzk0hvNdRe26GUDhQ3N", generateTextQuery(
//            "Obama says this is going to be the best app ever"
////        )).enqueue(/
//            object: Callback<ResponseBody>{
//                override fun onResponse(
//                    call: Call<ResponseBody>,
//                    response: Response<ResponseBody>
//                ) {
////                    Log.d("response", response.body().toString())/
//                    val audioBytes = response.body()?.bytes()
////
//                    val tempMP3 = kotlin.io.path.createTempFile("audio", ".mp3")
////
//                    tempMP3.writeBytes(audioBytes ?: byteArrayOf())
////
//                    val mediaPlayer = MediaPlayer()
////
//                    mediaPlayer.setDataSource(tempMP3.pathString)
//                    mediaPlayer.prepare()
//                    mediaPlayer.start()
////
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    TODO("Not yet implemented")
//                }
//            }
////
//        )
//
//
//    }


//        override fun onResume() {
//            super.onResume()
//        }

//    override fun onResume() {
//        super.onResume()
//        val serviceTest = service.getVoices().enqueue(
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
//                        Log.d("help", voiceSearchResults.toString())
//
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d("Error", "Error making API call: ${t.message}")
//                }
//            }
//        )

        //        Testing playing a music file streamed from the internet... works.
        //Now, how to apply headers?


        //Simply playing one single audio file in the main activity for now
//        val header = HashMap<String,String>()
//
//        header["xi-api-key"] = ELEVEN_LABS_API
//
//        val uri = urlSchemeBuilder("YBvxnRxzT37PBA2MJqbP")
//
//
//        val context = this
//        mediaPlayer.setDataSource(this, uri, header)
//        mediaPlayer.prepare()
//        mediaPlayer.start()
//    }

        //Allows for the mediaPlayer to play the whole audio clip
        //Error I was getting before: 'MediaPlayer finalized without being released'
//    override fun onStop() {
//        super.onStop()
//        mediaPlayer.stop()
//        mediaPlayer.release()
//    }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.activity_history_list, menu)
            menuInflater.inflate(R.menu.activity_voice_list, menu)
            return true
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_voice_list -> {
                val intent = Intent(this, VoiceListActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_show_history -> {
                val intent = Intent(this, HistoryViewActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    }



    //Function that will be useful later to dynamically get the path to a particular audio file
//    private fun urlSchemeBuilder(audioId: String): Uri{
//        return Uri.parse("https://api.elevenlabs.io/v1/history/${audioId}/audio")
//    }

//    private fun queryVoices(){
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
//                        voiceResponse = voiceSearchResults!!
//
//                        for(i in voiceResponse.voices){
//                            Log.d("voices", i.name)
//                        }
//
//                        Log.d("help", voiceSearchResults.toString())
//
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d("Error", "Error making API call: ${t.message}")
//                }
//            })
//    }
//
//
//}