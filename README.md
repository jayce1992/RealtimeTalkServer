## Overview

TCP Server implementation of realtime speech translator messenger using Google Speech Recognition API & Yandex Translation API.

The server can serve several clients, but interact with only one of them (I mean it can't broadcast messages).

Server can send and receive voice messages from clients.

Server supports 4 languages: korean, english, russian and french(If you want, you can add new language by slightly modifying code).

## How to run

Step 1. Import project to Android Studio or any other IDE in your choice. 

Step 2. Add a library http://www.java2s.com/Code/Jar/j/Downloadjsonsimple111jar.htm to your project, you will need it to work with Yandex Translation API

Step 3. Go to https://tech.yandex.com/keys/get/?service=trnsl and get the API key

Step 4. In MainActivity.java, find the function getJsonStringYandex and replace apiKey to your own key that you got from https://tech.yandex.com/keys/get/?service=trnsl

Step 5. After that you are ready to run RealtimeTalkServer.


## API Reference
Yandex Translation API - https://tech.yandex.com/translate/

Android Speech API - https://developer.android.com/reference/android/speech/package-summary.html

Android Text to speech API - https://developer.android.com/reference/android/speech/tts/package-summary.html

Cloud Speech API - https://cloud.google.com/speech/

Cloud Translation API - https://cloud.google.com/translate/

## Motivation
A short description of the motivation behind the creation and maintenance of the project. This should explain why the project exists.
