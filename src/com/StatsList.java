package com;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.stream.Collectors;

class StatsList extends ListInstance{
    private static final long serialVersionUID = 1L;

    public StatsList(List list) {
        super(list);
        items = new ArrayList<>(items.stream().map(n -> new StatsList(n, this)).collect(Collectors.toList()));
    }

    public StatsList(List list, StatsList holder) {
        this(list);
        this.holder = holder;
    }

    public StatsList(StatsList list) {
        super(list);
    }
    
    public StatsList(String name, List holder){
        super(name, holder);
        unorder();
    }

    public void draw(Graphics2D g2, int w, int h){
        int index = 0;
        for(int i = 0; i <= 9; i++){
            if(find(i).size() != 0){
                g2.setColor(Color.BLUE);
                g2.fillOval(index%3*w/3, index/3*h/3, w/3, h/3);
                g2.setColor(Color.GREEN);
                g2.fillArc(index%3*w/3, index/3*h/3, w/3, h/3, 90, (int) (-360*find(i).stream().collect(Collectors.averagingDouble(n->{n.update(); return n.progress;}))));
                Font f = g2.getFont();
                g2.setFont(new Font("Ubuntu", Font.PLAIN, 150));
                g2.setColor(Color.BLACK);
                g2.drawString(i+"", index%3*w/3, (1+index/3)*h/3);
                g2.setFont(f);
                index++;
            }
        }
    }

    List merge(List l){
        return l;
    }
}