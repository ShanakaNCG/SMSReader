package com.example.shanakagamage.smsreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent_service = new Intent(this, HeadPhoneIntentService.class);
        startService(intent_service);

        Intent service = new Intent(this, HeadPhoneService.class);
        startService(service);
    }
}
