package com.example.myapplication.ui

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.BuildConfig.ELEVEN_LABS_API
import com.example.myapplication.R
import com.example.myapplication.data.HistoryItem
import com.example.myapplication.data.HistoryResponse
import com.example.myapplication.data.HistoryTextAdapter
import com.example.myapplication.data.Voice
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes

const val EXTRA_SELECTED_VOICE = "SELECTED_VOICE"

const val EXTRA_HISTORY_ITEMS = "HISTORY_ITEMS"

class HistoryBySelectedVoiceActivity : AppCompatActivity() {

    private var historyResponse: HistoryResponse? = null

    private var selectedVoice: Voice? = null

    private var historyResponseSearchResults: MutableList<HistoryItem>? = null

    private val voiceService = VoiceInterface.create()

    private lateinit var historyTextAdapter: HistoryTextAdapter

    private lateinit var historyTextRV: RecyclerView

    private lateinit var voiceName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_by_selected_voice)

        if (intent != null && intent.hasExtra(EXTRA_HISTORY_ITEMS)) {
            historyResponse = intent.getSerializableExtra(EXTRA_HISTORY_ITEMS) as HistoryResponse
//            Log.d("HistoryBySelectedVoice", historyResponse.toString())
        }

        if (intent != null && intent.hasExtra(EXTRA_SELECTED_VOICE)) {
            selectedVoice = intent.getSerializableExtra(EXTRA_SELECTED_VOICE) as Voice
            voiceName = selectedVoice!!.name
            Log.d(
                "HistoryBySelectedVoice", "The selected voice name is ${selectedVoice!!.name} " +
                        "and the id is ${selectedVoice!!.voice_id}"
            )
        }

        historyResponseSearchResults = mutableListOf<HistoryItem>()

        if (historyResponse != null) {
            for (historyItem in historyResponse!!.history) {
                if (historyItem.voice_id == selectedVoice!!.voice_id) {
                    historyResponseSearchResults!!.add(historyItem)
                }
            }

        }


        historyTextAdapter = HistoryTextAdapter(::onHistoryItemClick, ::onHistoryItemLongClick)

        historyTextAdapter.addHistoryItem(historyResponseSearchResults)

        historyTextRV = findViewById(R.id.rv_history_generated_text)

        historyTextRV.layoutManager = LinearLayoutManager(this)
        historyTextRV.setHasFixedSize(true)

        historyTextRV.adapter = historyTextAdapter
    }

    private fun onHistoryItemClick(historyItem: HistoryItem) {
        Log.d("itempress", "Item ID is: ${historyItem.history_item_id}")

        val mediaPlayer = MediaPlayer()

        val uri = uriSchemeBuilder(historyItem.history_item_id)

        val header = HashMap<String, String>()

        header["xi-api-key"] = ELEVEN_LABS_API

        //TODO: possibly make mediaplayer global
        //TODO: handle playing and stopping of mediaplayer resource gracefully as was implemented in the voicegenerator activity
        mediaPlayer.setDataSource(this, uri, header)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    private fun onHistoryItemLongClick(historyItem: HistoryItem) {

//        val vibrator = parent.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        Log.d(
            "longpress",
            "https://api.elevenlabs.io/v1/history/${historyItem.history_item_id}/audio"
        )

        GlobalScope.launch {
            shareMP3(historyItem)
        }


//            val file = File(this.cacheDir, "${historyItem.history_item_id}.mp3")

//            FileOutputStream(file).use { fileOutputStream ->
//                fileOutputStream.write(bytes)
//            }
    }

//            val fileUri = FileProvider.getUriForFile(this@HistoryBySelectedVoiceActivity,
//                "${this@HistoryBySelectedVoiceActivity.packageName}.fileprovider",
//            file)
//
//            ShareCompat.IntentBuilder(this@HistoryBySelectedVoiceActivity)
//                .setType("audio/mpeg")
//                .setText("Check this out!")
//                .setText("${historyItem.text}")
//                .addStream(fileUri)
//                .startChooser()
//
//        }



    //Functionality to long press on a history item and share the MP3

    //TODO: LET THE USER KNOW AT THE TOP THAT LONG-PRESSING ON AN ITEM WILL SHARE IT
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun shareMP3(historyItem: HistoryItem) {
        val client = OkHttpClient()
//
        val request = Request.Builder()
            .url("https://api.elevenlabs.io/v1/history/${historyItem.history_item_id}/audio")
            .header("xi-api-key", ELEVEN_LABS_API)
            .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("$response")

                    val bytes = response.body?.bytes()

                    val tmpMP3 = kotlin.io.path.createTempFile("$voiceName", ".mp3")

                    val file = File(tmpMP3.pathString)






                    tmpMP3.writeBytes(bytes!!)

                    val fileUri = FileProvider.getUriForFile(
                        this@HistoryBySelectedVoiceActivity,
                        "${this@HistoryBySelectedVoiceActivity.packageName}.fileprovider",
                        file
                    )

                    ShareCompat.IntentBuilder(this@HistoryBySelectedVoiceActivity)
                        .setType("audio/mpeg")
                        .setSubject("Check out what $voiceName just said!")
                        .setText("$voiceName just said:\t\t\t\n\n\n\t ${historyItem.text}")
                        .addStream(fileUri)
                        .startChooser()
                }
            } catch(e: Exception){
                Snackbar.make(
                    this.findViewById(android.R.id.content),
                    e.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }

    private fun uriSchemeBuilder(audioId: String): Uri {
        return Uri.parse("https://api.elevenlabs.io/v1/history/${audioId}/audio")
    }
}

