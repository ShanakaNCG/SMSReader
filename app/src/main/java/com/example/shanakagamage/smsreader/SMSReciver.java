package com.example.shanakagamage.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Locale;

/**
 * Created by shanaka.gamage on 10/2/2017.
 */

public class SMSReciver extends BroadcastReceiver {
    private SharedPreferences preferences;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSReciver";
    public SMSReciver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent recieved: " + intent.getAction());

        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }
                if (messages.length > -1) {
                    String no = messages[0].getOriginatingAddress();
                    Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(no));
                    Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME},null,null,null);
                    try {
                        c.moveToFirst();
                        String  displayName = c.getString(0);
                        String ContactName = displayName;
                        String toSpeak = "Message recieved: " + messages[0].getMessageBody()+" From "+ContactName;
                        Log.d(TAG, "Message recieved: " + messages[0].getMessageBody()+" From "+ContactName);
                        Intent speechIntent = new Intent(context, TextToSpeechService.class);
                        speechIntent.putExtra(TextToSpeechService.TEXT_TO_READ, toSpeak);
                        context.startService(speechIntent);

                    } catch (Exception e) {
                        // TODO: handle exception
                    }finally{
                        c.close();
                    }

                }
            }
        }
    }
}
