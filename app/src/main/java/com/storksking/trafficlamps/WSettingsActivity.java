package com.storksking.trafficlamps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

public class WSettingsActivity extends AppCompatActivity {

    private static final String TAG = "LOOKING";
    private AdView mAdViewW1, mAdViewW2;
    private PublisherInterstitialAd publisherInterstitialAd;

    private EditText redEditText, greenEditText;
    CheckBox countDownCheckBox, blinkingGreenCheckBox;
    SharedPreferences settingsPreferences;
    SharedPreferences.Editor editor;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsettings);

        TextView policy = findViewById(R.id.policy);
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse("https://docs.google.com/document/d/1OOqwJoz9k_p4t2cIpVqZGGgLS5F6qWdUS4wI-VWhQlE/edit");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        mAdViewW1 = findViewById(R.id.adViewW1);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C208971BC8376D4FDC96E9DC05A7EFA6")
                .build();
        mAdViewW1.loadAd(adRequest);

        mAdViewW2 = findViewById(R.id.adViewW2);
        AdRequest adRequest1 = new AdRequest.Builder()
                .addTestDevice("C208971BC8376D4FDC96E9DC05A7EFA6")
                .build();
        mAdViewW2.loadAd(adRequest1);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        redEditText = findViewById(R.id.red_light_time);
        greenEditText = findViewById(R.id.green_light_time);
        countDownCheckBox = findViewById(R.id.wcountdown_checkbox);
        blinkingGreenCheckBox = findViewById(R.id.blinking_green_checkbox);
        settingsPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = settingsPreferences.edit();
        countDownCheckBox.setChecked(settingsPreferences.getBoolean
                ("wCountdown", true));
        blinkingGreenCheckBox.setChecked(settingsPreferences.getBoolean
                ("wBlinkingGreen", false));

        redEditText.setHint(String.valueOf((int)
                (settingsPreferences.getLong("stopTime", 5000)) / 1000));
        greenEditText.setHint(String.valueOf((int)
                (settingsPreferences.getLong("goTime", 5000)) / 1000));

        redEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                try {
                    if (Integer.parseInt(str) < 1) {
                        str = "1";
                    }
                    if (Integer.parseInt(str) > 300) {
                        str = "300";
                    }

                    redEditText.setHint(str);
                    long time = (long) Integer.parseInt(str) * 1000;
                    WActivity.stopTime = time;
                    editor.putLong("stopTime", time);
                    editor.apply();
                } catch (Exception e) {
                    Log.d(TAG, "myError: " + e);
                }
            }
        });

        greenEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                try {
                    if (Integer.parseInt(str) < 1) {
                        str = "1";
                    }
                    if (Integer.parseInt(str) > 300) {
                        str = "300";
                    }
                    greenEditText.setHint(str);
                    long time = (long) Integer.parseInt(str) * 1000;
                    WActivity.goTime = time;
                    editor.putLong("goTime", time);
                    editor.apply();
                } catch (Exception e) {
                    Log.d(TAG, "myError: " + e);
                }
            }
        });

        countDownCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (countDownCheckBox.isChecked()) {
                    WActivity.wCountdown = true;
                    editor.putBoolean("wCountdown", WActivity.wCountdown);
                    editor.apply();
                } else {
                    WActivity.wCountdown = false;
                    editor.putBoolean("wCountdown", WActivity.wCountdown);
                    editor.apply();
                }
            }
        });
        blinkingGreenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (blinkingGreenCheckBox.isChecked()){
                    WActivity.wBlinkingGreen = true;
                    editor.putBoolean("wBlinkingGreen", WActivity.wBlinkingGreen);
                    editor.apply();
                } else {
                    WActivity.wBlinkingGreen = false;
                    editor.putBoolean("wBlinkingGreen", WActivity.wBlinkingGreen);
                    editor.apply();
                }
            }
        });
    }
}