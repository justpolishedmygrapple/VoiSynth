package com.example.myapplication.ui

import android.content.res.Configuration
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.R.*
import com.example.myapplication.data.Voice
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes


const val EXTRA_CHARACTER_VOICE = "CHARACTER_VOICE"
class VoiceGeneratorActivity : AppCompatActivity() {

    private val voiceservice = VoiceInterface.create()

    private var selectedVoice: Voice? = null

    private var userGeneratedText: String? = null

    private lateinit var mediaPlayer: MediaPlayer

    //    private val GenerateButton: Button = findViewById<Button>(R.id.button_generate_text)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_voice_generator)

        mediaPlayer = MediaPlayer()

        if (savedInstanceState != null){
            val position = savedInstanceState.getInt("position")
            mediaPlayer.seekTo(position)
        }




        if(intent != null && intent.hasExtra(EXTRA_CHARACTER_VOICE)){
            selectedVoice = intent.getSerializableExtra(EXTRA_CHARACTER_VOICE) as Voice
            findViewById<TextView>(R.id.tv_selected_voice).text = getString(R.string.voice_name,
                selectedVoice!!.name)

            val editText = findViewById<EditText>(R.id.edit_generated_text)

            editText.setHint(getString(R.string.generate_hint, selectedVoice!!.name))
        }

        val generateButton = findViewById<Button>(R.id.button_generate_text)

        generateButton.setOnClickListener {
            onGenerateButtonClick(generateButton)

        }


    }

    private fun onGenerateButtonClick(generateButton: Button) {
        Log.d("buttonClick", "User entered: ${findViewById<EditText>(R.id.edit_generated_text).text.toString()}")

        val userRequestedText: String = findViewById<EditText>(R.id.edit_generated_text).text.toString()



        if(userRequestedText == "") {

            Log.d("userText", "user text is null")
            Snackbar.make(
                this.findViewById(android.R.id.content),
                "You have to enter some text in order to generate audio",
                Snackbar.LENGTH_LONG
            ).show()
        }

//        if(mediaPlayer.isPlaying){
//            Snackbar.make(
//                this.findViewById(android.R.id.content),
//                "Please wait until audio is done playing before generating a new request",
//                Snackbar.LENGTH_LONG
//            ).show()
//        }



        //Fixed critical bug
        //Not checking that userRequestedText == "" before would send an API call anyway with
        // An empty string and generate garbage audio.
        if((userRequestedText != "")){




            voiceservice.generateVoiceAudio(selectedVoice!!.voice_id,
                generateJsonRequestBody(userRequestedText)).enqueue(
                object: Callback<ResponseBody>{
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {

                        if(response.isSuccessful){
                            Log.d("response", response.body().toString())
                        }
                        else{
                            Log.d("response failure", response.body().toString())
                        }
                        val audioBytes = response.body()?.bytes()
                        val tempMP3 = kotlin.io.path.createTempFile("audio", ".mp3")
                        tempMP3.writeBytes(audioBytes ?: byteArrayOf())

                        mediaPlayer.setDataSource(tempMP3.pathString)
                        mediaPlayer.prepare()
                        mediaPlayer.start()

                        mediaPlayer.setOnCompletionListener{
                            mediaPlayer.release()
                        }

                        mediaPlayer.setOnErrorListener{mp, what, extra ->
                            mediaPlayer.release()
                            false
                        }

//                        if(!mediaPlayer.isPlaying){
//                            mediaPlayer.release()
//                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("onfailure", t.message.toString())
                    }
                }
            )}
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }


    //audio stops playing when user leaves the screen
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }


    //Allows for media to keep playing during rotates.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("position", mediaPlayer.currentPosition)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }



    //The API slightly changed how their POST requests work. Broke everything in the app.
    //This method should be more stable.
    private fun generateJsonRequestBody(text: String): RequestBody {

        val requestJSONObject = JSONObject()

        val voiceSettings = JSONObject()

        requestJSONObject.put("text", text)

        voiceSettings.put("stability", "0.75")
        voiceSettings.put("similarity_boost", "0.75")

        requestJSONObject.put("voice_settings", voiceSettings)

        return requestJSONObject.toString().toRequestBody("application/json".toMediaTypeOrNull())


    }


}