package com.importknowledge.lifemgr.lists;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.importknowledge.lifemgr.util.Settings;

public class ListInstance extends AbsList{
    private static final long serialVersionUID = 1L;
	public List orig;
    
    boolean ordered = true;

    public  ListInstance(AbsList list, List orig) {
        super(list);
        this.orig = orig;
    }

    public  ListInstance(List list, List orig) {
        super(list);
        items = new ArrayList<>(list.items.stream().map(n->new ListInstance((List) n, orig)).collect(Collectors.toList()));
        this.orig = orig;
    }

    public  ListInstance(AbsList list) {
        super(list);
    }

    public  ListInstance(List list) {
        this((AbsList) list, list);
        items = new ArrayList<>(list.items.stream().map(n->new ListInstance((List) n)).collect(Collectors.toList()));
    }

    public ListInstance(AbsList list, ListInstance holder) {
        this(list, holder.correct());
        this.holder = holder;
    }

    public ListInstance(ListInstance list) {
        super(list);
    }

    public void set(Graphics2D g2, int x, int y, List l) {
        int index = Math.floorDiv(y, l.name.lineheight(g2)+Settings.line);
        index = index(index);
        if(index < 0){
            return;
        }else if(index == 0){
            if(holder.items.indexOf(this) != -1) holder.items.set(holder.items.indexOf(this), l);
        }else if(index > items.size()){
            return;
        }else{
            ((ListInstance) get(--index)).set(g2, x-Settings.indent, (y-(countit(index))*(l.name.lineheight(g2)+Settings.line)), l);
        }
    }

    boolean equals(ListInstance list){
        return id == list.id;
    }

    public int index(int index2){
        int index = index2-1;
        if(index>=0){
            for(int i = 0; i < ((index2>items.size())?items.size():index2); i++){
                index -= get(i).countit();
                if(index < 0){
                    index = i;
                    break;
                }
            }
        }
        return index+1;
    }

    ListInstance unorder(){
        ordered = false;
        for (AbsList l : items) {
            ((ListInstance) l).unorder();
        }
        return this;
    }

    @Override
    AbsList get(int index) {
        return items.get(index);
    }

    @Override
	public
    void clear() {
        correct().clear();
    }

    @Override
    void check(boolean b) {
        correct().check(b);
    }

    @Override
    AbsList get(AbsList l1) {
        return correct().get(l1);
    }

    @Override
    ListInstance getFromID(int id) {
        if(this.id == id) return this;
        return items.stream().map(n -> (ListInstance) n).map(n->n.getFromID(id)).filter(n->n!=null).findAny().orElse(null);
    }

    @Override
    public void remove(AbsList e) {
        correct().remove(((ListInstance) e).correct());
        items.remove(e);
        update();
    }

    @Override
    boolean remFrom(AbsList l1, AbsList l2) {
        return correct().remFrom(l1, l2);
    }

    @Override
    boolean addTo(AbsList l1, int i, AbsList l2) {
        return correct().addTo(l1, i, l2);
    }

    @Override
    boolean addTo(AbsList l1, AbsList l2) {
        return correct().addTo(l1, l2);
    }

    @Override
    public void add(int i, AbsList e) {
        correct().add(i, e);
    }

    @Override
    public void add(AbsList e) {
        correct().add(e);
    }

    @Override
    AbsList getFirst() {
        if(items.size() > 0){
            return items.stream().map(n -> n.getFirst()).filter(n -> n != null).findFirst().orElse(null);
        }else if(correct().progress == 0){
            return this;
        }else{
            return null;
        }
    }

    @Override
    AbsList getNext(AbsList l) {
        if(this.id == l.id){
            return this.next();
        }else if(items.size() > 0){
            return items.stream().map(n -> n.getNext(l)).filter(n -> n != null).findAny().orElse(null);
        }
        return null;
    }

    @Override
    boolean set(AbsList l1, AbsList l2) {
        return correct().set(l1, l2);
    }

    @Override
	public
    void update() {
        correct().update();
    }
    public List correct(){
        return ((List) orig.top().getFromID(id));
    }
}