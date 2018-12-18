package com;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

class PriorityPanel extends ListPanel implements MouseInputListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	public PriorityPanel(List list, JFrame frame, String path){
		super(list, frame, path);
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
			true
		);
        g2.translate(0, -scroll);
	}
	@Override
	void update() {
		list = new OrderedList(orig).prioitysort();
		super.update();
	}
}