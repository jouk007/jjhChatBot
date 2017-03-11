package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.model;

/**
 * Created by Jaehun on 2017-02-02.
 */

public class Temperature {

    private double temp;
    private float minTemp;
    private float maxTemp;

    public double getTemp() {
        double truncatedTemp = Math.floor(100 * temp) / 100;
        return truncatedTemp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }
}
