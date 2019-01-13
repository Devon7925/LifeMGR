package com.importknowledge.lifemgr.panels;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;
import com.importknowledge.lifemgr.lists.*;
import com.importknowledge.lifemgr.util.*;
import com.importknowledge.lifemgr.rendering.*;

public class ListPanel extends ListPanelType implements MouseInputListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	Graphics2D g2 = (Graphics2D) getGraphics();
	double scroll = 0;
	Point click, click2 = new Point(0, 0);
	JFrame frame;
	boolean selectdrag = false;
	String path;

	public ListPanel(List list, JFrame frame, String path){
		super(list, frame);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		this.frame = frame;
		this.path = path;
		update(list);
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
		g2.setFont(Settings.font.deriveFont((float) Settings.fontsize));
        g2.translate(0, scroll);
		((OrderedList) list).draw(g2, 0,
			new Point(Settings.indent, Settings.indent),
			((OrderedList) list).hover(g2, click2.x, click2.y-(int) scroll),
			false
		);
        g2.translate(0, -scroll);
	}
	//region mouse
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		click2 = click = e.getPoint();
		OrderedList clicked = ((OrderedList) list).get(g2, (int) click.getX(), (int) click.getY()-(int) scroll);
		if(clicked != null) selectdrag = clicked.equals(((OrderedList) list).getfocus());
	}
	public void mouseReleased(MouseEvent e) {
		if(Math.abs(click.x-e.getX())<5 && Math.abs(click.y-e.getY())<5){
			OrderedList clicked = ((OrderedList) list).get(g2, e.getX()-Settings.indent, e.getY()-Settings.indent-(int) scroll);
			if(clicked != null) clicked.click(g2, e.getX());
		}else if(!selectdrag){
			scroll -= click2.y-e.getY();
		}
		list.update();
		if(scroll<-Settings.line+getHeight()+(-list.countit())*(1.0*Settings.line+list.correct().name.lineheight(g2))) scroll = -Settings.line+getHeight()+(-list.countit())*(1.0*Settings.line+list.correct().name.lineheight(g2));
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(selectdrag){
			List temp = ((OrderedList) list).getfocus().correct();
			List temp2 = ((OrderedList) list).get(g2, (int) e.getX(), (int) e.getY()-(int) scroll).correct();
			if(temp == temp2 && e.getX()-click.getX() > Settings.indent){
				int i = temp.holder.items.indexOf(temp);
				if(i > 0){
					click.x = e.getX();
					list.correct().remFrom(temp.holder, temp);
					list.correct().addTo(temp.holder.items.get(i-1), temp);
				}
			}else if(temp == temp2 && click.getX()-e.getX() > Settings.indent){
				if(temp.holder.holder != null){
					int i = temp.holder.holder.items.indexOf(temp.holder);
					click.x = e.getX();
					list.correct().remFrom(temp.holder, temp);
					list.correct().addTo(temp.holder.holder, i+1, temp);
				}
			}else if(temp.level() == temp2.level() && temp.holder == temp2.holder){
				if(click2.getY() < e.getY()){
					list.correct().set(temp2, temp);
					list.correct().set(((OrderedList) list).getfocus(), temp2);
				}else{
					list.correct().set(((OrderedList) list).getfocus(), temp2);
					list.correct().set(temp2, temp);
				}
			}else if(temp.level() > temp2.level() && temp.level() > 1){
				if(click2.getY() > e.getY()){
					List foc = ((OrderedList) list).getfocus().correct();
					list.correct().remFrom(foc.holder, foc);
					list.correct().addTo(foc.holder.holder, foc.holder.holder.items.indexOf(foc.holder), foc);
				}else{
					List foc = ((OrderedList) list).getfocus().correct();
					list.correct().remFrom(foc.holder, foc);
					list.correct().addTo(foc.holder.holder, foc.holder.holder.items.indexOf(foc.holder)+1, foc);
				}
			}
		}else if(click2.getY()-e.getY()>0 || scroll<0) scroll -= click2.getY()-e.getY();
		click2 = e.getPoint();
		list.update();
		repaint();
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		click2 = e.getPoint();
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if((e.getWheelRotation()>0 && scroll>-Settings.line+getHeight()+(-list.countit())*(1.0*Settings.line+list.correct().name.lineheight(g2))) || (scroll<0 && e.getWheelRotation()<0))scroll -= Settings.scroll*e.getWheelRotation();
		repaint();
	}
	//endregion
	//region key
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {
		OrderedList focuus = ((OrderedList) list).getfocus();
		List foc = focuus.correct();
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_F1){
			list.correct().clear();
		}else if(key == KeyEvent.VK_F2){
			if(!foc.persistant){
				foc.setpersistant();
			}else{
				foc.persistant = false;
			}
		}else if(key == KeyEvent.VK_ESCAPE){
			((OrderedList) list).resetfocus();
		}else if(foc != null){
			if(e.isControlDown()){
				ListInstance hoold = (ListInstance) focuus.holder;
				List hold = (List) foc.holder;
				if(hold == null){}//donothing
				else if(key == KeyEvent.VK_RIGHT){
					int i = hold.items.indexOf(foc)-1;
					if(i < hold.items.size() && i >= 0){
						hoold.remove(focuus);
						hoold.items.get(i).add(foc);
						((OrderedList) hoold.items.get(i)).setfocus((OrderedList) foc.instance(list));
					}
				}else if(key == KeyEvent.VK_LEFT){
					if(hold.holder != null){
						hoold.remove(focuus);
						hoold.holder.add(hold.holder.items.indexOf(hold)+1, foc);
						((OrderedList) hoold.holder).setfocus((OrderedList) foc.instance(list));
					}
				}else if(key == KeyEvent.VK_UP && hold.items.indexOf(foc)-1 >= 0){
					Collections.swap(hold.items, hold.items.indexOf(foc), hold.items.indexOf(foc)-1);
					update(list.correct());
					((OrderedList) hoold).setfocus((OrderedList) foc.instance(list));
				}else if(key == KeyEvent.VK_DOWN && hold.items.indexOf(foc)+1 < hold.items.size()){
					Collections.swap(hold.items, hold.items.indexOf(foc), hold.items.indexOf(foc)+1);
					update(list.correct());
					((OrderedList) hoold).setfocus((OrderedList) foc.instance(list));
				}else if(key == KeyEvent.VK_V) {
					try {
						String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
							.getData(DataFlavor.stringFlavor);
						if(focuus.cursor==foc.name.getValue().length()) foc.name.setValue(foc.name.getValue()+data);
						else foc.name.setValue(foc.name.getValue().substring(0, focuus.cursor)+data+foc.name.getValue().substring(focuus.cursor, foc.name.getValue().length()));
						int a = (1+foc.level())*Settings.indent+foc.name.linewidth(g2)+Settings.linespace+5*foc.name.lineheight(g2);
						frame.setSize(frame.getWidth()>a?frame.getWidth():a, frame.getHeight());
						focuus.cursor+=data.length();
					} catch (HeadlessException | UnsupportedFlavorException | IOException e1) {
						e1.printStackTrace();
					} 
				}else if(key == KeyEvent.VK_ENTER){
						List l = new List("", (List) foc);
						focuus.add(0, l);
						((OrderedList) foc.instance(list)).setfocus((OrderedList) l.instance(list));
				}else if(KeyEvent.getKeyText(key).matches("\\d")){
					int newprioty = Integer.parseInt(KeyEvent.getKeyText(key));
					if(newprioty == 0) newprioty = 10;
					foc.setpriority(newprioty);
				}
			}else if(key == KeyEvent.VK_UP){
				if(foc.holder != null) focuus.setfocus((OrderedList) ((List) foc.prev()).instance(list));
			}else if(key == KeyEvent.VK_DOWN){
				if(foc.holder == null) focuus.setfocus((OrderedList) ((List) foc.next()).instance(list));
				else ((OrderedList) focuus.holder).setfocus((OrderedList) ((List) foc.next()).instance(list));
			}else if(key == KeyEvent.VK_RIGHT && focuus.cursor < foc.name.getValue().length()){
				focuus.cursor++;
			}else if(key == KeyEvent.VK_LEFT && focuus.cursor > 0){
				focuus.cursor--;
			}else if(key == KeyEvent.VK_ENTER && foc.holder != null){
				List l = new List("", (List) foc.holder);
				((List) foc.holder).add(foc.holder.items.indexOf(foc)+1, l);
				scroll -= foc.name.lineheight(g2)+Settings.line;
				update(list.correct());
				((OrderedList) ((List) foc.holder).instance(list)).setfocus((OrderedList) l.instance(list));
			}else if(key == KeyEvent.VK_BACK_SPACE && focuus.cursor != 0){
				foc.name.setValue(foc.name.getValue().substring(0, focuus.cursor-1)+foc.name.getValue().substring(focuus.cursor, foc.name.getValue().length()));
				focuus.cursor--;
			}else if(isPrintableChar(e.getKeyChar())){
				if(focuus.cursor==foc.name.getValue().length()) foc.name.setValue(foc.name.getValue()+e.getKeyChar());
				else foc.name.setValue(foc.name.getValue().substring(0, focuus.cursor)+e.getKeyChar()+foc.name.getValue().substring(focuus.cursor, foc.name.getValue().length()));
				int a = (1+foc.level())*Settings.indent+foc.name.linewidth(g2)+Settings.linespace+5*foc.name.lineheight(g2);
				frame.setSize(frame.getWidth()>a?frame.getWidth():a, frame.getHeight());
				focuus.cursor++;
			}
		}
		update();
		Main.serializeAddress(list.correct(), path);
		repaint();
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
	public boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }
	//endregion
	public void update(List l){
		list = new OrderedList(l, l);
		update();
	}
}