package com.example.myapplication.fragments

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.myapplication.BuildConfig
import com.example.myapplication.R
import com.example.myapplication.data.HistoryItem
import com.example.myapplication.data.Voice
import com.example.myapplication.voicedatabase.VoiceDBViewModel
import com.example.myapplication.voicedatabase.VoiceDatabaseItem
import com.example.myapplication.database.HistoryDBViewModel
import com.example.myapplication.database.HistoryDatabaseItem
import com.example.myapplication.ui.HistorySearchViewModel
import com.example.myapplication.ui.ListOfVoicesViewModel
import com.example.myapplication.ui.MediaViewModel
import com.example.myapplication.ui.VoiceInterface
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URLConnection
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes

class QuickGenerateFragment: Fragment(R.layout.quick_generate) {


    private lateinit var mediaViewModel: MediaViewModel

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val historyVM: HistoryDBViewModel by viewModels()

    private lateinit var loadingIndicator: CircularProgressIndicator

    private var filePath: String? = null


    private val voiceViewModel: ListOfVoicesViewModel by viewModels()


    private lateinit var voiceDBViewModel: VoiceDBViewModel

    private val voiceservice = VoiceInterface.create()

    private var currentlyGenerating: Boolean = false

    private var selectedVoice: Voice? = null


    private var enteredText: String? = null


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.quick_voice_generate, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.share_file_quick_mode  ->{
                if(filePath != null){
                    shareFileIntent(filePath!!, selectedVoice!!.name)
                }
                else{
                    Snackbar.make(requireView(),
                    getString(R.string.no_file_generated_error),
                    Snackbar.LENGTH_LONG).show()
                }
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        loadingIndicator = view.findViewById(R.id.quick_gen_loading_indicator)





        val navView: NavigationView = requireActivity().findViewById(R.id.nav_view)

        val quickGenButton = view.findViewById<Button>(R.id.button_generate_text_quick_mode)

        quickGenButton.setOnClickListener {
            onGenerateButtonClick()
        }


        voiceDBViewModel = ViewModelProvider(this).get(VoiceDBViewModel::class.java)

        voiceViewModel.loadListOfVoices()

        voiceViewModel.voiceListResults.observe(viewLifecycleOwner){it ->

            if(it != null) {
                for (voice in it.voices) {
                    val x = voice.name
                    Log.d("desperate", x)

                    voiceDBViewModel.addVoice(VoiceDatabaseItem(voice.voice_id, voice.name, voice.category))
                }
            }
        }



        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(requireContext())


        //Makes sure Biden is the preferred voice by default
        val prefVoiceID: String? = preferenceManager.getString(getString(R.string.pref_voice_key), getString(R.string.biden_default_key))

        val quickGenVoiceSelectedTextView: TextView = view.findViewById(R.id.tv_selected_voice_quick_gen)

        val quickGenEditText: EditText = view.findViewById(R.id.quick_gen_edit_text)



            quickGenEditText.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(quickGenEditText.windowToken, 0)
                return@OnKeyListener true
            }
            false
        })


        voiceDBViewModel.searchVoice(prefVoiceID?: getString(R.string.biden_default_key)).observe(viewLifecycleOwner) { prefVoice ->

            when(prefVoice) {
                null ->
                {(requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.quick_generate_default_title_bar)}

                else ->
                {
                    (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.quickmode_title_with_pref_set, prefVoice.name)
                    quickGenVoiceSelectedTextView.text = getString(R.string.voice_name_quick_gen, prefVoice.name)
                    quickGenEditText.hint = getString(R.string.generate_hint_quick_mode, prefVoice.name)
                    selectedVoice = Voice(prefVoice.voice_id, prefVoice.name, "")


                }
            }
        }

        loadHistoryResults()




        mediaViewModel = ViewModelProvider(requireActivity()).get(MediaViewModel::class.java)




    }

    private fun onGenerateButtonClick(){
        loadingIndicator.visibility = View.VISIBLE

        enteredText = view?.findViewById<EditText>(R.id.quick_gen_edit_text)?.text.toString()



        if (enteredText == "") {

            Log.d("userText", "user text is null")
            Snackbar.make(
                requireView(),
                getString(R.string.no_text_entered),
                Snackbar.LENGTH_LONG
            ).show()
            loadingIndicator.visibility = View.INVISIBLE
        }
        else{
            playGeneratedAudio(enteredText!!)
            currentlyGenerating = true
        }


    }

    private fun playGeneratedAudio(userRequestedText: String){

        loadingIndicator.visibility = View.VISIBLE

        if(currentlyGenerating){
            val toast = Toast.makeText(
                requireContext(),
                getString(R.string.please_wait_generate_again),
                Toast.LENGTH_SHORT
            )
            toast.show()
            loadingIndicator.visibility = View.INVISIBLE
            return
        }

        coroutineScope.launch {
            withContext(Dispatchers.IO){


                try{

                    val audioFile = getAudio(
                        selectedVoice!!.voice_id,
                        generateJsonRequestBody(userRequestedText))
                    if(audioFile!!.isNotEmpty()){

                        val tmpMP3 = kotlin.io.path.createTempFile(selectedVoice?.name ?: "null", ".mp3")
                        tmpMP3.writeBytes(audioFile ?: byteArrayOf())

                        val file = File(tmpMP3.pathString)

                        filePath = tmpMP3.pathString

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
                                currentlyGenerating = false
                            }
                        }


                    }
                } catch(e: Exception){
                    currentlyGenerating = false
                    Snackbar.make(
                        requireView(),
                        e.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                loadHistoryResults()


            }
            loadingIndicator.visibility = View.INVISIBLE
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

    suspend fun getAudio(voice_id: String, requestBody: RequestBody): ByteArray? {

        val response = voiceservice.generateVoiceAudio(voice_id, requestBody).apply {
            delay(500)
        }

        return if(response.isSuccessful){
            response.body()?.bytes()}
        else{
            null}

    }

    private fun generateJsonRequestBody(text: String): RequestBody {

        val requestJSONObject = JSONObject()

        val voiceSettings = JSONObject()

        requestJSONObject.put("text", text)

        voiceSettings.put("stability", "0.75")
        voiceSettings.put("similarity_boost", "0.75")

        requestJSONObject.put("voice_settings", voiceSettings)

        return requestJSONObject.toString().toRequestBody("application/json".toMediaTypeOrNull())


    }

    private fun shareFileIntent(filePath: String, character: String){

        val shareIntent = Intent(Intent.ACTION_SEND)

        shareIntent.type = URLConnection.guessContentTypeFromName(filePath)

        val userRequestedText: String =
            view?.findViewById<EditText>(R.id.edit_generated_text)?.text.toString()

        val file = File(filePath)

        val fileUri = FileProvider.getUriForFile(
            requireContext(), "${requireContext().packageName}.fileprovider", file)



        ShareCompat.IntentBuilder(requireContext())
            .setType("audio/mpeg")
            .setSubject(getString(R.string.checkout_what_just_said, character))
            .setText(getString(R.string.share_text, character, enteredText))
            .addStream(fileUri)
            .startChooser()
    }

    private fun loadHistoryResults(){

        coroutineScope.launch {
            withContext(Dispatchers.IO){
            }
            val navView: NavigationView = requireActivity().findViewById(R.id.nav_view)


            val navmenu = navView.menu

            val historySearchViewModel:  HistorySearchViewModel by viewModels()

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

                        val group = navmenu.addSubMenu(R.id.history_group, Menu.NONE, Menu.NONE, historyItem.voice_name)
                        val menuItemClicked = group.add(Menu.NONE, Menu.NONE, Menu.NONE, historyItem.text)

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


    }



    override fun onPause() {
        super.onPause()
        mediaViewModel.mediaPlayer?.pause()
        mediaViewModel.isPlaying = false

        view?.findViewById<EditText>(R.id.quick_gen_edit_text)?.setText("")

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