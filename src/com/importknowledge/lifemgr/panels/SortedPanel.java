package com.importknowledge.lifemgr.panels;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;
import com.importknowledge.lifemgr.lists.*;
import com.importknowledge.lifemgr.util.*;

public class SortedPanel extends ListPanel implements MouseInputListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	public SortedPanel(List list, JFrame frame, String path){
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
			((OrderedList) list).hover(g2, lastpos.x, lastpos.y-(int) scroll),
			true
		);
        g2.translate(0, -scroll);
	}
	@Override
	public void update() {
		list = new OrderedList(list.correct().prioitysort(), list.correct());
		super.update();
	}
}