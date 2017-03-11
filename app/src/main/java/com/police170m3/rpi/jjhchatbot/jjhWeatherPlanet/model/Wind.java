package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.model;

/**
 * Created by Jaehun on 2017-02-02.
 */

public class Wind {
    private float speed;
    private float degree;

    public float getDegree(float deg) {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
