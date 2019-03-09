package com.wedp2.alybb.activityrecognition;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private TextView mDetectedActivityTextView;
    private ImageView imgActivity;
    private ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDetectedActivityTextView = (TextView) findViewById(R.id.detected_activities_textview);
        imgActivity = findViewById(R.id.imageView);
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
//        mDetectedActivityTextView.setText(intent.getStringExtra("type"));





    }

    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this,ActivitiesIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 3000, getActivityDetectionPendingIntent());
        Log.i(TAG,"Connected");
    }

    public void onConnectionSuspended(int i){
        Log.i(TAG,"Connection suspended");
        mGoogleApiClient.connect();
    }

    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.i(TAG,"Connection failed.Error" + connectionResult.getErrorCode());
    }

    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop(){
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    public String getDetectedActivity(int type){
        Resources resources = this.getResources();
        switch (type){
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.walking);
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.bicycle);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            default:
                return resources.getString(R.string.unidentifiable);
        }
    }

    public int getImag(int type){

        switch (type){
            case DetectedActivity.RUNNING:
                return R.drawable.running;
            case DetectedActivity.STILL:
                return R.drawable.still;
            case DetectedActivity.WALKING:
                return R.drawable.walking;
            case DetectedActivity.ON_FOOT:
                return R.drawable.walking;
            case DetectedActivity.IN_VEHICLE:
                return R.drawable.vehicle;
            case DetectedActivity.ON_BICYCLE:
                return R.drawable.bicycle;
            case DetectedActivity.TILTING:
                return R.drawable.tilting;
            case DetectedActivity.UNKNOWN:
                return R.drawable.wrong;
            default:
                return R.drawable.wrong;
        }
    }


//    private void handleUserActivity(int type) {
//        String label = getString(R.string.unidentifiable);
//        int icon = R.drawable.running;
//
//        switch (type) {
//            case DetectedActivity.RUNNING: {
//                label = getString(R.string.running);
//                icon = R.drawable.running;
//                break;
//            }
//            case DetectedActivity.STILL: {
//                label = getString(R.string.still);
//                icon = R.drawable.still;
//                break;
//            }
//            case DetectedActivity.WALKING: {
//                label = getString(R.string.walking);
//                icon = R.drawable.walking;
//                break;
//            }
//            case DetectedActivity.UNKNOWN: {
//                label = getString(R.string.unknown);
//                break;
//            }
//        }
//
//        Log.e(TAG, "User activity: " + label );
//
//            mDetectedActivityTextView.setText(label);
//            imgActivity.setImageResource(icon);
//
//    }



    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        public void onReceive (Context context, Intent intent){
            int type = intent.getIntExtra("type",-1);
            String activityString = "";
//            for(DetectedActivity activity: detectedActivities){
            activityString +=  "Activity: " + getDetectedActivity(type);
//            }
            mDetectedActivityTextView.setText(activityString);
            imgActivity.setImageResource(getImag(type));
            RecognizeMusic(type);


//            if (intent.getAction().equals(constants.string_action)){
//                int type = intent.getIntExtra("type",-1);
//                handleUserActivity(type);
//            }


        }
    }

    public void RecognizeMusic(int type){
        if( type == DetectedActivity.WALKING || type == DetectedActivity.ON_FOOT){
            Intent ms = new Intent(this,MusicService.class);
            startService(ms);
        }else{}
    }

//    public void requestActivityUpdates(View view){
//        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, "GoogleApiClient not yet connected", Toast.LENGTH_SHORT).show();
//        } else {
//            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, getActivityDetectionPendingIntent()).setResultCallback(this);
//        }
//    }
//
//    public void removeActivityUpdates(View view) {
//        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, getActivityDetectionPendingIntent()).setResultCallback(this);
//    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status){
        if(status.isSuccess()){
            Log.e(TAG,"Successfully added activity detection.");
        }else{
            Log.e(TAG,"Error:" +status.getStatusMessage());
        }
    }

    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(constants.string_action));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }


}




