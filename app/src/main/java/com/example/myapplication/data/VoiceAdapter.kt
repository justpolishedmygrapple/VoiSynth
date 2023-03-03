package com.example.myapplication.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class VoiceAdapter : RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder>() {

    val voices: MutableList<Voice> = mutableListOf()

    override fun getItemCount() = voices.size

    fun addVoice(voice: Voice){
        voices.add(0, voice)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.voice_list_item, parent, false)
        return VoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoiceViewHolder, position: Int) {
        holder.bind(voices[position])
    }

    class VoiceViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val voiceNameTV: TextView = view.findViewById(R.id.tv_voice_name)
        private val voiceIDTV: TextView = view.findViewById(R.id.tv_voice_id)

        private var currentVoice: Voice? = null

        fun bind(voice: Voice){
            currentVoice = voice
            voiceIDTV.text = voice.voice_id
            voiceNameTV.text = voice.name

        }

    }
}