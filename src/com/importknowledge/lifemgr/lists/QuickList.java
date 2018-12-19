package com.importknowledge.lifemgr.lists;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.importknowledge.lifemgr.panels.*;
import com.importknowledge.lifemgr.rendering.*;

public class QuickList extends ListInstance implements Runnable{

    private static final long serialVersionUID = 1L;
	List active, oldactive;
	boolean h = false;
	double anim = 1;
    double oldprogress;
    QuickPanel panel;


    public  QuickList(List list, QuickPanel panel) {
        super(list);
        items = new ArrayList<>(items.stream().map(n -> new ListInstance((List) n, this)).collect(Collectors.toList()));
        this.panel = panel;
    }

    public QuickList(List list, QuickList holder, QuickPanel panel) {
        this(list, panel);
        this.holder = holder;
    }

    public QuickList(QuickList list) {
        super(list);
    }
    
    public QuickList(String name, List holder, QuickPanel panel){
        super(name, holder);
        this.panel = panel;
    }

    public void draw(Graphics2D g2, int w, int h){
		Drawer d = new Drawer(new Rectangle2D.Double(0, 0, w, h), g2);
		d.setFont(new Font("TimesRoman", Font.PLAIN, 50));
		if(active != null) {
			double len = active.name.getValue().length()*0.01;
			d.setColor(new Color(100, 100, 255));
			d.drawEllipCent(new Rectangle2D.Double(1.5-anim, 0.5, (0.5+len), 0.5));
			d.setColor(Color.white);
			if(active.persistant) d.drawEllipCent(new Rectangle2D.Double(1.5-anim, 0.35, 0.05, 0.05));
			d.setColor(Color.black);
			d.drawString(active.name.getValue(), 1.5-anim, 0.5);
			d.setColor(Color.black);
			if(active.holder != null){
				d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				double x = (oldactive.holder == active.holder)?-0.5:1.5-anim;
				List hold = (List) active.holder;
				len = hold.name.getValue().length()*0.01;
				d.setColor(new Color(100, 100, 255));
				d.drawEllipCent(new Rectangle2D.Double(x, 0.15, 0.25+len, 0.1));
				d.setColor(Color.GREEN);
				d.drawEllipCent(new Rectangle2D.Double(x, 0.15, hold.progress*(0.25+len), hold.progress*0.1));
				d.setColor(Color.black);
				d.drawString(hold.name.getValue(), x, 0.15);
				d.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			}
		}
		if(oldactive != null) {
			double len = oldactive.name.getValue().length()*0.01;
			d.setColor(new Color(100, 100, 255));
			d.drawEllipCent(new Rectangle2D.Double(0.5-anim, 0.5, 0.5+len, 0.5));
			d.setColor(Color.green);
			if(oldactive.progress > 0.5) d.drawEllipCent(new Rectangle2D.Double(0.5-anim, 0.5, 1.2*anim*0.5, 1.2*anim*0.5));
			d.setColor(Color.white);
			if(oldactive.persistant) d.drawEllipCent(new Rectangle2D.Double(0.5-anim, 0.35, 0.05, 0.05));
			d.setColor(Color.black);
			d.drawString(oldactive.name.getValue(), 0.5-anim, 0.5);
			if(oldactive.holder != null){
				d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				double x = (oldactive.holder == active.holder)?0.5:0.5-anim;
				List hold = (List) oldactive.holder;
				len = hold.name.getValue().length()*0.01;
				d.setColor(new Color(100, 100, 255));
				d.drawEllipCent(new Rectangle2D.Double(x, 0.15, 0.25+len, 0.1));
				d.setColor(Color.GREEN);
				double val = oldprogress+(hold.progress-oldprogress)*anim;
				d.drawEllipCent(new Rectangle2D.Double(x, 0.15, val*(0.25+len), val*0.1));
				d.setColor(Color.black);
				d.drawString(hold.name.getValue(), x, 0.15);
				d.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			}
		}
    }

    public void complete(){
        if(active != null) {
            if(active.holder != null)oldprogress = ((List) active.holder).progress;
            active.check(true);
        }
        update();
        new Thread(this).start();
        oldactive = active;
        active = (List) getFirst();
    }

    public void skip(){
        if(active.holder != null)oldprogress = ((List) active.holder).progress;
        update();
        new Thread(this).start();
        oldactive = active;
        do{
            if(active != null) active = 
                    (List) active.next();
            else active = this;
        }while(active.getFirst() == null);
        if(active != null) active = (List) active.getFirst();
    }

	@Override
	public void run() {
		anim = 0;
		while(anim < 1){
			anim += 0.04;
			panel.repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    public void update(){
        super.update();
		if(active == null) oldactive = active = (List) getFirst();
    }

    public QuickList prioitysort(){
        List orig = new List(this);
        QuickList l = new QuickList(orig.name.getValue(), null, panel);
        for(int i = 0; i <= 9; i++){
            l.addAll(new ArrayList<>(orig.find(i).stream().map(n -> new ListInstance(n)).collect(Collectors.toList())));
        }
        l.unorder();
        return l;
    }
}