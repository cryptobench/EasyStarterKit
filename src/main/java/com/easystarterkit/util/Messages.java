package com.easystarterkit.util;

import com.hypixel.hytale.server.core.Message;

import java.awt.Color;

public class Messages {

    private static final Color GREEN = new Color(85, 255, 85);
    private static final Color RED = new Color(255, 85, 85);
    private static final Color YELLOW = new Color(255, 255, 85);
    private static final Color GOLD = new Color(255, 170, 0);
    private static final Color GRAY = new Color(170, 170, 170);
    private static final Color AQUA = new Color(85, 255, 255);

    private static final String PREFIX = "[StarterKit] ";

    public static Message success(String text) {
        return Message.raw(PREFIX + text).color(GREEN);
    }

    public static Message error(String text) {
        return Message.raw(PREFIX + text).color(RED);
    }

    public static Message warning(String text) {
        return Message.raw(PREFIX + text).color(YELLOW);
    }

    public static Message info(String text) {
        return Message.raw(PREFIX + text).color(AQUA);
    }

    public static Message gray(String text) {
        return Message.raw(text).color(GRAY);
    }
}
