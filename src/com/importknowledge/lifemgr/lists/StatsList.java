package com.importknowledge.lifemgr.lists;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.importknowledge.lifemgr.util.Settings;

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
            if(correct().find(i, true).size() != 0){
                g2.setColor(Settings.incomplete);
                g2.fillOval(index%Settings.statsgrid*w/Settings.statsgrid, index/Settings.statsgrid*h/Settings.statsgrid, w/Settings.statsgrid, h/Settings.statsgrid);
                g2.setColor(Settings.complete);
                g2.fillArc(index%Settings.statsgrid*w/Settings.statsgrid, index/Settings.statsgrid*h/Settings.statsgrid, w/Settings.statsgrid, h/Settings.statsgrid, 90, (int) (
                    -360*correct().find(i, true).stream()
                        .peek(n->n.update())
                        .collect(Collectors.averagingDouble(n->n.progress))
                ));
                Font f = g2.getFont();
                g2.setFont(Settings.statsfont);
                g2.setColor(Settings.text);
                g2.drawString(i+"", index%Settings.statsgrid*w/Settings.statsgrid, (1+index/Settings.statsgrid)*h/Settings.statsgrid);
                g2.setFont(f);
                index++;
            }
        }
    }

    public List merge(List l){
        return l;
    }
}