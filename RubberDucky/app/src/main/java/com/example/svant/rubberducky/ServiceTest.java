package com.example.svant.rubberducky;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Svant on 06.12.2017.
 */

public class ServiceTest extends IntentService {
    public ServiceTest(){
        super("ServiceTest");
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

    }
}
