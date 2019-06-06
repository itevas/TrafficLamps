package com.storksking.trafficlamps;

import android.graphics.Color;
import android.widget.ImageView;

class TrafficLights{

    ImageView light;
    long time;
    int color;
    private int defaultColor = Color.DKGRAY;

    TrafficLights (ImageView view, int color, long time){
        light = view;
        this.color = color;
        this.time = time;
        light.setColorFilter(defaultColor);

    }
    void startLight(){
        light.setColorFilter(color);
    }

    void stopLight(){
        light.setColorFilter(defaultColor);
    }

    void setClickable(boolean clickable){
        light.setClickable(clickable);
    }
}