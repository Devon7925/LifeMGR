package com.importknowledge.lifemgr.util;

import java.awt.Font;
import java.io.File;

public class Settings {
	private static Font extracted() {
        try{
            return Font.createFont(Font.TRUETYPE_FONT, new File("C:/Users/Public/eclipse-workspace/LifeMGR/times.ttf"));
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
	}
	public final static int 
        indent = 15,
        line = 20,
        linespace = 5,
        fontsize = 35,
        buttondist = 300;
    public final static double
        scroll = 7.5;
    public final static Font
        font = extracted();
}