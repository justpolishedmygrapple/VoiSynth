package com.example.myapplication.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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


        voiceResultsRV.adapter = voiceAdapter

        if(voiceViewModel.voiceListResults.value == null){
            voiceViewModel.loadListOfVoices()
        }

        voiceViewModel.voiceListResults.observe(viewLifecycleOwner){ results->
            voiceAdapter.addVoice(results?.voices)
        }

    }

    private fun onVoiceItemClick(voice: Voice){

        val directions = VoiceListFragmentDirections.navigateToVoiceGenerator(voice)

        findNavController().navigate(directions)

    }


}