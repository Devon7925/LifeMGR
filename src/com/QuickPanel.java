package com;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

class QuickPanel extends ListPanelType {
	private static final long serialVersionUID = 1L;
	public QuickPanel(List list, JFrame frame){
		super(list, frame);
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
	}
	@Override
	void update() {
		list = new QuickList(orig, this).prioitysort();
		super.update();
	}
	void update(List l){
		list = new QuickList(l, this);
		update();
	}
}