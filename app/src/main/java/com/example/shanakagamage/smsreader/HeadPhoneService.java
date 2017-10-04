package com.example.shanakagamage.smsreader;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.*;
import android.os.Process;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class HeadPhoneService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private MusicIntentReceiver myReceiver;
    private boolean headPhonePluged = false;
    private static final String TAG = "HeadPhoneService";


    @Override
    public void onCreate() {
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("TutorialService", Process.THREAD_PRIORITY_BACKGROUND);
        // start the new handler thread
        thread.start();

        mServiceLooper = thread.getLooper();
        // start the service using the background handler
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {


            myReceiver = new MusicIntentReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(myReceiver, filter);
         /*   // Well calling mServiceHandler.sendMessage(message); from onStartCommand,
            // this method will be called.

            // Add your cpu-blocking activity here
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            showToast("Finishing TutorialService, id: " + msg.arg1);
            // the msg.arg1 is the startId used in the onStartCommand, so we can track the running sevice here.
            stopSelf(msg.arg1);*/
        }
    }

    public class MusicIntentReceiver extends BroadcastReceiver {

        public MusicIntentReceiver(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Recived intend actions: " + intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                int duration = Toast.LENGTH_SHORT;
                CharSequence text;
                Toast toast;
                switch (state) {
                    case 0:
                        Intent intentSMSReciver = new Intent(context, SMSReciver.class);
                        startService(intentSMSReciver);
                        headPhonePluged = false;
                        text = "Headset is unplugged!";
                        toast = Toast.makeText(context, text, duration);
                        toast.show();
                        // Log.d(TAG, "Headset is unplugged");
                        break;
                    case 1:
                        headPhonePluged = true;
                        text = "Headset is plugged!";
                        toast = Toast.makeText(context, text, duration);
                        toast.show();
                        //Log.d(TAG, "Headset is plugged");
                        break;
                    default:
                        text = "I have no idea what the headset state is!";
                        toast = Toast.makeText(context, text, duration);
                        toast.show();
                        //Log.d(TAG, "I have no idea what the headset state is");
                }
            }

        }
    }
}
