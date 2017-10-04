package com.example.shanakagamage.smsreader;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by shanaka.gamage on 10/2/2017.
 */

public class TextToSpeechService extends Service implements TextToSpeech.OnInitListener {
    public final static String TAG = "TextToSpeechService";

    public static final String TEXT_TO_READ = "text";
    private final String UTTERANCE_ID = "FINISHED_PLAYING";
    private final int MULTI_LINE = 2;

    private TextToSpeech tts;
    private String texts;
    private boolean isInit;

    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            if (utteranceId.equals(UTTERANCE_ID)) {
                stopSelf();
            }
        }

        @Override
        public void onError(String utteranceId) {
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(getApplicationContext(), this);
        tts.setOnUtteranceProgressListener(utteranceProgressListener);
        Log.d(TAG, "onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        texts = intent.getStringExtra(TextToSpeechService.TEXT_TO_READ);

        if (isInit) {
            speak();
        }

        return TextToSpeechService.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit");
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.ENGLISH);
            if (result != TextToSpeech.LANG_MISSING_DATA
                    && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                speak();
                isInit = true;
            }
        }
    }

    private void speak() {
        if (tts != null) {

            // Speak with 3 parameters deprecated but necessary on pre 21 version codes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // This is a single message
                tts.speak(texts, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }
}
