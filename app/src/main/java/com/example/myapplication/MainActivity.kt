package com.example.myapplication

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R.*
import com.example.myapplication.data.TextToVoiceQuery
import com.example.myapplication.data.Voice
import com.example.myapplication.data.VoiceAdapter
import com.example.myapplication.data.VoiceResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes

const val ELEVEN_LABS_API = BuildConfig.ELEVEN_LABS_API

class MainActivity : AppCompatActivity() {
    private val voiceservice = VoiceInterface.create()

    private val voiceAdapter = VoiceAdapter()


    private lateinit var voiceResultsRV: RecyclerView





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


        voiceResultsRV= findViewById(R.id.rv_voice_list)

        voiceResultsRV.layoutManager = LinearLayoutManager(this)
        voiceResultsRV.setHasFixedSize(true)


        voiceResultsRV.adapter = voiceAdapter

        queryVoices()


        val testQuery = TextToVoiceQuery("this is a test")
//
//        voiceservice.generateVoiceAudio("pAzk0hvNdRe26GUDhQ3N", generateTextQuery(
//            "Obama says this is going to be the best app ever"
//        )).enqueue(
//            object: Callback<ResponseBody>{
//                override fun onResponse(
//                    call: Call<ResponseBody>,
//                    response: Response<ResponseBody>
//                ) {
//                    Log.d("response", response.body().toString())
//                    val audioBytes = response.body()?.bytes()
//
//                    val tempMP3 = kotlin.io.path.createTempFile("audio", ".mp3")
//
//                    tempMP3.writeBytes(audioBytes ?: byteArrayOf())
//
//                    val mediaPlayer = MediaPlayer()
//
//                    mediaPlayer.setDataSource(tempMP3.pathString)
//                    mediaPlayer.prepare()
//                    mediaPlayer.start()
//
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    TODO("Not yet implemented")
//                }
//            }
//
//        )


    }

    private fun generateTextQuery(text: String): String{

        return "{\n" +
                "  \"text\": \"${text}\",\n" +
                "  \"voice_settings\": {\n" +
                "    \"stability\": 0,\n" +
                "    \"similarity_boost\": 0\n" +
                "  }\n" +
                "}"

    }

    override fun onResume() {
        super.onResume()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_voice_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_voice_list ->{
                //TODO: make a new activity to list voices
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    //Function that will be useful later to dynamically get the path to a particular audio file
    private fun urlSchemeBuilder(audioId: String): Uri{
        return Uri.parse("https://api.elevenlabs.io/v1/history/${audioId}/audio")
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