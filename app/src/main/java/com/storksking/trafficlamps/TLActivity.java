package com.storksking.trafficlamps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class TLActivity extends AppCompatActivity {

    static long redTime, yellowTime, greenTime;
    static boolean countdown, smile, yellowWithRed, redAfterGreen, blinkingGreen;

    SharedPreferences settingsPreferences;

    private InterstitialAd mInterstitialAd;

    private CountDownTimer redTimer, yellowTimerRed, yellowTimerGreen, greenTimer;
    private static final String TAG = "LOOKING";
    private boolean auto = false;
    private ImageView redSmile, yellowSmile, greenSmile;
    private TrafficLights red, yellow, green;
    private TextView timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tl);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-4249365726191634/1199952092", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    Log.d("TAG", "The ad was shown.");
                }
            });
        }

        settingsPreferences =
                getSharedPreferences("Settings", Context.MODE_PRIVATE);
        redTime = settingsPreferences.getLong("redTime", 4000);
        yellowTime = settingsPreferences.getLong("yellowTime", 2000);
        greenTime = settingsPreferences.getLong("greenTime", 4000);
        countdown = settingsPreferences.getBoolean("countdown", true);
        smile = settingsPreferences.getBoolean("smile", false);
        yellowWithRed = settingsPreferences.getBoolean("yellowWithRed", false);
        redAfterGreen = settingsPreferences.getBoolean("redAfterGreen", false);
        blinkingGreen = settingsPreferences.getBoolean("blinkingGreen", false);

        ImageView redLight = findViewById(R.id.redImageView);
        redLight.setVisibility(View.VISIBLE);
        redLight.setOnClickListener(onClickListener);

        ImageView yellowLight = findViewById(R.id.yellowImageView);
        yellowLight.setVisibility(View.VISIBLE);
        yellowLight.setOnClickListener(onClickListener);

        ImageView greenLight = findViewById(R.id.greenImageView);
        greenLight.setVisibility(View.VISIBLE);
        greenLight.setOnClickListener(onClickListener);

        redSmile = findViewById(R.id.redSmileImageView);
        yellowSmile = findViewById(R.id.yellowSmileImageView);
        greenSmile = findViewById(R.id.greenSmileImageView);
        redSmile.setVisibility(View.INVISIBLE);
        yellowSmile.setVisibility(View.INVISIBLE);
        greenSmile.setVisibility(View.INVISIBLE);

        timeView = findViewById(R.id.time_view);
        timeView.setVisibility(View.INVISIBLE);
        timeView.setTextColor(Color.RED);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        long h = metrics.heightPixels;
        if (h <= 800) {
            timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (110));
        } else if (h <= 1280) {
            timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (131));
        } else if (h <= 1920) {
            timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (135));
        } else {
            timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (140));
        }

        red = new TrafficLights(redLight, Color.RED, redTime);
        yellow = new TrafficLights(yellowLight, Color.YELLOW, yellowTime);
        green = new TrafficLights(greenLight, Color.GREEN, greenTime);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        auto = false;
        stopBlink();
    }

    @Override
    public void onPause() {
        super.onPause();
        auto = false;
        stopBlink();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.auto_button:
                auto = true;
                auto();
                break;
            case R.id.manual_button:
                auto = false;
                stopBlink();
                red.setClickable(true);
                yellow.setClickable(true);
                green.setClickable(true);
                break;
            case R.id.settings_button:
                stopBlink();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(TLActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void auto() {
        red.stopLight();
        yellow.stopLight();
        green.stopLight();
        red.setClickable(false);
        yellow.setClickable(false);
        green.setClickable(false);

        stopBlink();
        blink();
    }

    void stopBlink() {
        timeView.setVisibility(View.INVISIBLE);
        try {
            redTimer.cancel();
            red.stopLight();
            redSmile.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.d(TAG, "Error_redTimer: " + e);
        }

        try {
            yellowTimerRed.cancel();
            yellow.stopLight();
            yellowSmile.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.d(TAG, "Error_yellowTimerRed: " + e);
        }

        try {
            greenTimer.cancel();
            green.stopLight();
            greenSmile.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.d(TAG, "Error_greenTimer: " + e);
        }

        try {
            yellowTimerGreen.cancel();
            yellow.stopLight();
            yellowSmile.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.d(TAG, "Error_yellowTimerGreen: " + e);
        }
    }

    void blink() {
        if (yellowWithRed) {
            redTimer = new CountDownTimer(redTime, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    red.startLight();
                    if (smile) {
                        redSmile.setVisibility(View.VISIBLE);
                    }
                    if (countdown) {
                        if ((millisUntilFinished + yellowTime) < 99000) {
                            timeView.setText(String.valueOf((int) (millisUntilFinished +
                                    yellowTime + 1000) / 1000));
                            timeView.setTextColor(Color.RED);
                            timeView.setVisibility(View.VISIBLE);
                        } else {
                            timeView.setText(">" + String.valueOf((int) (millisUntilFinished +
                                    yellowTime + 1000) / 60000));
                            timeView.setTextColor(Color.RED);
                            timeView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFinish() {
                    red.stopLight();
                    redSmile.setVisibility(View.INVISIBLE);
                    timeView.setVisibility(View.INVISIBLE);

                    yellowTimerRed = new CountDownTimer(yellowTime, 1) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            yellow.startLight();
                            red.startLight();
                            if (smile) {
                                yellowSmile.setVisibility(View.VISIBLE);
                                redSmile.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFinish() {
                            red.stopLight();
                            yellow.stopLight();
                            yellowSmile.setVisibility(View.INVISIBLE);
                            redSmile.setVisibility(View.INVISIBLE);
                            greenTimer = new CountDownTimer(greenTime, 1) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    green.startLight();
                                    if (blinkingGreen) {
                                        if ((millisUntilFinished) < 3000) {
                                            if ((int) ((millisUntilFinished / 500) % 2) == 0) {
                                                green.startLight();
                                                if (smile) {
                                                    greenSmile.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                green.stopLight();
                                                greenSmile.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                    if (countdown) {
                                        if ((millisUntilFinished) < 99000) {
                                            timeView.setText(String.valueOf((int)
                                                    (millisUntilFinished + 1000) / 1000));
                                            timeView.setTextColor(Color.GREEN);
                                            timeView.setVisibility(View.VISIBLE);
                                        } else {
                                            timeView.setText(">" + String.valueOf((int)
                                                    (millisUntilFinished + 1000) / 60000));
                                            timeView.setTextColor(Color.GREEN);
                                            timeView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    if (smile) {
                                        greenSmile.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    green.stopLight();
                                    timeView.setVisibility(View.INVISIBLE);
                                    greenSmile.setVisibility(View.INVISIBLE);
                                    yellowTimerGreen = new CountDownTimer(yellowTime,
                                            1) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            yellow.startLight();
                                            if (smile) {
                                                yellowSmile.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onFinish() {
                                            yellow.stopLight();
                                            yellowSmile.setVisibility(View.INVISIBLE);
                                            blink();
                                        }
                                    };
                                    yellowTimerGreen.start();
                                }
                            };
                            greenTimer.start();
                        }
                    };
                    yellowTimerRed.start();
                }
            };
            redTimer.start();
        } else if (redAfterGreen) {
            redTimer = new CountDownTimer(redTime + yellowTime,
                    1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    red.startLight();
                    if (smile) {
                        redSmile.setVisibility(View.VISIBLE);
                    }
                    if (countdown) {
                        if ((millisUntilFinished) < 99000) {
                            timeView.setText(String.valueOf((int)
                                    (millisUntilFinished + 1000) / 1000));
                            timeView.setTextColor(Color.RED);
                            timeView.setVisibility(View.VISIBLE);
                        } else {
                            timeView.setText(">" + String.valueOf((int)
                                    (millisUntilFinished + 1000) / 60000));
                            timeView.setTextColor(Color.RED);
                            timeView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFinish() {
                    red.stopLight();
                    redSmile.setVisibility(View.INVISIBLE);
                    timeView.setVisibility(View.INVISIBLE);
                    greenTimer = new CountDownTimer(greenTime, 1) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            green.startLight();
                            if (blinkingGreen) {
                                if ((millisUntilFinished) < 3000) {
                                    if ((int) ((millisUntilFinished / 500) % 2) == 0) {
                                        green.startLight();
                                        if (smile) {
                                            greenSmile.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        green.stopLight();
                                        greenSmile.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                            if (countdown) {
                                if ((millisUntilFinished) < 99000) {
                                    timeView.setText(String.valueOf((int)
                                            (millisUntilFinished + 1000) / 1000));
                                    timeView.setTextColor(Color.GREEN);
                                    timeView.setVisibility(View.VISIBLE);
                                } else {
                                    timeView.setText(">" + String.valueOf((int)
                                            (millisUntilFinished + 1000) / 60000));
                                    timeView.setTextColor(Color.GREEN);
                                    timeView.setVisibility(View.VISIBLE);
                                }
                            }
                            if (smile) {
                                greenSmile.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFinish() {
                            green.stopLight();
                            greenSmile.setVisibility(View.INVISIBLE);
                            timeView.setVisibility(View.INVISIBLE);
                            yellowTimerGreen = new CountDownTimer(yellowTime,
                                    1) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    yellow.startLight();
                                    if (smile) {
                                        yellowSmile.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    yellow.stopLight();
                                    yellowSmile.setVisibility(View.INVISIBLE);
                                    blink();
                                }
                            };
                            yellowTimerGreen.start();
                        }
                    };
                    greenTimer.start();
                }
            };
            redTimer.start();
        } else {
            redTimer = new CountDownTimer(redTime, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    red.startLight();
                    if (smile) {
                        redSmile.setVisibility(View.VISIBLE);
                    }
                    if (countdown) {
                        if ((millisUntilFinished) < 99000) {
                            timeView.setText(String.valueOf((int)
                                    (millisUntilFinished + 1000) / 1000));
                            timeView.setTextColor(Color.RED);
                            timeView.setVisibility(View.VISIBLE);
                        } else {
                            timeView.setText(">" + String.valueOf((int)
                                    (millisUntilFinished + 1000) / 60000));
                            timeView.setTextColor(Color.RED);
                            timeView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFinish() {
                    red.stopLight();
                    redSmile.setVisibility(View.INVISIBLE);
                    timeView.setVisibility(View.INVISIBLE);
                    yellowTimerRed = new CountDownTimer(yellowTime, 1) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            yellow.startLight();
                            if (smile) {
                                yellowSmile.setVisibility(View.VISIBLE);
                                redSmile.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFinish() {
                            yellow.stopLight();
                            yellowSmile.setVisibility(View.INVISIBLE);
                            greenTimer = new CountDownTimer(greenTime, 1) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    green.startLight();
                                    if (blinkingGreen) {
                                        if ((millisUntilFinished) < 3000) {
                                            if ((int) ((millisUntilFinished / 500) % 2) == 0) {
                                                green.startLight();
                                                if (smile) {
                                                    greenSmile.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                green.stopLight();
                                                greenSmile.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                    if (countdown) {
                                        if ((millisUntilFinished) < 99000) {
                                            timeView.setText(String.valueOf((int)
                                                    (millisUntilFinished + 1000) / 1000));
                                            timeView.setTextColor(Color.GREEN);
                                            timeView.setVisibility(View.VISIBLE);
                                        } else {
                                            timeView.setText(">" + String.valueOf((int)
                                                    (millisUntilFinished + 1000) / 60000));
                                            timeView.setTextColor(Color.GREEN);
                                            timeView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    if (smile) {
                                        greenSmile.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    green.stopLight();
                                    greenSmile.setVisibility(View.INVISIBLE);
                                    timeView.setVisibility(View.INVISIBLE);
                                    yellowTimerGreen = new CountDownTimer(yellowTime,
                                            1) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            yellow.startLight();
                                            if (smile) {
                                                yellowSmile.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onFinish() {
                                            yellow.stopLight();
                                            yellowSmile.setVisibility(View.INVISIBLE);
                                            blink();
                                        }
                                    };
                                    yellowTimerGreen.start();
                                }
                            };
                            greenTimer.start();
                        }
                    };
                    yellowTimerRed.start();
                }
            };
            redTimer.start();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.redImageView:
                    red.startLight();
                    yellow.stopLight();
                    green.stopLight();
                    if (smile) {
                        redSmile.setVisibility(View.VISIBLE);
                        yellowSmile.setVisibility(View.INVISIBLE);
                        greenSmile.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.id.yellowImageView:
                    red.stopLight();
                    yellow.startLight();
                    green.stopLight();
                    if (smile) {
                        redSmile.setVisibility(View.INVISIBLE);
                        yellowSmile.setVisibility(View.VISIBLE);
                        greenSmile.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.id.greenImageView:
                    red.stopLight();
                    yellow.stopLight();
                    green.startLight();
                    if (smile) {
                        redSmile.setVisibility(View.INVISIBLE);
                        yellowSmile.setVisibility(View.INVISIBLE);
                        greenSmile.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };
}
