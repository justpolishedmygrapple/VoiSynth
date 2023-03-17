package com.example.myapplication.fragments

import android.content.Context.INPUT_METHOD_SERVICE
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
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.data.Voice
import com.example.myapplication.ui.MediaViewModel
import com.example.myapplication.ui.VoiceInterface
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.net.URLConnection
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes

class VoiceGeneratorFragment: Fragment(R.layout.voice_generator) {

    private val voiceservice = VoiceInterface.create()

    private var selectedVoice: Voice? = null


    private var filePath: String? = null

    private lateinit var mediaViewModel: MediaViewModel

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var loadingIndicator: CircularProgressIndicator


    private val args: VoiceGeneratorFragmentArgs by navArgs()

    private var currentlyGenerating: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        mediaViewModel = ViewModelProvider(requireActivity()).get(MediaViewModel::class.java)

        loadingIndicator = view.findViewById(R.id.voice_generator_loading_indicator)


        selectedVoice = args.selectedVoice
        view.findViewById<TextView>(R.id.tv_selected_voice).text = getString(
            R.string.voice_name,
            selectedVoice!!.name
        )

        val editText = view.findViewById<TextView>(R.id.edit_generated_text)

        editText.setHint(getString(R.string.generate_hint, selectedVoice!!.name))

        val generateButton = view.findViewById<Button>(R.id.button_generate_text)

        generateButton.setOnClickListener {
            onGenerateButtonClick()
        }

        //Enter will close the keyboard
        //Src: https://stackoverflow.com/questions/47298935/handling-enter-key-on-edittext-kotlin-android
        //Src: https://developer.android.com/reference/android/view/inputmethod/InputMethodManager#hideSoftInputFromInputMethod(android.os.IBinder,%20int)
        editText.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
                return@OnKeyListener true
            }
            false
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_voice_generate, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.share_file -> {
                if(filePath != null){
                    shareFileIntent(filePath!!, selectedVoice!!.name)
                }
                else{
                    Snackbar.make(requireView(),
                    "You either haven't generated a file yet, or there was an error",
                    Snackbar.LENGTH_LONG).show()
                }
                true
            }
            else ->{
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun onGenerateButtonClick(){
        loadingIndicator.visibility = View.VISIBLE

        val userRequestedText: String =
            view?.findViewById<EditText>(R.id.edit_generated_text)?.text.toString()

        if (userRequestedText == "") {

            Log.d("userText", "user text is null")
            Snackbar.make(
                requireView(),
                "You have to enter some text in order to generate audio",
                Snackbar.LENGTH_LONG
            ).show()
            loadingIndicator.visibility = View.INVISIBLE
        }
        else{
            playGeneratedAudio(userRequestedText)
            currentlyGenerating = true
        }


    }



    private fun playGeneratedAudio(userRequestedText: String){

        loadingIndicator.visibility = View.VISIBLE

        if(currentlyGenerating){
            val toast = Toast.makeText(
                requireContext(),
                "Please wait until your current request has finished before generating another" +
                        "request",
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

            }
            loadingIndicator.visibility = View.INVISIBLE
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
            .setSubject("Check out what $character just said!")
            .setText("$character just said:\t\t\t\n\n\n\t $userRequestedText")
            .addStream(fileUri)
            .startChooser()
    }

}