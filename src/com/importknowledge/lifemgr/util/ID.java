package com.importknowledge.lifemgr.util;

public class ID {
    static private int id = 0;

    static public int getId() {
        return id++;
    }
}