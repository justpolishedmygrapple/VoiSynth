
# VoiSynth

VoiSynth allows you to say whatever you want in anyone's voice and share your clips with friends


## Authors

- Dom Wilson: [@DomWilson2](https://github.com/DomWilson2) - Tutorial onboarding screen and graphics
- Matthew Attebery: [@atteberm](https://github.com/atteberm) - XML styling elements, tutorial onboarding graphics
- Austin Mangel: [@justpolishedmygrapple](https://github.com/justpolishedmygrapple) - Kotlin, except for majority of onboarding screen 

## Demo

[Video link to demo over VoiSynth's core features](https://www.youtube.com/watch?v=I5Kk7PHrp50)


## Environment Variables

* On Linux and Mac: 
```bash
echo "ELEVEN_LABS_API="\"YOUR_API_KEY\"" >> ~/.gradle/gradle.properties
```

* On Windows:
    ```
    Add "ELEVEN_LABS_API="YOUR_API_KEY" to C:\Users\You\.gradle\gradle.properties
    ```
* The source code, as it stands, relies on a paid-for ElevenLabs API key. Everything will work for you when you provide your own API key, apart from "Biden" not being available. Upload a voice, or set a pre-made voice as your preferred voice, and then quickmode will work. 
## Features

- Tutorial mode
- Quickmode, where you can have your preferred voice say whatever you want.
- Share your voice clip with friends from either the Quickmode or Voice Generator
- A settings pane, where you can configure:

    * Whether to show or hide pre-made voices provided by ElevenLabs in both the Voice Generator and history view
    * Set your preferred voice
    * Re-run the Tutorial
    * Change your language from either English or Swedish

- History view, where you can filter your previously generated voicelines by character
    * You can play any history item by clicking it, and you can share it by long pressing on any history item. The audio clip will be shared to your favorite app with the text you wrote as well as an MP3 attachment
        * The last ten history items that you generated will be available in the navigation drawer. Click on any one of them to play it!

- Available voices allows you to pick any of your voices and quickly generate audio clips with their voice. The same share functionality is implemented here.

- Add a new voice
    * Upload an MP3 (recommended length of 1 minute or longer) of clear speech of any person speaking. The voice will be immediately available in Voice Generator for you to make new audio clips.

- Localization
    * This app has full translations for English and Swedish

## Installation


## Documentation

[ElevenLabs API Documentation](https://api.elevenlabs.io/docs)


## Screenshots

### Onboarding tutorial
![Onboarding screen](https://i.imgur.com/VR9Kc9O.png)
#### Intuitive, helpful tutorial
![Tutorial screen](https://i.imgur.com/svVa8XG.png)
### Quickmode 
![Quickmode](https://i.imgur.com/d7NbaWq.png)
### Share your audio clip
![Share MP3](https://i.imgur.com/hpm1INk.png)
### Navigation with history items
![Navigation pane with history items](https://i.imgur.com/j7uBKyL.png)
### Search history by character
![Search history by character](https://i.imgur.com/ugJUFOz.png)
![View all history items from a character](https://i.imgur.com/AmMaZQ5.png)
### Settings
![Settings](https://i.imgur.com/agaQ1Uz.png)
####  When show all voices is enabled:
 ![All voices](https://i.imgur.com/BJGgxuY.png)
 ####  When hide all voices is enabled:
 ![Only custom voices](https://i.imgur.com/UcdFa8M.png)
 
 ### Add new voice
![Add new voice](https://i.imgur.com/FcCQ2Du.png)
### Full localization in Swedish and English
![Swedish](https://i.imgur.com/xE6XZEt.png)


## API Reference

### See [the voice interface](https://github.com/osu-cs492-w23/final-project-ai-voice/blob/austin-splash/app/src/main/java/com/example/myapplication/ui/VoiceInterface.kt) for the majority of the API endpoints used in this app. 
### Posting an MP3 is defined in [fun postMP3(uri: Uri, voiceName: String): Boolean](https://github.com/osu-cs492-w23/final-project-ai-voice/blob/austin-splash/app/src/main/java/com/example/myapplication/fragments/AddVoiceFragment.kt)

