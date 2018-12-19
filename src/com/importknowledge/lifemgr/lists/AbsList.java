package com.importknowledge.lifemgr.lists;

import java.util.ArrayList;
import java.io.Serializable;

public abstract class AbsList implements Serializable {
	private static final long serialVersionUID = 1L;
    public ArrayList<AbsList> items;
    public AbsList holder;
    int id = -1;

    abstract AbsList get(int index);
    abstract void clear();
    abstract void check(boolean b);
    abstract AbsList get(AbsList l1);
    abstract AbsList getFromID(int id);
    abstract void remove(AbsList e);
    abstract boolean remFrom(AbsList l1, AbsList l2);
    abstract boolean addTo(AbsList l1, int i, AbsList l2);
    abstract boolean addTo(AbsList l1, AbsList l2);
    abstract public void add(int i, AbsList e);
    abstract public void add(AbsList e);
    abstract AbsList getFirst();
    abstract AbsList getNext(AbsList l);
    abstract AbsList upnext();
    abstract boolean set(AbsList l1, AbsList l2);
    abstract AbsList top();
    abstract int countit();
}