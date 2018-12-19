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
    boolean selected = false;
    boolean collapsed;
    public int cursor = 0;

    public  OrderedList(List list) {
        super(list);
        items = new ArrayList<>(items.stream().map(n -> new OrderedList((List) n, this)).collect(Collectors.toList()));
    }

    public OrderedList(List list, OrderedList holder) {
        this(list);
        this.holder = holder;
    }

    public OrderedList(OrderedList list) {
        super(list);
        selected = list.selected;
        collapsed = list.collapsed;
    }
    
    public OrderedList(String name, List holder){
        super(name, holder);
    }

    public void setfocus(OrderedList focus){
        resetfocus();
        focus.selected = true;
        focus.cursor = focus.name.getValue().length();
    }

    public OrderedList getfocus(){
        if(selected) {
            return this;
        }
        return items.stream().map(n -> ((OrderedList) n).getfocus()).filter(n -> n != top()).findAny().orElse(top());
    }

    public void resetfocus(){
        selected = false;
        items.forEach(n -> ((OrderedList) n).resetfocus());
    }

    public int countit() {
        return 1+(collapsed?0:items.stream().collect(Collectors.summingInt(n -> n.countit())));
    }

    public int draw(Graphics2D g2, int indent, Point loc, List hovered, boolean priorityLines) {
        drawThis(g2, indent, loc, hovered);
        int i = 1;
        if(//draw priority lines
            priorityLines &&
            holder != null && 
            ((collapsed && Math.abs(((List) upnext()).importance) != Math.abs(importance)) || 
            (!collapsed && Math.abs(((List)   next()).importance) != Math.abs(importance))) && 
            holder.getNext(this) != top()
        ){
            g2.setColor(new Color(225, 150, 225));
            g2.drawLine((int) loc.getX()+indent*Settings.indent, (int) loc.getY()+(name.lineheight(g2)+Settings.line/2), (int) loc.getX()+200+indent*Settings.indent, (int) loc.getY()+(name.lineheight(g2)+Settings.line/2));
        }
        if(!collapsed){
            for(AbsList e : items){
                i += ((OrderedList) e).draw(g2, indent+1, new Point(loc.x, loc.y+(((List) e).name.lineheight(g2)+Settings.line)*i), hovered, priorityLines);
            }
        }
        g2.setColor(Color.lightGray);
        if(items.size() > 0){//draw clarifying lines
            if(selected) g2.setColor(new Color(140, 140, 140));
            g2.drawLine(
                Settings.indent*indent+loc.x+name.lineheight(g2)/4, 
                loc.y+name.lineheight(g2)+Settings.line/2, 
                Settings.indent*indent+loc.x+name.lineheight(g2)/4,
                loc.y+name.lineheight(g2)+(name.lineheight(g2)+Settings.line)*(i-1));
        }
        return i;
    }
    
    public int drawThis(Graphics2D g2, int indent, Point loc, List hovered) {
        g2.translate(loc.x+Settings.indent*indent, loc.y);
        g2.setColor(Color.blue);
        int x = 0;
        g2.fillOval(0, 0, name.lineheight(g2), name.lineheight(g2));
        g2.setColor(Color.green);
        g2.fillArc(0, 0, (int) (1.1*name.lineheight(g2)), (int) (1.1*name.lineheight(g2)), 90, (int) (-360*progress));
        g2.setColor(Color.white);
        if(persistant)
            g2.fillOval(5*name.lineheight(g2)/12, 5*name.lineheight(g2)/12, name.lineheight(g2)/3, name.lineheight(g2)/3);
        x += name.lineheight(g2)+Settings.linespace;
        g2.setColor(Color.black);
        Font font = g2.getFont();
        if(progress == 1 || importance < 0){
            g2.setColor(Color.gray);
            Hashtable<TextAttribute, Object> attributes = new Hashtable<TextAttribute, Object>();
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            g2.setFont(font.deriveFont(attributes)); 
        }
        if(selected){
            g2.setColor(Color.magenta);
            MutableString nameToCursor = new MutableString(name.getValue().substring(0, cursor));
            g2.drawLine(x+nameToCursor.linewidth(g2), 0, x+nameToCursor.linewidth(g2), name.lineheight(g2));
        }
        g2.drawString(name.getValue(), x, name.lineheight(g2));
        g2.setFont(font);
        if(this == hovered){
            x += Settings.linespace+((name.linewidth(g2)>Settings.buttondist)?name.linewidth(g2):Settings.buttondist);
            g2.fillOval(x, (int) (name.lineheight(g2)/6.0), (int) (name.lineheight(g2)/1.5), (int) (name.lineheight(g2)/1.5));
            g2.setColor(Color.red);
            g2.drawLine(x, name.lineheight(g2), (int) (2.0/3.0*name.lineheight(g2)+x), 0);
            g2.drawLine(x, 0, (int) (2.0/3.0*name.lineheight(g2)+x), name.lineheight(g2));
            x += name.lineheight(g2);
            g2.setColor(Color.black);
            g2.fillOval(x, (int) (name.lineheight(g2)/6.0), (int) (name.lineheight(g2)/1.5), (int) (name.lineheight(g2)/1.5));
            g2.setColor(Color.green);
            g2.drawLine(x, name.lineheight(g2)/2, (int) (2.0/3.0*name.lineheight(g2)+x), name.lineheight(g2)/2);
            g2.drawLine((int) (1.0/3.0*name.lineheight(g2)+x), 0, (int) (1.0/3.0*name.lineheight(g2)+x), name.lineheight(g2));
            x += name.lineheight(g2);
            g2.setColor(Color.gray);
            g2.drawString(Math.abs(importance)+"", x, name.lineheight(g2));
        }
        g2.setColor(Color.black);
        g2.translate(-loc.x-Settings.indent*indent, -loc.y);
        return 1;
    }

    public void click(Graphics2D g2, int x) {
        x -= Settings.indent*level();
        int x1 = 0;
        if(x < x1){
            if(top() == null){
                resetfocus();
            }else{
                top().resetfocus();
            }
            return;
        }
        x1 += name.lineheight(g2)+2*Settings.linespace;
        if(x < x1) {
            check(progress < 1);
            top().setfocus(this);
            return;
        }
        x1 += Settings.linespace+((name.linewidth(g2)>Settings.buttondist)?name.linewidth(g2):Settings.buttondist);
        if(x < x1) {
            if(selected) {
                collapsed = !collapsed;
                if(items.size() == 0){
                    collapsed = false;
                }
            }
            top().setfocus(this);
            return;
        }
        x1 += name.lineheight(g2);
        if(x < x1){
            holder.remove(this);
            return;
        }
        x1 += name.lineheight(g2);
        if(x < x1){
            addto();
            return;
        }else{
            if(top() == null){
                resetfocus();
            }else {
                top().resetfocus();
            }
            return;
        }
    }
    public List hover(Graphics2D g2, int x, int y) {
        int index = Math.floorDiv(y, name.lineheight(g2)+Settings.line);
        index = index(index);
        if(index <= 0){
            return this;
        }else if(index > items.size()){
            return null;
        }else{
            return ((OrderedList) get(--index)).hover(g2, x-Settings.indent, (y-(countit(index))*(name.lineheight(g2)+Settings.line)));
        }
    }

    public OrderedList get(Graphics2D g2, int x, int y) {
        int index = Math.floorDiv(y, name.lineheight(g2)+Settings.line);
        index = index(index);
        if(index < 0){
            return new OrderedList("", null);
        }else if(index == 0){
            return this;
        }else if(index > items.size()){
            return new OrderedList("", null);
        }else{
            return ((OrderedList) get(--index)).get(g2, x-Settings.indent, (y-(countit(index))*(name.lineheight(g2)+Settings.line)));
        }
    }

    public void addto() {
        OrderedList l = new OrderedList("", this);
        add(0, l);
        setfocus(l);
    }

    public OrderedList prioitysort(){
        List orig = new List(this);
        OrderedList l = new OrderedList(orig.name.getValue(), null);
        for(int i = 0; i <= 9; i++){
            l.addAll(new ArrayList<>(orig.find(i).stream().map(n -> new OrderedList(n)).collect(Collectors.toList())));
        }
        l.unorder();
        return l;
    }
    public OrderedList top(){
        if(holder == null)return this;
        return ((OrderedList) holder).top();
    }
}