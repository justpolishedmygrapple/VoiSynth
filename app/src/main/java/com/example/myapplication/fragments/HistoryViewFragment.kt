package com.example.myapplication.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.myapplication.R
import com.example.myapplication.data.HistoryAdapter
import com.example.myapplication.data.HistoryResponse
import com.example.myapplication.data.LoadingStatus
import com.example.myapplication.data.Voice
import com.example.myapplication.ui.*
import com.google.android.material.snackbar.Snackbar


class HistoryViewFragment: Fragment(R.layout.history_view) {



    private lateinit var spinner: Spinner


    private var voiceArray = mutableListOf<Voice>()


    private val voiceViewModel: ListOfVoicesViewModel by viewModels()

    private val historySearchViewModel: HistorySearchViewModel by viewModels()


    private var historyItems: HistoryResponse? = null

    private val TAG = "HistoryViewActivity"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        voiceArray = mutableListOf<Voice>()


        spinner = view.findViewById<Spinner>(R.id.spinner_history)


        val button: Button = view.findViewById(R.id.button_history)

        if (historySearchViewModel.historySearchResults.value == null) {
            historySearchViewModel.loadHistorySearchResults()
        }

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val hidePremadeVoices = preferenceManager.getString(getString(R.string.pref_hide_premade_key),null)



        historySearchViewModel.historySearchResults.observe(viewLifecycleOwner) { histResults ->
            if (histResults != null) {
                historyItems = histResults

                for (hist in historyItems!!.history) {
                    hist.url = uriSchemeBuilder(hist.history_item_id)
                }

            }
        }

        val tvError: TextView = view.findViewById(R.id.tv_error_history_view)


        voiceViewModel.loadingStatus.observe(viewLifecycleOwner){ uiState ->

            when (uiState){

                LoadingStatus.ERROR -> {
                    tvError.visibility = View.VISIBLE
                    spinner.visibility = View.INVISIBLE
                    button.visibility = View.INVISIBLE
                } else -> {
                tvError.visibility = View.INVISIBLE
                spinner.visibility = View.VISIBLE
                button.visibility = View.VISIBLE

                }


            }

        }





        if (voiceViewModel.voiceListResults.value == null) {

            voiceViewModel.loadListOfVoices()

        }



        voiceViewModel.voiceListResults.observe(viewLifecycleOwner) { results ->



            if (results != null) {
                for (voice in results.voices) {
                    voiceArray.add(voice)
                    Log.d("NewVoiceAdded", voice.toString())
                }


                if(hidePremadeVoices == "Hide pre-made voices" || hidePremadeVoices == "Göm konstgjorda röster"){
                    spinner.adapter = HistoryAdapter(requireContext(), (voiceArray.filterNot { it.category == "premade" }).sortedBy { it.name })
                }
                else{
                    spinner.adapter = HistoryAdapter(requireContext(), voiceArray.sortedBy { it.name }) }
                }





            }





        button.setOnClickListener {

            try{
                val directions = HistoryViewFragmentDirections.navigateToHistoryBySelectedVoice(
                    spinner.selectedItem as Voice, historyItems as HistoryResponse)
                findNavController().navigate(directions)
            }catch (e: Exception){
                Snackbar.make(requireView(),
                getString(R.string.no_voice_selected),
                Snackbar.LENGTH_LONG).show()
                Log.d("NullException", e.toString())
            }

        }



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedVoice = p0?.getItemAtPosition(p2) as? Voice

                selectedVoice?.let {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_selected_voice, it.name),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Log.d("selection", selectedVoice!!.name)

            }


        }
    }


    private fun uriSchemeBuilder(audioId: String): Uri {
        return Uri.parse("https://api.elevenlabs.io/v1/history/${audioId}/audio")
    }

}







