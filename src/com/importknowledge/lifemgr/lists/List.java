package com.importknowledge.lifemgr.lists;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.importknowledge.lifemgr.util.ID;
import com.importknowledge.lifemgr.util.MutableString;

public class List extends AbsList{
    private static final long serialVersionUID = 2L;
    public MutableString name = new MutableString("");
    
    double progress;
    int importance;
    
    public boolean persistant = false;

    public List(String name, List holder){
        this.name = new MutableString(name);
        this.holder = holder;
        progress = 0;
        id = ID.getId();
        if(holder != null)
            importance = holder.importance;
        items = new ArrayList<>();
    }

    public List(List list){
        this(list.name.getValue(), list.holder!=null?(List) list.holder:null);
        items = new ArrayList<>(list.items.size());
        list.items.forEach(n->items.add(new List((List) n, this)));
        progress = list.progress;
        importance = list.importance;
        persistant = list.persistant;
        id = list.id;
    }

    public List(List list, List holder){
        this(list.name.getValue(), holder);
        items = new ArrayList<>(list.items.size());
        progress = list.progress;
        importance = list.importance;
        persistant = list.persistant;
        id = list.id;
        list.items.forEach(n->items.add(new List((List) n, this)));
    }

    public void update() {
        if(id == -1) id = ID.getId();
        if(items.size() == 0) return;
        progress = 0;
        items.stream().map(n -> (List) n).forEach(n->n.update());
        progress = items.stream().map(n -> (List) n).collect(Collectors.averagingDouble(n -> n.progress));
        if(items.size() == 0) progress = 1;
    }
    public void check(boolean b){
        items.forEach(n->n.check(b));
        progress = b?1:0;
        update();
    }
    public void clear(){
        items.forEach(n->n.clear());
        if(progress == 1){
            if(persistant) progress = 0;
            else ((List) holder).remove(this);
        }
    }
    public void setpersistant(){
        persistant = true;
        if(holder != null)((List) holder).setpersistant();
    }
    public int level(){
        return holder == null?0:(((List) holder).level()+1);
    }
    public AbsList get(AbsList l1){
        if(l1.id == id){
            return this;
        }else {
            return items.stream().map(n -> n.get(l1)).filter(n -> n != null).findAny().orElse(null);
        }
    }
    public AbsList get(int index){
        return items.get(index);
    }
    public AbsList getFromID(int id){
        if(this.id == id) return this;
        return items.stream().map(n->n.getFromID(id)).filter(n->n!=null).findAny().orElse(null);
    }
    public boolean set(AbsList l1, AbsList l2){
        if(items.contains(l1)){
            items.set(items.indexOf(l1), l2);
            return true;
        }else {
            return items.stream().anyMatch(n->n.set(l1, l2));
        }
    }
    public void addAll(ArrayList<List> e) {
        items.addAll(e);
        items.forEach(n->n.holder = this);
    }
    public void add(AbsList e) {
        items.add(e);
        e.holder = this;
    }
    public void add(int i, AbsList e) {
        items.add(i, e);
        e.holder = this;
    }
    public void addto() {
        List l = new List("", this);
        add(l);
    }
    public boolean addTo(AbsList l1, AbsList l2){
        if(this == l1){
            add(l2);
            l2.holder = this;
            return true;
        }else {
            return items.stream().anyMatch(n -> n.addTo(l1, l2));
        }
    }
    public boolean addTo(AbsList l1, int i, AbsList l2){
        if(this == l1){
            add(i, l2);
            l2.holder = this;
            return true;
        }else {
            return items.stream().anyMatch(n -> n.addTo(l1, i, l2));
        }
    }
    public void remove(AbsList e) {
        items.remove(e);
        update();
    }
    public boolean remFrom(AbsList l1, AbsList l2){
        if(items.contains(l1)){
            get(items.indexOf(l1)).remove(l2);
            return true;
        }else {
            return items.stream().anyMatch(n -> n.remFrom(l1, l2));
        }
    }
    public AbsList getFirst() {
        if(items.size() > 0){
            return items.stream().map(n -> n.getFirst()).filter(n -> n != null).findAny().orElse(null);
        }else if(progress == 0){
            return this;
        }else{
            return null;
        }
    }
    public AbsList prev(){
        if(holder != null){
            if(holder.items.indexOf(this) > 0){
                return holder.get(holder.items.indexOf(this)-1);
            }else {
                return holder;
            }
        }else{
            return getFirst();
        }
    }
    public AbsList getNext(AbsList l) {
        if(this.id == l.id){
            return this.next();
        }else if(items.size() > 0){
            return items.stream().map(n -> n.getNext(l)).filter(n -> n != null).findAny().orElse(null);
        }
        return null;
    }
    public AbsList next(){
        if(items.size() > 0){
            return get(0);
        }else{
            return upnext();
        }
    }
    public AbsList upnext(){
        if(holder != null){
            if(holder.items.indexOf(this) < holder.items.size()-1){
                return holder.get(holder.items.indexOf(this)+1);
            }else{
                return holder.upnext();
            }
        }else{
            return getFirst();
        }
    }
    public void setpriority(int prioty){
        importance = prioty;
        items.forEach(n->((List) n).setpriority(prioty));
    }
    public ArrayList<List> find(int prioty){
        List list = new List(this);
        return (ArrayList<List>) Stream.concat(
            list.items.stream().map(n -> (List) n).filter(n -> n.importance == prioty).map(n->{n.items = new ArrayList<>(); n.find(prioty).forEach(n.items::add); return n;}),
            items.stream().map(n -> (List) n).filter(e -> e.importance != prioty).filter(e -> e.find(prioty).size() != 0).map(n->{n.items = new ArrayList<>(); n.find(prioty).forEach(n.items::add); n.importance = -prioty; return n;})
        ).collect(Collectors.toList());
    }
    public AbsList top(){
        if(holder == null)return this;
        return holder.top();
    }
    public int countit() {
        return items.stream().collect(Collectors.summingInt(n -> n.countit()))+1;
    }
    public int countit(int index) {
        return items.subList(0, index).stream().collect(Collectors.summingInt(n -> n.countit()))+1;
    }
}