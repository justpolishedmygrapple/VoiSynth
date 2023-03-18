package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Voice
import com.example.myapplication.data.VoiceAdapter
import com.example.myapplication.ui.ListOfVoicesViewModel

class VoiceListFragment: Fragment(R.layout.voice_list) {

    private val voiceAdapter = VoiceAdapter(::onVoiceItemClick)


    private lateinit var voiceResultsRV: RecyclerView

    private val voiceViewModel: ListOfVoicesViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        voiceResultsRV = view.findViewById(R.id.rv_voice_list)

        voiceResultsRV.layoutManager = LinearLayoutManager(requireContext())
        voiceResultsRV.setHasFixedSize(true)

        val prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val hidePremade = prefManager.getString(getString(R.string.pref_hide_premade_key), null)

        Log.d("hidePremade", hidePremade.toString())





        voiceResultsRV.adapter = voiceAdapter

        voiceViewModel.loadListOfVoices()

        if(voiceViewModel.voiceListResults.value == null ){
            Log.d("voice view model shows nothing", "voice view model shows nothing")
            voiceResultsRV.visibility = View.INVISIBLE
            val errorText: TextView = view.findViewById(R.id.tv_error_voice_view)

            errorText.text = getString(R.string.no_voices_error)

            errorText.visibility = View.VISIBLE
        }


        if (hidePremade == "Hide pre-made voices" || hidePremade == "Göm konstgjorda röster") {
            voiceViewModel.voiceListResults.observe(viewLifecycleOwner) { results ->
                voiceAdapter.addVoice(results?.voices?.filterNot { it.category == "premade" })
            }
        } else if (hidePremade == "Show pre-made voices") {
            voiceViewModel.voiceListResults.observe(viewLifecycleOwner) { results ->
                voiceAdapter.addVoice(results?.voices)
            }
        } else {
            voiceViewModel.voiceListResults.observe(viewLifecycleOwner) { results ->
                voiceAdapter.addVoice(results?.voices)
            }
        }




    }

    private fun onVoiceItemClick(voice: Voice) {

        val directions = VoiceListFragmentDirections.navigateToVoiceGenerator(voice)

        findNavController().navigate(directions)

    }
}