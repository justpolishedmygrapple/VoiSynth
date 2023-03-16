package com.example.myapplication.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.data.HistoryItem
import com.example.myapplication.data.HistoryResponse
import com.example.myapplication.database.HistoryDBViewModel
import com.example.myapplication.database.HistoryDatabaseItem
import com.example.myapplication.ui.HistorySearchViewModel
import com.example.myapplication.ui.MediaViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes

class QuickGenerateFragment: Fragment(R.layout.quick_generate) {


    private lateinit var mediaViewModel: MediaViewModel

    private val coroutineScope = CoroutineScope(Dispatchers.Main)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val historySearchViewModel:  HistorySearchViewModel by viewModels()

        val navView: NavigationView = requireActivity().findViewById(R.id.nav_view)

        val navmenu = navView.menu


        val historyVM: HistoryDBViewModel by viewModels()

        mediaViewModel = ViewModelProvider(requireActivity()).get(MediaViewModel::class.java)


        historySearchViewModel.loadHistorySearchResults()


        historySearchViewModel.historySearchResults.observe(viewLifecycleOwner){ it ->

            navmenu.removeGroup(R.id.history_group)

            var i = 0
            if(it != null){
                for(historyItem in it.history){
                    historyVM.addHistoryItem(HistoryDatabaseItem(historyItem.history_item_id,
                    historyItem.voice_name,
                    historyItem.text,
                    historyItem.date_unix))


                    //Src: https://www.bestprog.net/en/2022/05/23/kotlin-anonymous-functions-lambda-expressions/

                    val textToUse = {str: String -> if(str.length < 30){
                        str
                    }
                    else{
                        str.substring(0,30) + "..."
                    }}

                    val group = navmenu.addSubMenu(R.id.history_group, Menu.NONE, Menu.NONE, historyItem.voice_name)
                    val menuItemClicked = group.add(Menu.NONE, Menu.NONE, Menu.NONE, textToUse(historyItem.text) )

                    val fragmentManager = fragmentManager

                    fragmentManager?.findFragmentById(R.id.history_by_selected_voice)

                    menuItemClicked.setOnMenuItemClickListener {

                        playFile(historyItem)

                        true
                    }

                    i+=1

                    //Show the last ten menu items in the history, by any character
                    if(i == 10){
                        break
                    }

                }


            }
        }





    }

    private fun playFile(historyItem: HistoryItem){

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

                        val tmpMP3 = kotlin.io.path.createTempFile("${historyItem.voice_name}", ".mp3")

                        val file = File(tmpMP3.pathString)

                        tmpMP3.writeBytes(bytes!!)

                        val fileUri = FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.fileprovider",
                            file
                        )


                        mediaViewModel.mediaPlayer?.apply {
                            stop()
                            reset()
                            release()
                        }

                        mediaViewModel.mediaPlayer = MediaPlayer.create(requireContext(), fileUri)?.apply {
                            start()

                            mediaViewModel.isPlaying = true


                            setOnCompletionListener {
                                reset()
                                release()
                                mediaViewModel.mediaPlayer = null
                                mediaViewModel.isPlaying = false
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
        mediaViewModel.mediaPlayer?.pause()
        mediaViewModel.isPlaying = false
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaViewModel.mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        mediaViewModel.mediaPlayer = null
        mediaViewModel.isPlaying = false
    }

}