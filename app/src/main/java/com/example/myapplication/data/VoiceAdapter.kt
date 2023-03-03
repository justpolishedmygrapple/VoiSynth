package com.example.myapplication.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class VoiceAdapter() : RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder>() {

    private var voiceList = listOf<Voice>()

    override fun getItemCount() = this.voiceList.size

    fun addVoice(newVoiceList: List<Voice>?){
        voiceList = newVoiceList ?: listOf()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.voice_list_item, parent, false)
        return VoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoiceViewHolder, position: Int) {
        holder.bind(this.voiceList[position])
    }

    class VoiceViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val voiceNameTV: TextView = view.findViewById(R.id.tv_voice_name)
        private val voiceIDTV: TextView = view.findViewById(R.id.tv_voice_id)

        private lateinit var currentVoice: Voice

        fun bind(voice: Voice){
            currentVoice = voice
            voiceIDTV.text = voice.voice_id
            voiceNameTV.text = voice.name

        }

    }
}