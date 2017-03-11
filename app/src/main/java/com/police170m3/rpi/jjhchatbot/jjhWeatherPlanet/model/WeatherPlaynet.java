package com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.model;

import java.util.List;

/**
 * Created by Jaehun on 2017-02-14.
 */

public class WeatherPlaynet {
    private String timeRelease;

    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private Wind wind;
    private Rain rain;
    private Clouds clouds;
    private int dt;
    private Sys sys;
    private int id;
    private String name;
    private int cod;
}
