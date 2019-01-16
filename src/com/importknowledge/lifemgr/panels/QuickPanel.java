package com.importknowledge.lifemgr.panels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import com.importknowledge.lifemgr.lists.List;
import com.importknowledge.lifemgr.lists.QuickList;
import com.importknowledge.lifemgr.rendering.Main;

public class QuickPanel extends ListPanelType {
	private static final long serialVersionUID = 1L;
	public QuickPanel(List list, JFrame frame, String path){
		super(list, frame, path);
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		((QuickList) list).draw(g2, getWidth(), getHeight());
	}
	
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_F1){
			list.clear();
		}else if(e.getKeyCode() == KeyEvent.VK_SPACE){
			((QuickList) list).complete();
		}else if(e.getKeyCode() == KeyEvent.VK_S){
			((QuickList) list).skip();
		}
		repaint();
		Main.serializeAddress(list.correct(), path);
	}
	@Override
	public void update() {
		list = new QuickList(list.correct().prioitysort(), list.correct(), this);
		super.update();
	}
	public void update(List l){
		list = new QuickList(l, l, this);
		update();
	}
}