package com.importknowledge.lifemgr.lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class AbsList implements Serializable {
	private static final long serialVersionUID = 1L;
    public ArrayList<AbsList> items;
    public AbsList holder;
    boolean pseudo = false;
    int id = -1;

    public AbsList(){}
    public AbsList(AbsList list){
        items = new ArrayList<>(list.items);
        if(list.holder != null) holder = list.holder;
        id = list.id;
        pseudo = list.pseudo;
    }

    abstract void clear();
    abstract void check(boolean b);
    abstract AbsList get(AbsList l1);
    abstract AbsList getFromID(int id);
    abstract void remove(AbsList e);
    abstract public void add(int i, AbsList e);
    abstract public void add(AbsList e); 
    public void addAll(ArrayList<AbsList> e) {
        items.addAll(e);
        items.forEach(n->n.holder = this);
    }
    abstract AbsList getFirst();
    abstract AbsList getNext(AbsList l);
    public AbsList child(){
        if(items.size() > 0) return items.get(0).child();
        else return this;
    }
    public AbsList next(){
        return items.size() > 0?get(0):upnext();
        
    }
    public AbsList upnext(){
        if(holder != null){
            if(holder.items.indexOf(this) < holder.items.size()-1) return holder.get(holder.items.indexOf(this)+1);
            else return holder.upnext();
        }else return getFirst();
    }
    abstract boolean set(AbsList l1, AbsList l2);
    abstract void update();
    public AbsList top(){
        return holder == null?this:holder.top();
    }
    public int countit() {
        return items.stream().collect(Collectors.summingInt(n -> n.countit()))+1;
    }
    public int countit(int index) {
        return items.subList(0, index).stream().collect(Collectors.summingInt(n -> n.countit()))+1;
    }

    boolean equals(AbsList list){
        return id == list.id;
    }
    public int level(){
        return holder == null?0:(holder.level()+1);
    }
    public AbsList get(int index){
        return items.get(index);
    }
}