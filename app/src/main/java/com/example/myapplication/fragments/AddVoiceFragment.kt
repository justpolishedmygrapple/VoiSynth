package com.example.myapplication.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.ui.VoiceInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.example.myapplication.ELEVEN_LABS_API
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.*

class AddVoiceFragment: Fragment(R.layout.add_voice) {

    private val TAG: String = "AddVoiceFragment"

    private var selectedFile: Uri? = null

    private val x = VoiceInterface.create()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var nameTextField: EditText

    private lateinit var loadingIndicator: CircularProgressIndicator


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: Button = view.findViewById(R.id.button_browse_files)

        nameTextField = view.findViewById(R.id.edit_voice_name)

        loadingIndicator = view.findViewById(R.id.loading_indicator_add_voice)





        val openFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    selectedFile = result.data?.data
                }

                val openFileLauncher =
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == RESULT_OK) {
                            selectedFile = result.data?.data
                        }

                        Log.d(TAG, selectedFile.toString())


                    }
            }

        button.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "audio/mpeg"
            }


            startActivityForResult(intent, 0)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            data?.data?.let { fileUri ->
                loadingIndicator.visibility = View.VISIBLE
                postMP3(fileUri, nameTextField.text.toString())
            }
        }
    }


    fun postMP3(uri: Uri, voiceName: String): Boolean {

        var x: Boolean? = false

        if(voiceName.length == 0 ){
            loadingIndicator.visibility = View.INVISIBLE

            Snackbar.make(
                requireView(),
                getString(R.string.please_enter_name),
                Snackbar.LENGTH_LONG
            ).show()
            return false
        }

        coroutineScope.launch {
            loadingIndicator.visibility = View.VISIBLE
            withContext(Dispatchers.IO) {

                try {


                    Log.d(TAG, selectedFile.toString())

                    val inputStream = requireContext().contentResolver.openInputStream(uri)

                    val reqBody = inputStream?.let {
                        RequestBody.create(
                            "audio/mpeg".toMediaTypeOrNull(),
                            it.readBytes()
                        )
                    }


                    val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("name", "${voiceName}")
                        .addFormDataPart("labels", "")

                    reqBody?.let {
                        multipartBuilder.addFormDataPart("files", "{$voiceName}.mp3", it)
                    }


                    val request = Request.Builder()
                        .url("https://api.elevenlabs.io/v1/voices/add")
                        .header("accept", "application/json")
                        .header("xi-api-key", ELEVEN_LABS_API)
                        .post(multipartBuilder.build())
                        .build()

                    val client = OkHttpClient()
                    val response = client.newCall(request).execute()


                    inputStream?.close()

                    Snackbar.make(
                        requireView(),
                        getString(R.string.successfully_cloned, voiceName),
                        Snackbar.LENGTH_LONG
                    ).show()

                } catch (e: Exception) {
                    Snackbar.make(
                        requireView(),
                        e.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            loadingIndicator.visibility = View.INVISIBLE
        }

        return true
    }



    }
