package com.importknowledge.lifemgr.lists;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.stream.Collectors;
import com.importknowledge.lifemgr.util.*;

public class OrderedList extends ListInstance{

    private static final long serialVersionUID = 1L;
    boolean collapsed;
    public int cursor = -1;

    public  OrderedList(AbsList list, List orig) {
        super(list, orig);
        items = new ArrayList<>(list.items.size());
        list.items.stream().map(n -> new OrderedList(n, this)).forEach(items::add);
        if(items.size()*correct().level() >= 7) collapsed = true;
    }

    public OrderedList(AbsList list, OrderedList holder) {
        this(list, holder.orig);
        this.holder = holder;
    }

    public void setfocus(OrderedList focus){
        resetfocus();
        focus.cursor = focus.correct().name.getValue().length();
    }

    public OrderedList getfocus(){
        if(cursor >= 0) {
            return this;
        }
        return items.stream().map(n -> ((OrderedList) n).getfocus()).filter(n -> n != top()).findAny().orElse((OrderedList) top());
    }

    public void resetfocus(){
        cursor = -1;
        items.forEach(n -> ((OrderedList) n).resetfocus());
    }

    public int countit() {
        return 1+(collapsed?0:items.stream().collect(Collectors.summingInt(n -> n.countit())));
    }

    public int draw(Graphics2D g2, int indent, Point loc, List hovered, boolean priorityLines) {
        drawThis(g2, indent, loc, hovered);
        int i = 1;
        if(//draw priority lines
            holder != null && priorityLines && holder.getNext(this) != top() &&
            ((ListInstance) visnext().child()).correct().importance != ((ListInstance) child()).correct().importance
        ){
            g2.setColor(new Color(225, 150, 225));
            g2.drawLine((int) loc.getX()+indent*Settings.indent, (int) loc.getY()+(correct().name.lineheight(g2)+Settings.line/2), (int) loc.getX()+200+indent*Settings.indent, (int) loc.getY()+(correct().name.lineheight(g2)+Settings.line/2));
        }
        if(!collapsed) for(AbsList e : items){
                i += ((OrderedList) e).draw(g2, indent+1, new Point(loc.x, loc.y+(((OrderedList) e).correct().name.lineheight(g2)+Settings.line)*i), hovered, priorityLines);
        }
        g2.setColor(Color.lightGray);
        if(correct().items.size() > 0){//draw clarifying lines
            if(cursor >= 0) g2.setColor(new Color(140, 140, 140));
            int x = Settings.indent*indent+loc.x+((List) correct().top().getFromID(id)).name.lineheight(g2)/4;
            int y = loc.y+correct().name.lineheight(g2);
            g2.drawLine(
                x, y+Settings.line/2, 
                x, y+(correct().name.lineheight(g2)+Settings.line)*(i-1)
            );
        }
        return i;
    }
    
    public void drawThis(Graphics2D g2, int indent, Point loc, List hovered) {
        g2.translate(loc.x+Settings.indent*indent, loc.y);
        int x = 0;
        List correct = correct();
        MutableString name = correct.name;
        g2.setColor(Color.BLUE);
        g2.fillOval(0, 0, name.lineheight(g2), name.lineheight(g2));
        g2.setColor(Color.GREEN);
        g2.fillArc(0, 0, (int) (1.1*name.lineheight(g2)), (int) (1.1*name.lineheight(g2)), 90, (int) (-360*correct.progress));
        g2.setColor(Color.WHITE);
        if(correct.persistant)
            g2.fillOval(5*name.lineheight(g2)/12, 5*name.lineheight(g2)/12, name.lineheight(g2)/3, name.lineheight(g2)/3);

        x += name.lineheight(g2)+Settings.linespace;
        g2.setColor(Color.BLACK);
        Font font = g2.getFont();
        if(correct.progress == 1 || pseudo) g2.setColor(Color.GRAY);
        if(correct.progress == 1){
            Hashtable<TextAttribute, Object> attributes = new Hashtable<TextAttribute, Object>();
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            g2.setFont(font.deriveFont(attributes)); 
        }
        g2.drawString(name.getValue(), x, name.lineheight(g2));
        if(cursor >= 0){
            g2.setColor(Color.BLUE);
            var nameToCursor = new MutableString(name.getValue().substring(0, cursor));
            g2.drawLine(x+nameToCursor.linewidth(g2), 0, x+nameToCursor.linewidth(g2), name.lineheight(g2));
        }
        
        g2.setFont(font);
        if(correct.equals(hovered) || cursor >= 0){
            x += Settings.linespace+Math.max(name.linewidth(g2), Settings.buttondist);
            g2.setColor(Color.RED);
            g2.fillOval(x, 0, (int) name.lineheight(g2), (int) name.lineheight(g2));

            x += name.lineheight(g2)/5;
            g2.setColor(Color.BLACK);
            g2.drawLine(x, name.lineheight(g2)*4/5, (int) (name.lineheight(g2)*3/5+x), name.lineheight(g2)/5);
            g2.drawLine(x, name.lineheight(g2)/5, (int) (name.lineheight(g2)*3/5+x), name.lineheight(g2)*4/5);

            x += name.lineheight(g2);
            g2.setColor(Color.GREEN);
            g2.fillOval(x, 0, (int) name.lineheight(g2), (int) name.lineheight(g2));

            x += name.lineheight(g2)/5;
            g2.setColor(Color.BLACK);
            g2.drawLine(x, name.lineheight(g2)/2, (int) (name.lineheight(g2)*3/5+x), name.lineheight(g2)/2);
            g2.drawLine((int) (name.lineheight(g2)*3/10+x), name.lineheight(g2)/5, (int) (name.lineheight(g2)*3/10+x), name.lineheight(g2)*4/5);

            x += name.lineheight(g2);
            g2.setColor(Color.GRAY);
            g2.drawString(correct.importance+"", x, name.lineheight(g2));
        }
        g2.translate(-loc.x-Settings.indent*indent, -loc.y);
    }

    public void click(Graphics2D g2, int x) {
        List correct = correct();
        MutableString name = correct.name;
        x -= Settings.indent*correct.level();
        int x1 = 0;
        if(x < x1){//left out of bounds
            if(top() == null){
                resetfocus();
            }else{
                ((OrderedList) top()).resetfocus();
            }
            return;
        }
        x1 += name.lineheight(g2)+2*Settings.linespace;
        if(x < x1) {//check space
            check(correct.progress < 1);
            ((OrderedList) top()).setfocus(this);
            return;
        }
        x1 += name.lineheight(g2)/5+Settings.linespace+Math.max(name.linewidth(g2), Settings.buttondist);
        if(x < x1) {//text
            if(cursor >= 0) {
                collapsed = !collapsed;
                if(items.size() == 0){
                    collapsed = false;
                }
            }
            ((OrderedList) top()).setfocus(this);
            return;
        }
        x1 += name.lineheight(g2)*6/5;
        if(x < x1){//remove
            holder.remove(this);
            return;
        }
        x1 += name.lineheight(g2)*6/5;
        if(x < x1){//add sub
            add(new List("", correct()));
            return;
        }else{//right out of bounds
            if(top() == null){
                resetfocus();
            }else {
                ((OrderedList) top()).resetfocus();
            }
            return;
        }
    }
    public List hover(Graphics2D g2, int x, int y) {
        int index = Math.floorDiv(y, correct().name.lineheight(g2)+Settings.line);
        index = index(index);
        if(index <= 0){
            return correct();
        }else if(index > items.size()){
            return null;
        }else{
            return ((OrderedList) get(--index)).hover(g2, x-Settings.indent, (y-(countit(index))*(correct().name.lineheight(g2)+Settings.line)));
        }
    }

    public OrderedList get(Graphics2D g2, int x, int y) {
        int index = Math.floorDiv(y, correct().name.lineheight(g2)+Settings.line);
        index = index(index);
        if(index < 0 || index > items.size()){
            return null;
        }else if(index == 0){
            return this;
        }else{
            return ((OrderedList) get(--index)).get(g2, x-Settings.indent, (y-(countit(index))*(correct().name.lineheight(g2)+Settings.line)));
        }
    }

    @Override
    public void add(int i, AbsList e) {
        super.add(i, e);
        items.add(i, new OrderedList(e, this));
    }

    @Override
    public void add(AbsList e) {
        super.add(e);
        items.add(new OrderedList(e, this));
    }
    
    public OrderedList visnext(){
        return (OrderedList) (collapsed?upnext():next());
    }
}