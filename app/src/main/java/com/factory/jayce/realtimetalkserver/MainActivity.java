package com.factory.jayce.realtimetalkserver;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG = "log";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    protected TextToSpeech tts;
    protected String voice_message = null;
    protected String lang_from = null;
    protected String lang_to = null;
    Spinner spinnerLanguageFrom,spinnerLanguageTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(this);
        spinnerLanguageFrom = (Spinner) findViewById(R.id.spinnerLangFrom);
        spinnerLanguageTo = (Spinner) findViewById(R.id.spinnerLangTo);

        initSpinners();
        spinnerLanguageFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //tts.setLanguage(Locale.KOREAN);
                        lang_from = "ko";
                        break;
                    case 1:
                        //tts.setLanguage(Locale.ENGLISH);
                        lang_from = "en";
                        break;
                    case 2:
                        lang_from = "ru";
                        //Locale locale = new Locale("ru");
                        //tts.setLanguage(locale);
                        break;
                    case 3:
                        lang_from = "fr";
                        break;
                };
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerLanguageTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //tts.setLanguage(Locale.KOREAN);
                        lang_to = "ko";
                        break;
                    case 1:
                        //tts.setLanguage(Locale.ENGLISH);
                        lang_to = "en";
                        break;
                    case 2:
                        lang_to = "ru";
                        //Locale locale = new Locale("ru");
                        //tts.setLanguage(locale);
                        break;
                    case 3:
                        lang_to = "fr";
                        break;
                };
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d(LOG, "This Language is not supported");
                    }
                } else Log.d(LOG, "Initilization Failed!");
            }
        });

        new Thread(new ServerThread(this)).start();
    }

    protected void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    // SPEECH RECOGNITION
    protected void promptSpeechInput(int selectedLanguage) {
        String lang = null;
        String prompt = null;
        switch(selectedLanguage) {
            case 0:
                lang = "ko-KR";
                prompt = "말하세요";
                break;
            case 1:
                lang = "en-US";
                prompt = "Speak now";
                break;
            case 2:
                lang = "ru-RU";
                prompt = "Говорите";
                break;
            case 3:
                lang = "fr-FR";
                prompt = "prendre la parole";
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voice_message = result.get(0);
                }
                break;
            }
        }
    }
    // YANDEX TRANSLATION API
    protected String getJsonStringYandex(String trans, String text) throws IOException, org.json.simple.parser.ParseException {
        String apiKey = "trnsl.1.1.20170307T034647Z.4992e59ad95c1481.37e8035638e36cc9369098aa87fd483264e86e9e";
        String requestUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key="
                + apiKey + "&lang=" + trans + "&text=" + text;

        URL url = new URL(requestUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();
        int rc = httpConnection.getResponseCode();
        if (rc == 200) {
            String line = null;
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            StringBuilder strBuilder = new StringBuilder();
            while ((line = buffReader.readLine()) != null) {
                strBuilder.append(line + '\n');
            }

            return getTranslateFromJSON(strBuilder.toString());
        }
        return "Error";
    }

    protected  String getTranslateFromJSON(String str) throws org.json.simple.parser.ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(str);
        StringBuilder sb = new StringBuilder();
        JSONArray array = (JSONArray) object.get("text");
        for (Object s : array) {
            sb.append(s.toString() + "\n");
        }
        return sb.toString();
    }

    private void initSpinners() {
        List<String> languages = new ArrayList<String>();
        languages.add("korean");
        languages.add("english");
        languages.add("russian");
        languages.add("french");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguageFrom.setAdapter(dataAdapter);
        spinnerLanguageTo.setAdapter(dataAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                promptSpeechInput(spinnerLanguageFrom.getSelectedItemPosition());
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
