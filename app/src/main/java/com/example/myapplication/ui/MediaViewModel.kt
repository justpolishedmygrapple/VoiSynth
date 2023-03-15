package com.example.myapplication.ui

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel

class MediaViewModel: ViewModel(){

    var mediaPlayer: MediaPlayer? = null
    var playing = false

}