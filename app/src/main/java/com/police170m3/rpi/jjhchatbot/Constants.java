package com.police170m3.rpi.jjhchatbot;

/**
 * Created by JaeHun Jung on 02/10/2017.
 */
public final class Constants {

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION_BRAIN_STATUS =
            "com.police170m3.rpi.jjhchatbot.BROADCAST_ACTION_BRAIN_STATUS";
    public static final String BROADCAST_ACTION_WEATHER_STATUS =
            "com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.BROADCAST_ACTION_WEATHER_STATUS";
    public static final String BROADCAST_ACTION_BRAIN_ANSWER =
            "com.police170m3.rpi.jjhchatbot.BROADCAST_ACTION_BRAIN_ANSWER";
    public static final String BROADCAST_ACTION_BRAIN_ANSWER_SUB =
            "com.police170m3.rpi.jjhchatbot.BROADCAST_ACTION_BRAIN_ANSWER_SUB";
    public static final String BROADCAST_ACTION_WEATHER_ANSWER =
            "com.police170m3.rpi.jjhchatbot.BROADCAST_ACTION_WEATHER_ANSWER";
    public static final String BROADCAST_ACTION_LOGGER =
            "com.police170m3.rpi.jjhchatbot.BROADCAST_ACTION_LOGGER";

    // Defines the key for the status "extra" in an Intent
    public static final String EXTRA_BRAIN_STATUS =
            "com.police170m3.rpi.jjhchatbot.BRAIN_STATUS";
    public static final String EXTRA_WEATHER_STATUS =
            "com.police170m3.rpi.jjhchatbot.jjhWeatherPlanet.BRAIN_STATUS";
    public static final String EXTRA_BRAIN_ANSWER =
            "com.police170m3.rpi.jjhchatbot.EXTRA_BRAIN_ANSWER";
    public static final String EXTRA_TASK_ANSWER =
            "com.police170m3.rpi.jjhchatbot.EXTRA_TASK_ANSWER";
    public static final String EXTRA_BRAIN_ANSWER_SUB =
            "com.police170m3.rpi.jjhchatbot.EXTRA_BRAIN_ANSWER_SUB";
    public static final String EXTRA_WEATHER_ANSWER =
            "com.police170m3.rpi.jjhchatbot.EXTRA_WEATHER_ANSWER";
    public static final String EXTRA_TMAP_ANSWER =
            "com.police170m3.rpi.jjhchatbot.EXTRA_TMAP_ANSWER";

    public static final int STATUS_BRAIN_LOADING = -1;
    public static final int STATUS_BRAIN_LOADED = 1;

    public static final String EXTENDED_LOGGER_INFO =
            "com.police170m3.rpi.jjhchatbot.LOGGER_INFO";
    public static final String myword="myword";
    public static final String myanswer="myanswer";

}
