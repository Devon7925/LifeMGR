package com;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

class StatsPanel extends ListPanelType {

    private static final long serialVersionUID = 1L;

    StatsPanel(List list, JFrame frame){
		super(list, frame);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        ((StatsList) list).draw(g2, getWidth(), getHeight());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
    
	void update(List l){
		list = new StatsList(l);
		update();
	}

}