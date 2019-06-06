package com.storksking.trafficlamps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

public class MainActivity extends AppCompatActivity {

    SharedPreferences settingsPreferences;
    SharedPreferences.Editor editor;
    private PublisherInterstitialAd publisherInterstitialAd;
    private AdView mAdView;
    private Context context = this;
    private static final String TAG = "LOOKING";
    private ImageView imageViewtl, imageViewpl, imageView1, imageView2,
            imageView3, imageView4, imageView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MobileAds.initialize(this, "ca-app-pub-4249365726191634~3701810203");

        publisherInterstitialAd = new PublisherInterstitialAd(this);
        publisherInterstitialAd.setAdUnitId("ca-app-pub-4249365726191634/1199952092");
        publisherInterstitialAd.loadAd(new PublisherAdRequest.Builder()
                .addTestDevice("C208971BC8376D4FDC96E9DC05A7EFA6")
                .build());

        mAdView = findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C208971BC8376D4FDC96E9DC05A7EFA6")
                .build();
        mAdView.loadAd(adRequest);

        settingsPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if (settingsPreferences.getLong("redTime", 5000) == 0
                | settingsPreferences.getLong("yellowTime", 2000) == 0
                | settingsPreferences.getLong("greenTime", 5000) == 0
                | settingsPreferences.getLong("stopTime", 5000) == 0
                | settingsPreferences.getLong("goTime", 5000) == 0) {
            editor.putBoolean("countdown", true);
            editor.putBoolean("wCountdown", true);
            editor.putBoolean("wBlinkingGreen", false);
            editor.putBoolean("smile", false);
            editor.putBoolean("yellowWithRed", false);
            editor.putBoolean("redAfterGreen", false);
            editor.putBoolean("blinkingGreen", false);
            editor.putLong("redTime", 5000);
            editor.putLong("yellowTime", 2000);
            editor.putLong("greenTime", 5000);
            editor.putLong("stopTime", 5000);
            editor.putLong("goTime", 5000);
            editor.apply();
        }

        RelativeLayout tl = findViewById(R.id.traffic_control);
        RelativeLayout pd = findViewById(R.id.pedestrian_control);
        RelativeLayout rs = findViewById(R.id.racing_control);

        imageViewtl = findViewById(R.id.tl3);
        imageViewpl = findViewById(R.id.pl2);
        imageView1 = findViewById(R.id.sl1);
        imageView2 = findViewById(R.id.sl2);
        imageView3 = findViewById(R.id.sl3);
        imageView4 = findViewById(R.id.sl4);
        imageView5 = findViewById(R.id.sl5);

        setColors(Color.WHITE);

        tl.setOnClickListener(onClickListener);
        pd.setOnClickListener(onClickListener);
        rs.setOnClickListener(onClickListener);

    }

    void setColor (int color){
        imageView1.setColorFilter(color);
        imageView2.setColorFilter(color);
        imageView3.setColorFilter(color);
        imageView4.setColorFilter(color);
        imageView5.setColorFilter(color);
    }

    void setColors(int color){
        setColor(color);
        imageViewtl.setColorFilter(color);
        imageViewpl.setColorFilter(color);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.traffic_control:
                    if (publisherInterstitialAd.isLoaded()) {
                        publisherInterstitialAd.show();
                    } else {
                        Log.d(TAG, "The interstitial wasn't loaded yet(settings_main).");
                    }

                    imageViewtl.setColorFilter(Color.GREEN);
                    Intent intent = new Intent(context, TLActivity.class);
                    startActivity(intent);
                    break;
                case R.id.pedestrian_control:
                    if (publisherInterstitialAd.isLoaded()) {
                        publisherInterstitialAd.show();
                    } else {
                        Log.d(TAG, "The interstitial wasn't loaded yet(settings_main).");
                    }

                    imageViewpl.setColorFilter(Color.GREEN);
                    Intent intent2 = new Intent(context, WActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.racing_control:
                    if (publisherInterstitialAd.isLoaded()) {
                        publisherInterstitialAd.show();
                    } else {
                        Log.d(TAG, "The interstitial wasn't loaded yet(settings_main).");
                    }
                    setColor(Color.GREEN);
                    Intent intent3 = new Intent(context, RLActivity.class);
                    startActivity(intent3);
                    break;
            }
        }
    };

    @Override
    public void onStart(){
        super.onStart();
        setColors(Color.WHITE);
    }

    @Override
    public void onResume(){
        super.onResume();
        setColors(Color.WHITE);
    }

    @Override
    public void onStop(){
        super.onStop();
        setColors(Color.WHITE);
    }

    @Override
    public void onPause(){
        super.onPause();
        setColors(Color.WHITE);
    }
}
