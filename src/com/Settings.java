package com;

import java.awt.Font;
import java.io.File;

class Settings {
	private static Font extracted() {
        try{
        return Font.createFont(Font.TRUETYPE_FONT, new File("C:/Users/Public/eclipse-workspace/LifeMGR/times.ttf"));
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
	}
	static int 
        indent = 15,
        line = 20,
        linespace = 5,
        fontsize = 35,
        buttondist = 300;
    static double
        scroll = 7.5;
    static Font
        font = extracted();
}