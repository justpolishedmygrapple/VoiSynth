package com.example.myapplication.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.R
import com.example.myapplication.R.*
import com.example.myapplication.VoiceInterface
import com.example.myapplication.data.Voice
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes


const val EXTRA_CHARACTER_VOICE = "CHARACTER_VOICE"
class VoiceGeneratorActivity : AppCompatActivity() {

    private val voiceservice = VoiceInterface.create()

    private var selectedVoice: Voice? = null

    private var userGeneratedText: String? = null

//    private val GenerateButton: Button = findViewById<Button>(R.id.button_generate_text)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_voice_generator)


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

        else{
        voiceservice.generateVoiceAudio(selectedVoice!!.voice_id,
        generateTextQuery(userRequestedText)).enqueue(
            object: Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val audioBytes = response.body()?.bytes()
                    val tempMP3 = kotlin.io.path.createTempFile("audio", ".mp3")
                    tempMP3.writeBytes(audioBytes ?: byteArrayOf())
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(tempMP3.pathString)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            }
        )}
    }

    private fun generateTextQuery(text: String): String{

        return "{\n" +
                "  \"text\": \"${text}\",\n" +
                "  \"voice_settings\": {\n" +
                "    \"stability\": 0.75,\n" +
                "    \"similarity_boost\": 0.75\n" +
                "  }\n" +
                "}"

    }


}