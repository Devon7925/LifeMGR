package com;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.stream.Collectors;

class ListInstance extends List{
    private static final long serialVersionUID = 1L;
    
    boolean ordered = true;

    public  ListInstance(List list) {
        super(list);
        items = new ArrayList<>(items.stream().map(n -> new ListInstance(n, this)).collect(Collectors.toList()));
    }

    public ListInstance(List list, ListInstance holder) {
        this(list);
        this.holder = holder;
    }

    public ListInstance(ListInstance list) {
        super(list);
    }
    
    public ListInstance(String name, List holder){
        super(name, holder);
    }

    public void addto() {
        ListInstance l = new ListInstance("", this);
        add(0, l);
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

    public ListInstance prioitysort(){
        List orig = new List(this);
        ListInstance l = new ListInstance(orig.name.getValue(), null);
        for(int i = 0; i <= 9; i++){
            l.addAll(new ArrayList<>(orig.find(i).stream().map(n -> new ListInstance(n)).collect(Collectors.toList())));
        }
        l.unorder();
        return l;
    }

    boolean equals(ListInstance list){
        return id == list.id;
    }

    public int index(int index2){
        int index = index2-1;
        if(index>=0){
            for(int i = 0; i < ((index2>items.size())?items.size():index2); i++){
                index -= ((ListInstance) get(i)).countit();
                if(index < 0){
                    index = i;
                    break;
                }
            }
        }
        return index+1;
    }

    void unorder(){
        ordered = false;
        for (List l : items) {
            ((ListInstance) l).unorder();
        }
    }
    
    List merge(List l){
        if(ordered) return this;
        List list = new List(l);
        list.name = name;
        list.progress = progress;
        list.id = id;
        for (int i = 0; i < items.size(); i++) {
            List elem = list.getFromID(get(i).id);
            if(elem == null){
                list.add(i, get(i++));
            }else{
                list.set(elem, ((ListInstance) get(i)).merge(elem));
            }
        }
        return list;
    }
}