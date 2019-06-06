package com.storksking.trafficlamps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

public class WActivity extends AppCompatActivity {

    static long stopTime, goTime;
    static boolean wCountdown, wBlinkingGreen;

    private PublisherInterstitialAd publisherInterstitialAd;
    private static final String TAG_1 = "KINGL";

    private ImageView redImageView, greenImageView, stopWalk, goWalk;
    private TextView stopTextView, goTextView;
    private CountDownTimer stopTimer, goTimer;

    SharedPreferences settingsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        publisherInterstitialAd = new PublisherInterstitialAd(this);
        publisherInterstitialAd.setAdUnitId("ca-app-pub-4249365726191634/1199952092");
        publisherInterstitialAd.loadAd(new PublisherAdRequest.Builder()
                .addTestDevice("C208971BC8376D4FDC96E9DC05A7EFA6")
                .build());

        settingsPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        stopTime = settingsPreferences.getLong("stopTime", 5000);
        goTime = settingsPreferences.getLong("goTime", 5000);
        wCountdown = settingsPreferences.getBoolean("wCountdown", true);
        wBlinkingGreen = settingsPreferences.getBoolean("wBlinkingGreen", false);

        redImageView = findViewById(R.id.redImageView);
        stopWalk = findViewById(R.id.redWImageView);
        greenImageView = findViewById(R.id.greenImageView);
        goWalk = findViewById(R.id.greenWImageView);
        redImageView.setVisibility(View.VISIBLE);
        redImageView.setColorFilter(Color.DKGRAY);
        greenImageView.setVisibility(View.VISIBLE);
        greenImageView.setColorFilter(Color.DKGRAY);

        stopWalk.setColorFilter(Color.RED);
        stopWalk.setVisibility(View.INVISIBLE);
        goWalk.setColorFilter(Color.GREEN);
        goWalk.setVisibility(View.INVISIBLE);

        stopTextView = findViewById(R.id.red_time_view);
        stopTextView.setTextColor(Color.RED);
        goTextView = findViewById(R.id.green_time_view);
        goTextView.setTextColor(Color.GREEN);
        stopTextView.setVisibility(View.INVISIBLE);
        goTextView.setVisibility(View.INVISIBLE);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        long h = metrics.heightPixels;
        if (h <= 800) {
            stopTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (160));
            goTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (160));
        } else if (h <= 1280) {
            stopTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (200));
            goTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (200));
        } else if (h <= 1920) {
            stopTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (204));
            goTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (204));
        } else {
            stopTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (207));
            goTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (207));
        }

        redImageView.setClickable(true);
        greenImageView.setClickable(true);
        redImageView.setOnClickListener(onClickListener);
        greenImageView.setOnClickListener(onClickListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.auto_button:
                stopBlink();
                redImageView.setClickable(false);
                greenImageView.setClickable(false);
                auto();
                break;
            case R.id.manual_button:
                stopBlink();
                redImageView.setClickable(true);
                greenImageView.setClickable(true);
                break;
            case R.id.settings_button:
                stopBlink();
                if (publisherInterstitialAd.isLoaded()) {
                    publisherInterstitialAd.show();
                } else {
                    Log.d(TAG_1, "The interstitial wasn't loaded yet(WActivity_settings).");
                }
                Intent intent = new Intent(this, WSettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void auto() {
        stopWalk.setVisibility(View.INVISIBLE);
        goWalk.setVisibility(View.INVISIBLE);
        stopBlink();
        blink();
    }

    private void stopBlink() {
        try {
            stopTimer.cancel();
            stopWalk.setVisibility(View.INVISIBLE);
            stopTextView.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            goTimer.cancel();
            goWalk.setVisibility(View.INVISIBLE);
            goTextView.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void blink() {
        stopTimer = new CountDownTimer(stopTime, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                stopWalk.setVisibility(View.VISIBLE);
                if (wCountdown) {
                    if (millisUntilFinished < 99000) {
                        stopTextView.setVisibility(View.VISIBLE);
                        stopTextView.setText(String.valueOf((int)
                                (millisUntilFinished + 1000) / 1000));
                    } else {
                        stopTextView.setVisibility(View.VISIBLE);
                        stopTextView.setText(">" + String.valueOf((int)
                                (millisUntilFinished + 1000) / 60000));
                    }
                }
            }

            @Override
            public void onFinish() {
                stopWalk.setVisibility(View.INVISIBLE);
                stopTextView.setVisibility(View.INVISIBLE);
                goTimer = new CountDownTimer(goTime, 1) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        goWalk.setVisibility(View.VISIBLE);
                        if (wBlinkingGreen) {
                            if ((millisUntilFinished) < 3000) {
                                if ((int) ((millisUntilFinished / 500) % 2) == 0) {
                                    goWalk.setVisibility(View.VISIBLE);
                                } else {
                                    goWalk.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        if (wCountdown) {
                            if (millisUntilFinished < 99000) {
                                goTextView.setVisibility(View.VISIBLE);
                                goTextView.setText(String.valueOf((int)
                                        (millisUntilFinished + 1000) / 1000));
                            } else {
                                goTextView.setVisibility(View.VISIBLE);
                                goTextView.setText(">" + String.valueOf((int)
                                        (millisUntilFinished + 1000) / 60000));
                            }
                        }
                    }

                    @Override
                    public void onFinish() {
                        goWalk.setVisibility(View.INVISIBLE);
                        goTextView.setVisibility(View.INVISIBLE);
                        blink();
                    }
                };
                goTimer.start();
            }
        };
        stopTimer.start();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.redImageView:
                    stopWalk.setVisibility(View.VISIBLE);
                    goWalk.setVisibility(View.INVISIBLE);
                    break;

                case R.id.greenImageView:
                    stopWalk.setVisibility(View.INVISIBLE);
                    goWalk.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
}