package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference

import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.myapplication.R
import com.example.myapplication.voicedatabase.VoiceDBViewModel
import com.example.myapplication.voicedatabase.VoiceDao
import com.example.myapplication.voicedatabase.VoiceDatabaseItem

class SettingsFragment: PreferenceFragmentCompat() {

    private lateinit var voiceDBViewModel: VoiceDBViewModel
    private lateinit var voiceDao: VoiceDao




    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        voiceDBViewModel = ViewModelProvider(this).get(VoiceDBViewModel::class.java)

        val voiceNames: MutableList<String> = mutableListOf()
        val voiceIDs: MutableList<String> = mutableListOf()

        val key = getString(R.string.pref_voice_key)

        val listPreference = findPreference<ListPreference>(key)
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

    override fun onPause() {
        super.onPause()

        //Saves preference when user navigates away
        //Src: In java, but it helped: https://stackoverflow.com/questions/24039577/android-sharedpreferences-editing-not-working
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().apply()
    }

    }

