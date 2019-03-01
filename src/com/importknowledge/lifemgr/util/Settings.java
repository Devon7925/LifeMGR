package com.importknowledge.lifemgr.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Hashtable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Settings {
    private static Font extractedFont() {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(fontpath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Font doneFont() {
        Hashtable<TextAttribute, Object> attributes = new Hashtable<TextAttribute, Object>();
        attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        return font.deriveFont(attributes);
    }

    public static int indent = 15, line = 20, linespace = 5, fontsize = 35, buttondist = 300, priorityLineLength = 200,
            statsgrid = 3, foldLimit = 7, width = 700, height = 1000;
    public static double scroll = 7.5, persistantSize = 1 / 3.0, quickAnimSpeed = 0.04;
    public static Color priorityLines = new Color(225, 150, 225), clarifyLine = Color.LIGHT_GRAY,
            selectedClarifyLine = new Color(140, 140, 140), incomplete = new Color(100, 100, 255),
            complete = Color.GREEN, persistant = Color.WHITE, xColor = Color.RED, addColor = Color.GREEN,
            buttonForeground = Color.BLACK, text = Color.BLACK, selectedtext = Color.BLUE, disabledtext = Color.GRAY,
            tabBackground = new Color(240, 240, 240), selectedTabBackground = Color.WHITE, tabBorder = Color.BLACK,
            backgroundColor = Color.WHITE;
    public final static String inpath = System.getProperty("user.home") + "/Desktop/LifeMGR/yay.list",
            outpath = System.getProperty("user.home") + "/Desktop/LifeMGR/yay.list",
            jsonpath = System.getProperty("user.home") + "/Desktop/LifeMGR/settings.json",
            fontpath = System.getProperty("user.home") + "/Desktop/LifeMGR/times.ttf",
            iconpath = System.getProperty("user.home") + "/Desktop/LifeMGR/icon.png";
    public final static Font font = extractedFont(), donefont = doneFont(),
            statsfont = new Font("Ubuntu", Font.PLAIN, 150);

    public static void read() {
        String json = "";
        try {
            json = Files.readString(new File(jsonpath).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        for (Field field : Settings.class.getFields()) {
            String name = field.getName();
            if (jsonObject.has(name)) {
                try {
                    Object value = null;
                    if (field.getType().equals(Integer.TYPE))
                        value = jsonObject.get(name).getAsInt();
                    else if (field.getType().equals(Double.TYPE))
                        value = jsonObject.get(name).getAsDouble();
                    else if (field.getType().equals(Color.class)) {
                        JsonObject color = jsonObject.get(name).getAsJsonObject();
                        value = new Color(color.get("red").getAsInt(), color.get("green").getAsInt(),
                                color.get("blue").getAsInt());
                    } else
                        throw new IllegalArgumentException("Unrecognized Type");
                    field.set(Settings.class.getDeclaredConstructor().newInstance(), value);
                } catch (IllegalArgumentException | IllegalAccessException | InstantiationException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}