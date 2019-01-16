package com.importknowledge.lifemgr.lists;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StatsList extends ListInstance{
    private static final long serialVersionUID = 1L;

    public StatsList(AbsList list, List orig) {
        super(list, orig);
        items = new ArrayList<>(items.stream().map(n -> new StatsList((List) n, this)).collect(Collectors.toList()));
    }

    public StatsList(List list, StatsList holder) {
        super(list, holder);
    }

    public StatsList(StatsList list) {
        super(list);
    }

    public void draw(Graphics2D g2, int w, int h){
        int index = 0;
        for(int i = 1; i <= 10; i++){
            if(correct().find(i).size() != 0){
                g2.setColor(Color.BLUE);
                g2.fillOval(index%3*w/3, index/3*h/3, w/3, h/3);
                g2.setColor(Color.GREEN);
                g2.fillArc(index%3*w/3, index/3*h/3, w/3, h/3, 90, (int) (-360*correct().find(i).stream().peek(n->n.update()).collect(Collectors.averagingDouble(n->n.progress))));
                Font f = g2.getFont();
                g2.setFont(new Font("Ubuntu", Font.PLAIN, 150));
                g2.setColor(Color.BLACK);
                g2.drawString(i+"", index%3*w/3, (1+index/3)*h/3);
                g2.setFont(f);
                index++;
            }
        }
    }

    public List merge(List l){
        return l;
    }
}