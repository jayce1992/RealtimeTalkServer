## Overview

TCP Server implementation of realtime speech translator messenger using Google Speech Recognition API & Yandex Translation API.

The server can serve several clients, but interact with only one of them (I mean it can't broadcast messages).

Server can send and receive voice messages from clients.

It is possible to choose input and output languages(example: from RU to EN).

Server supports 4 languages: korean, english, russian and french(If you want, you can add new language by slightly modifying code).

You will find the lists of Supported languages in Additional references below.

## Workflow
Server
1. Listens for incoming voice message. 
2. Turns a voice message into a text message.
3. Sends text message to a client.
Client
1. Recieves the text message and determines it's language.
2. Turns text message into a voice message.
3. Pronounces the voice message.
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

## Additional References

Yandex Translation API supported languages - https://tech.yandex.com/translate/doc/dg/concepts/api-overview-docpage/

Google API supported languages - https://cloud.google.com/speech/docs/languages
