package com.storksking.trafficlamps;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RLActivity extends AppCompatActivity {

    private ImageView l1T, l1B, l2T, l2B, l3T, l3B, l4T, l4B, l5T, l5B;
    private CountDownTimer countDownTimer;
    private LinearLayout linearLayout;
    private boolean isOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rl);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        l1T = findViewById(R.id.imageView);
        l1B = findViewById(R.id.imageView2);
        l2T = findViewById(R.id.imageView3);
        l2B = findViewById(R.id.imageView4);
        l3T = findViewById(R.id.imageView5);
        l3B = findViewById(R.id.imageView6);
        l4T = findViewById(R.id.imageView7);
        l4B = findViewById(R.id.imageView8);
        l5T = findViewById(R.id.imageView9);
        l5B = findViewById(R.id.imageView10);
        setColor(Color.DKGRAY);
        linearLayout = findViewById(R.id.main_linear);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOn){
                    stopBlink();
                    isOn=false;
                } else {
                    startLight();
                    isOn=true;
                }
            }
        });

    }

    public void setColor1(int color) {
        l1T.setColorFilter(color);
        l1B.setColorFilter(color);
    }

    public void setColor2(int color) {
        l2T.setColorFilter(color);
        l2B.setColorFilter(color);
    }

    public void setColor3(int color) {
        l3T.setColorFilter(color);
        l3B.setColorFilter(color);
    }

    public void setColor4(int color) {
        l4T.setColorFilter(color);
        l4B.setColorFilter(color);
    }

    public void setColor5(int color) {
        l5T.setColorFilter(color);
        l5B.setColorFilter(color);
    }

    public void setColor(int color) {
        setColor1(color);
        setColor2(color);
        setColor3(color);
        setColor4(color);
        setColor5(color);
    }

    public void startLight() {
        setColor(Color.DKGRAY);
        countDownTimer = new CountDownTimer(10000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished > 8000) {
                    setColor1(Color.RED);
                } else if (millisUntilFinished > 6000) {
                    setColor2(Color.RED);
                } else if (millisUntilFinished > 4000) {
                    setColor3(Color.RED);
                } else if (millisUntilFinished > 2000) {
                    setColor4(Color.RED);
                } else if (millisUntilFinished > 0) {
                    setColor5(Color.RED);
                }
            }
            @Override
            public void onFinish() {
                setColor(Color.GREEN);
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopBlink();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBlink();
    }

    public void stopBlink() {
        try {
            countDownTimer.cancel();
            setColor(Color.DKGRAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
