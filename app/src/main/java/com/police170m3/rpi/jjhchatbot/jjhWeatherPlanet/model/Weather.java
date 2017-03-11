package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.model;

/**
 * Created by Jaehun on 2017-02-02.
 */

public class Weather {
    public Place place;
    public String iconData;
    public CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();
}
