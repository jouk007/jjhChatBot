package com.police170m3.rpi.jjhchatbot;

/**
 * Created by JaeHun Jung on 02/10/2017.
 */
public class ChatMessage {
    public boolean left;
    public String text;

    public ChatMessage(boolean left, String text) {
        super();
        this.left = left;
        this.text = text;
    }

}
