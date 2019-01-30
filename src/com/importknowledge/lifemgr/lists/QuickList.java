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


    public QuickList(AbsList list, List orig, QuickPanel panel) {
        super(orig, orig);
		items = new ArrayList<>(list.items.stream().map(n -> n instanceof List?new ListInstance((List) n, this):new ListInstance(n, this)).collect(Collectors.toList()));
        this.panel = panel;
    }

    public QuickList(AbsList list, QuickList holder, QuickPanel panel) {
        super(list, holder);
        this.panel = panel;
    }

    public QuickList(QuickList list) {
        super(list);
    }

    public void draw(Graphics2D g2, int w, int h){
		Drawer d = new Drawer(new Rectangle2D.Double(0, 0, w, h), g2);
		d.setFont(new Font("TimesRoman", Font.PLAIN, 50));
		render(   active, d, 1.5, ((List) active.holder).progress);
		render(oldactive, d, 0.5, oldprogress+(((List) oldactive.holder).progress-oldprogress)*anim);
	}
	
	void render(List todraw, Drawer d, double shift, double val){
		if(todraw != null) {
			double len = todraw.name.getValue().length()*0.01;
			d.setColor(new Color(100, 100, 255));
			d.drawEllipCent(new Rectangle2D.Double(shift-anim, 0.5, 0.5+len, 0.5));
			d.setColor(Color.green);
			if(todraw.progress > 0.5) d.drawEllipCent(new Rectangle2D.Double(shift-anim, 0.5, 1.2*anim*0.5, 1.2*anim*0.5));
			d.setColor(Color.white);
			if(todraw.persistant) d.drawEllipCent(new Rectangle2D.Double(shift-anim, 0.35, 0.05, 0.05));
			d.setColor(Color.black);
			d.drawString(todraw.name.getValue(), shift-anim, 0.5);
			if(todraw.holder != null){
				d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				double x = (oldactive.holder == active.holder)?0.5:shift-anim;
				List hold = (List) todraw.holder;
				len = hold.name.getValue().length()*0.01;
				d.setColor(new Color(100, 100, 255));
				d.drawEllipCent(new Rectangle2D.Double(x, 0.15, 0.25+len, 0.1));
				d.setColor(Color.GREEN);
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
		ListInstance temp = ((ListInstance) getFirst());
		if(temp != null) active = temp.correct();
    }

    public void skip(){
        if(active.holder != null)oldprogress = ((List) active.holder).progress;
        update();
        new Thread(this).start();
        oldactive = active;
        do{
            if(active != null) active = (List) ((ListInstance) active.instance((ListInstance) top()).next()).correct();
            else active = correct();
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
		if(active == null && correct().progress != 1) oldactive = active = (List) ((ListInstance) getFirst()).correct();
    }
}