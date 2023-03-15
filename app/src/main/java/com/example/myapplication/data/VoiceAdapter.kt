package com.example.myapplication.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R


// BEFORE CHANGE: class VoiceAdapter(private val onVoiceItemClick: (Voice) -> Unit) : RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder>()
class VoiceAdapter(private val onVoiceItemClick: (Voice) -> Unit) : RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder>() {

    private var voiceList = listOf<Voice>()

    override fun getItemCount() = this.voiceList.size

    fun addVoice(newVoiceList: List<Voice>?){
        voiceList = newVoiceList ?: listOf()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.voice_list_item, parent, false)
        return VoiceViewHolder(view, onVoiceItemClick)
    }

    override fun onBindViewHolder(holder: VoiceViewHolder, position: Int) {
        holder.bind(this.voiceList[position])
    }


    //before class VoiceViewHolder(view: View, val onClick: (Voice) -> Unit): RecyclerView.ViewHolder(view){
    class VoiceViewHolder(view: View, val onClick: (Voice) -> Unit): RecyclerView.ViewHolder(view){
        private val voiceNameTV: TextView = view.findViewById(R.id.tv_voice_name)

        private lateinit var currentVoice: Voice

        init{
            itemView.setOnClickListener{
                currentVoice?.let(onClick)
            }
        }

        fun bind(voice: Voice){
            currentVoice = voice
            voiceNameTV.text = voice.name

        }

    }
}