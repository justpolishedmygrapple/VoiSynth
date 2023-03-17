package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.Preference

import androidx.preference.PreferenceFragmentCompat
import com.example.myapplication.R
import com.example.myapplication.data.voicedatabase.VoiceDBViewModel
import com.example.myapplication.data.voicedatabase.VoiceDao
import com.example.myapplication.data.voicedatabase.VoiceDatabaseItem

class SettingsFragment: PreferenceFragmentCompat() {

    private lateinit var voiceDBViewModel: VoiceDBViewModel
    private lateinit var voiceDao: VoiceDao




    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        voiceDBViewModel = ViewModelProvider(this).get(VoiceDBViewModel::class.java)

        val voiceNames: MutableList<String> = mutableListOf()
        val voiceIDs: MutableList<String> = mutableListOf()

        val listPreference = findPreference<ListPreference>("pref_voice_key")
        voiceDBViewModel.allVoices.observe(this, Observer { list ->

            for(voice in list){
                voiceNames.add(voice.name)
                voiceIDs.add(voice.voice_id)
            }

            val entries = voiceNames
            val entryValues = voiceIDs

            listPreference?.apply {
                setEntries(entries.toTypedArray())
                setEntryValues(entryValues.toTypedArray())
            }

        })

        listPreference?.setOnPreferenceChangeListener(Preference.OnPreferenceChangeListener{preference, newValue ->

            Log.d("preference changed", newValue.toString())
            true
        })

        }

    }

