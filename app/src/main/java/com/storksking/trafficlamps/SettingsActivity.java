package com.storksking.trafficlamps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "LOOKING";
    private AdView mAdView;

    private EditText redEditText, yellowEditText, greenEditText;
    CheckBox countDownCheckBox, smileCheckBox, yellowWithRedCheckBox, redAfterGreenCheckBox,
            blinkingGreenCheckBox;
    SharedPreferences settingsPreferences;
    SharedPreferences.Editor editor;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);

        mAdView = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice("C208971BC8376D4FDC96E9DC05A7EFA6")
                .build();
        mAdView.loadAd(adRequest2);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        redEditText = findViewById(R.id.red_light_time);
        yellowEditText = findViewById(R.id.yellow_light_time);
        greenEditText = findViewById(R.id.green_light_time);
        countDownCheckBox = findViewById(R.id.countdown_checkbox);
        smileCheckBox = findViewById(R.id.smile_checkbox);
        yellowWithRedCheckBox = findViewById(R.id.yellow_with_red_checkbox);
        redAfterGreenCheckBox = findViewById(R.id.red_after_green_checkbox);
        blinkingGreenCheckBox = findViewById(R.id.blik_green);

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

        settingsPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = settingsPreferences.edit();
        countDownCheckBox.setChecked(settingsPreferences.
                getBoolean("countdown", true));
        smileCheckBox.setChecked(settingsPreferences.getBoolean("smile", false));
        yellowWithRedCheckBox.setChecked(settingsPreferences.
                getBoolean("yellowWithRed", false));
        redAfterGreenCheckBox.setChecked(settingsPreferences.
                getBoolean("redAfterGreen", false));
        blinkingGreenCheckBox.setChecked(settingsPreferences.
                getBoolean("blinkingGreen", false));

        redEditText.setHint(String.valueOf((int)
                (settingsPreferences.getLong("redTime", 5000)) / 1000));
        yellowEditText.setHint(String.valueOf((int)
                (settingsPreferences.getLong("yellowTime", 2000)) / 1000));
        greenEditText.setHint(String.valueOf((int)
                (settingsPreferences.getLong("greenTime", 5000)) / 1000));

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
                    TLActivity.redTime = time;
                    editor.putLong("redTime", time);
                    editor.apply();
                } catch (Exception e) {
                    Log.d(TAG, "myError: " + e);
                }
            }
        });

        yellowEditText.addTextChangedListener(new TextWatcher() {
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
                    yellowEditText.setHint(str);
                    long time = (long) Integer.parseInt(str) * 1000;
                    TLActivity.yellowTime = time;
                    editor.putLong("yellowTime", time);
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
                    TLActivity.greenTime = time;
                    editor.putLong("greenTime", time);
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
                    TLActivity.countdown = true;
                    editor.putBoolean("countdown", TLActivity.countdown);
                    editor.apply();
                } else {
                    TLActivity.countdown = false;
                    editor.putBoolean("countdown", TLActivity.countdown);
                    editor.apply();
                }
            }
        });

        smileCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (smileCheckBox.isChecked()) {
                    TLActivity.smile = true;
                    editor.putBoolean("smile", TLActivity.smile);
                    editor.apply();
                } else {
                    TLActivity.smile = false;
                    editor.putBoolean("smile", TLActivity.smile);
                    editor.apply();
                }
            }
        });

        yellowWithRedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (yellowWithRedCheckBox.isChecked()) {
                    TLActivity.yellowWithRed = true;
                    redAfterGreenCheckBox.setChecked(false);
                    editor.putBoolean("yellowWithRed", true);
                    editor.apply();
                } else {
                    TLActivity.yellowWithRed = false;
                    editor.putBoolean("yellowWithRed", TLActivity.yellowWithRed);
                    editor.apply();
                }
            }
        });

        redAfterGreenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (redAfterGreenCheckBox.isChecked()) {
                    yellowWithRedCheckBox.setChecked(false);
                    TLActivity.redAfterGreen = true;
                    editor.putBoolean("redAfterGreen", TLActivity.redAfterGreen);
                    editor.apply();
                } else {
                    TLActivity.redAfterGreen = false;
                    editor.putBoolean("redAfterGreen", TLActivity.redAfterGreen);
                    editor.apply();
                }
            }
        });
        blinkingGreenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (blinkingGreenCheckBox.isChecked()) {
                    TLActivity.blinkingGreen = true;
                    editor.putBoolean("blinkingGreen", TLActivity.blinkingGreen);
                    editor.apply();
                } else {
                    TLActivity.blinkingGreen = false;
                    editor.putBoolean("blinkingGreen", TLActivity.blinkingGreen);
                    editor.apply();
                }
            }
        });
    }
}