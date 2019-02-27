package com.importknowledge.lifemgr.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.File;
import java.util.Hashtable;

public class Settings {
	private static Font extracted() {
        try{
            return Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.home")+"/Desktop/LifeMGR/times.ttf"));
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
	}
	private static Font done() {
        Hashtable<TextAttribute, Object> attributes = new Hashtable<TextAttribute, Object>();
        attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        return font.deriveFont(attributes);
	}
	public final static int 
        indent = 15,
        line = 20,
        linespace = 5,
        fontsize = 35,
        buttondist = 300,
        priorityLineLength = 200,
        statsgrid = 3,
        foldLimit = 7;
    public final static double
        scroll = 7.5,
        persistantSize = 1/3.0,
        quickAnimSpeed = 0.04;
    public final static Font
        font = extracted(),
        donefont = done(),
        statsfont = new Font("Ubuntu", Font.PLAIN, 150);
    public final static Color
        priorityLines = new Color(225, 150, 225),
        clarifyLine = Color.LIGHT_GRAY,
        selectedClarifyLine = new Color(140, 140, 140),
        incomplete = new Color(100, 100, 255),
        complete = Color.GREEN,
        persistant = Color.WHITE,
        xColor = Color.RED,
        addColor = Color.GREEN,
        buttonForeground = Color.BLACK,
        text = Color.BLACK,
        selectedtext = Color.BLUE,
        disabledtext = Color.GRAY,
        tabBackground = new Color(240, 240, 240),
        selectedTabBackground = Color.WHITE,
        tabBorder = Color.BLACK;
    public final static String 
        inpath = System.getProperty("user.home")+"/Desktop/LifeMGR/yay.list",
        outpath = System.getProperty("user.home")+"/Desktop/LifeMGR/yay.list",
        iconpath = System.getProperty("user.home")+"/Desktop/LifeMGR/icon.png";
}