package com.example.myapplication.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.ui.VoiceInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.net.URLConnection
import androidx.documentfile.provider.DocumentFile
import com.example.myapplication.ELEVEN_LABS_API
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.*
import java.net.Inet4Address

class AddVoiceFragment: Fragment(R.layout.add_voice) {

    private val TAG: String = "AddVoiceFragment"

    private var selectedFile: Uri? = null

    private val x = VoiceInterface.create()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: Button = view.findViewById(R.id.button_browse_files)

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
                postMP3(fileUri, "David Attenborough")
            }
        }
    }


    fun postMP3(uri: Uri, voiceName: String){
        coroutineScope.launch {
            withContext(Dispatchers.IO){

                try{
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
                        multipartBuilder.addFormDataPart("files", "whatever.mp3", it)
                    }


                    val request = Request.Builder()
                        .url("https://api.elevenlabs.io/v1/voices/add")
                        .header("accept", "application/json")
                        .header("xi-api-key", ELEVEN_LABS_API)
                        .post(multipartBuilder.build())
                        .build()

                    val client = OkHttpClient()
                    val response = client.newCall(request).execute()

                    Log.d(TAG, response.toString())

                }catch(e: Exception){
                    Snackbar.make(
                        requireView(),
                        e.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }


            }

        }
    }
