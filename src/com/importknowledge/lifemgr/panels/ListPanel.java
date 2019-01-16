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

import com.importknowledge.lifemgr.lists.List;
import com.importknowledge.lifemgr.lists.ListInstance;
import com.importknowledge.lifemgr.lists.OrderedList;
import com.importknowledge.lifemgr.rendering.Main;
import com.importknowledge.lifemgr.util.Settings;

public class ListPanel extends ListPanelType implements MouseInputListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	Graphics2D g2 = (Graphics2D) getGraphics();
	double scroll = 0;
	Point clickpos, lastpos = new Point(0, 0);
	JFrame frame;
	boolean selectdrag = false;

	public ListPanel(List list, JFrame frame, String path){
		super(list, frame, path);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		this.frame = frame;
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
			((OrderedList) list).hover(g2, lastpos.x, lastpos.y-(int) scroll),
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
		lastpos = clickpos = e.getPoint();
		OrderedList clicked = ((OrderedList) list).get(g2, (int) clickpos.getX(), (int) clickpos.getY()-(int) scroll);
		if(clicked != null) selectdrag = clicked.equals(((OrderedList) list).getfocus());
	}
	public void mouseReleased(MouseEvent e) {
		if(Math.abs(clickpos.x-e.getX())<5 && Math.abs(clickpos.y-e.getY())<5){
			OrderedList clicked = ((OrderedList) list).get(g2, e.getX()-Settings.indent, e.getY()-Settings.indent-(int) scroll);
			if(clicked != null) clicked.click(g2, e.getX());
		}else if(!selectdrag){
			scroll -= lastpos.y-e.getY();
		}
		list.update();
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {
		selectdrag = false;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(selectdrag){
			ListInstance foc = ((OrderedList) list).getfocus();
			List corrFoc = foc.correct();
			ListInstance moused = ((OrderedList) list).get(g2, (int) e.getX(), (int) e.getY()-(int) scroll);
			List corrMoused = moused == null?null:moused.correct();
			if(!(corrFoc == null || corrFoc.holder == null || corrMoused == null || corrMoused.holder == null)){//do nothing
				if(corrFoc.equals(corrMoused)){
					if(e.getX()-clickpos.getX() > Settings.indent){
						int i = foc.holder.items.indexOf(foc);
						if(i > 0){
							clickpos.x += Settings.indent;
							((OrderedList) foc.holder).remove(foc);
							foc.holder.items.get(i-1).add(corrFoc);
						}
					}else if(corrFoc.holder.holder != null && clickpos.getX()-e.getX() > Settings.indent){
						int i = corrFoc.holder.holder.items.indexOf(corrFoc.holder);
						if(corrFoc.holder.holder != null && foc.holder.items.indexOf(foc) == foc.holder.items.size()-1){
							clickpos.x -= Settings.indent;
							((OrderedList) foc.holder).remove(foc);
							foc.holder.holder.add(i+1, corrFoc);
						}
					}
				}else if(corrFoc.holder.equals(corrMoused.holder)){
					int lineheight = corrFoc.name.lineheight(g2)+Settings.line;
					int i = foc.holder.items.indexOf(foc);
					if(Math.abs(clickpos.getY()-e.getY())>2*lineheight){
						selectdrag = false;
					}else if(e.getY()-clickpos.getY() > lineheight){
						clickpos.y += lineheight;
						((OrderedList) foc.holder).remove(foc);
						foc.holder.add(i+1, corrFoc);
					}else if(clickpos.getY()-e.getY() > lineheight){
						clickpos.y -= lineheight;
						((OrderedList) foc.holder).remove(foc);
						foc.holder.add(i-1, corrFoc);
					}
				}
				((OrderedList) foc.holder).setfocus((OrderedList) corrFoc.instance(list));
			}
		}else {
			scroll -= lastpos.getY()-e.getY();
			if(scroll > 0) scroll = 0;
			var end = -Settings.line+getHeight()+(-list.countit())*(1.0*Settings.line+list.correct().name.lineheight(g2));
			if(scroll < end) scroll = end;
		}
		lastpos = e.getPoint();
		list.update();
		repaint();
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		lastpos = e.getPoint();
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scroll -= Settings.scroll*e.getWheelRotation();
		if(scroll > 0) scroll = 0;
		var end = -Settings.line+getHeight()+(-list.countit())*(1.0*Settings.line+list.correct().name.lineheight(g2));
		if(scroll < end) scroll = end;
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
						String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
						if(focuus.cursor==foc.name.getValue().length()) foc.name.setValue(foc.name.getValue()+data);
						else foc.name.setValue(foc.name.getValue().substring(0, focuus.cursor)+data+foc.name.getValue().substring(focuus.cursor, foc.name.getValue().length()));
						int endx = (1+foc.level())*Settings.indent+foc.name.linewidth(g2)+Settings.linespace+5*foc.name.lineheight(g2);
						frame.setSize(Math.max(frame.getWidth(), endx), frame.getHeight());
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
				(foc.holder == null?focuus:(OrderedList) focuus.holder).setfocus(focuus.visnext());
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