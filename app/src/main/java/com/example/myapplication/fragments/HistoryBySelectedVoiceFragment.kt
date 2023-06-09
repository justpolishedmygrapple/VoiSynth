package com.example.myapplication.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.data.HistoryItem
import com.example.myapplication.data.HistoryResponse
import com.example.myapplication.data.HistoryTextAdapter
import com.example.myapplication.data.Voice
import com.example.myapplication.ui.MediaViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes





class HistoryBySelectedVoiceFragment: Fragment(R.layout.history_by_selected_voice) {


    private var historyResponse: HistoryResponse? = null

    private var selectedVoice: Voice? = null

    private var historyResponseSearchResults: MutableList<HistoryItem>? = null


    private lateinit var historyTextAdapter: HistoryTextAdapter

    private lateinit var historyTextRV: RecyclerView

    private lateinit var voiceName: String

    private lateinit var mediaViewModel: MediaViewModel

    private val args: HistoryBySelectedVoiceFragmentArgs by navArgs()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var viewModel: MediaViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mediaViewModel = ViewModelProvider(requireActivity()).get(MediaViewModel::class.java)



        viewModel = ViewModelProvider(requireActivity()).get(MediaViewModel::class.java)



        //Bug fix where media player wasn't using the  ViewModel's player
        if (savedInstanceState != null) {
            val position = savedInstanceState.getInt("position")
            mediaViewModel.mediaPlayer?.seekTo(position)


        }


        historyResponse = args.historyItems

        selectedVoice = args.selectedVoice

        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.history_title_bar, selectedVoice?.name)

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

        historyTextRV = view.findViewById(R.id.rv_history_generated_text)

        historyTextRV.layoutManager = LinearLayoutManager(requireContext())
        historyTextRV.setHasFixedSize(true)

        historyTextRV

        historyTextRV.adapter = historyTextAdapter


    }


    private fun onHistoryItemClick(historyItem: HistoryItem) {
        Log.d("itempress", "Item ID is: ${historyItem.history_item_id}")


        playFile(historyItem, historyItem.voice_name)

    }

    private fun onHistoryItemLongClick(historyItem: HistoryItem) {
        Log.d(
            "longpress",
            "https://api.elevenlabs.io/v1/history/${historyItem.history_item_id}/audio"
        )
        shareMP3(historyItem)
    }

    private fun playFile(historyItem: HistoryItem, name: String){

        coroutineScope.launch {
            withContext(Dispatchers.IO){

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.elevenlabs.io/v1/history/${historyItem.history_item_id}/audio")
                    .header("xi-api-key", BuildConfig.ELEVEN_LABS_API)
                    .build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("$response")

                        val bytes = response.body?.bytes()

                        val tmpMP3 = kotlin.io.path.createTempFile("${selectedVoice!!.name}", ".mp3")

                        val file = File(tmpMP3.pathString)

                        tmpMP3.writeBytes(bytes!!)

                        val fileUri = FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.fileprovider",
                            file
                        )


                        viewModel.mediaPlayer?.apply {
                            stop()
                            reset()
                            release()
                        }

                        viewModel.mediaPlayer = MediaPlayer.create(requireContext(), fileUri)?.apply {
                            start()

                            viewModel.isPlaying = true


                            setOnCompletionListener {
                                reset()
                                release()
                                viewModel.mediaPlayer = null
                                viewModel.isPlaying = false
                            }
                        }

                    }
                } catch(e: Exception){
                    Snackbar.make(
                        requireView(),
                        e.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }
        }


    override fun onPause() {
        super.onPause()
        viewModel.mediaPlayer?.pause()
        viewModel.isPlaying = false
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        viewModel.mediaPlayer = null
        viewModel.isPlaying = false
    }


    private fun shareMP3(historyItem: HistoryItem) = CoroutineScope(Dispatchers.Main).launch {
        withContext(Dispatchers.IO){

            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.elevenlabs.io/v1/history/${historyItem.history_item_id}/audio")
                .header("xi-api-key", BuildConfig.ELEVEN_LABS_API)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("$response")

                    val bytes = response.body?.bytes()

                    val tmpMP3 = kotlin.io.path.createTempFile("${selectedVoice!!.name}", ".mp3")

                    val file = File(tmpMP3.pathString)

                    tmpMP3.writeBytes(bytes!!)

                    val fileUri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.fileprovider",
                        file
                    )


                    ShareCompat.IntentBuilder(requireContext())
                        .setType("audio/mpeg")
                        .setSubject(getString(R.string.checkout_what_just_said, selectedVoice!!.name))
                        .setText(getString(R.string.share_text, selectedVoice!!.name, historyItem.text))
                        .addStream(fileUri)
                        .startChooser()
                }
            } catch(e: Exception){
                Snackbar.make(
                    requireView(),
                    e.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }


}