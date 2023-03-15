package com.example.myapplication.ui

import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.example.myapplication.R
import com.example.myapplication.R.*
import com.example.myapplication.data.Voice
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream
import java.net.URLConnection
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes


const val EXTRA_CHARACTER_VOICE = "CHARACTER_VOICE"
class VoiceGeneratorActivity : AppCompatActivity() {

    private val voiceservice = VoiceInterface.create()

    private var selectedVoice: Voice? = null

    private var userGeneratedText: String? = null

    private lateinit var mediaPlayer: MediaPlayer

    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_voice_generator)

        mediaPlayer = MediaPlayer()

        if (savedInstanceState != null) {
            val position = savedInstanceState.getInt("position")
            mediaPlayer.seekTo(position)
        }




        if (intent != null && intent.hasExtra(EXTRA_CHARACTER_VOICE)) {
            selectedVoice = intent.getSerializableExtra(EXTRA_CHARACTER_VOICE) as Voice
            findViewById<TextView>(R.id.tv_selected_voice).text = getString(
                R.string.voice_name,
                selectedVoice!!.name
            )

            val editText = findViewById<EditText>(R.id.edit_generated_text)

            editText.setHint(getString(R.string.generate_hint, selectedVoice!!.name))
        }

        val generateButton = findViewById<Button>(R.id.button_generate_text)

        generateButton.setOnClickListener {
            onGenerateButtonClick()
        }


    }

    private fun onGenerateButtonClick() {
        Log.d(
            "buttonClick",
            "User entered: ${findViewById<EditText>(R.id.edit_generated_text).text.toString()}"
        )

        val userRequestedText: String =
            findViewById<EditText>(R.id.edit_generated_text).text.toString()



        if (userRequestedText == "") {

            Log.d("userText", "user text is null")
            Snackbar.make(
                this.findViewById(android.R.id.content),
                "You have to enter some text in order to generate audio",
                Snackbar.LENGTH_LONG
            ).show()
        }


        //Fixed critical bug
        //Not checking that userRequestedText == "" before would send an API call anyway with
        // An empty string and generate garbage audio.

        //AHA! resetting mediaPlayer to a new mediaplayer instance fixes the bug where you can't
        //generate more than one audio file per character per screen. Super annoying.


        if ((userRequestedText != "")) {
            GlobalScope.launch {

                //Wrapping this in a try block fixed error where app would crash if disconnected
                // from internet
                try {
                    val audioFile = getAudio(
                        selectedVoice!!.voice_id,
                        generateJsonRequestBody(userRequestedText)
                    )!!

                    if (audioFile!!.isNotEmpty()) {
                        val tmpMP3 = kotlin.io.path.createTempFile("audio", ".mp3")
                        tmpMP3.writeBytes(audioFile ?: byteArrayOf())



                        filePath = tmpMP3.pathString

                        mediaPlayer.setDataSource(tmpMP3.pathString)

                        mediaPlayer.prepare()
                        mediaPlayer.start()

                        mediaPlayer.setOnCompletionListener {
                            mediaPlayer.release()
                            mediaPlayer = MediaPlayer()
                        }



                        mediaPlayer.setOnErrorListener { mp, what, extra ->
                            mediaPlayer.release()
                            mediaPlayer = MediaPlayer()
                            false
                        }

                    }


                } catch (e: Exception) {

                    Log.d("exception", e.toString())

                    Snackbar.make(
                        findViewById(android.R.id.content),
                        e.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }




        }
    }


    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_voice_generate, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.share_file -> {
                Log.d("shareclick", "shareclick")



                if(filePath != null){
                    shareFileIntent(filePath!!, selectedVoice!!.name)
                } else{
                    Snackbar.make(
                        this.findViewById(android.R.id.content),
                        "You either haven't generated a file yet, or there was an error",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                true
            }
            else ->{
                super.onOptionsItemSelected(item)
            }
        }
    }




    //audio stops playing when user leaves the screen
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

// Code is irrelevant because of coroutines

//    Allows for media to keep playing during rotates.
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        if(mediaPlayer.isPlaying){
//            outState.putInt("position", mediaPlayer.currentPosition)
//        }
//    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    suspend fun getAudio(voice_id: String, requestBody: RequestBody): ByteArray? {

        val response = voiceservice.generateVoiceAudio(voice_id, requestBody).apply {
            delay(500)
        }

        return if(response.isSuccessful){
            response.body()?.bytes()}
        else{
            null}

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


    //Source I went to for help when things were going poorly with permissions -
    // https://tinyurl.com/yfe6b2h5
    // Android docs led me to ShareCompat... was way easier than doing it the way I was doing it before
    private fun shareFileIntent(filePath: String, character: String){

        val shareIntent = Intent(Intent.ACTION_SEND)

        shareIntent.type = URLConnection.guessContentTypeFromName(filePath)

        val userRequestedText: String =
            findViewById<EditText>(R.id.edit_generated_text).text.toString()

        val file = File(filePath)

        val fileUri = FileProvider.getUriForFile(this@VoiceGeneratorActivity, "${this@VoiceGeneratorActivity.packageName}.fileprovider", file)



        ShareCompat.IntentBuilder(this@VoiceGeneratorActivity)
            .setType("audio/mpeg")
            .setSubject("Check out what $character just said!")
            .setText("$character just said:\t\t\t\n\n\n\t $userRequestedText")
            .addStream(fileUri)
            .startChooser()
    }


}