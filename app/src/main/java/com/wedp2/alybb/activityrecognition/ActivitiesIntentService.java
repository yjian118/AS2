package com.wedp2.alybb.activityrecognition;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class ActivitiesIntentService extends IntentService {
    private static final String TAG = "ActivitiesIntentService";

    public ActivitiesIntentService(){
        super(TAG);
    }

    protected void onHandleIntent(Intent intent){
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent i = new Intent(constants.string_action);
        Intent aa = new Intent(this,MainActivity.class);
        DetectedActivity detectedActivity = result.getMostProbableActivity();
        i.putExtra(constants.string_extra,detectedActivity);
        i.putExtra("type",detectedActivity.getType());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

}
