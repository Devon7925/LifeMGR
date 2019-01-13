package com.importknowledge.lifemgr.panels;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.importknowledge.lifemgr.lists.*;

abstract public class ListPanelType extends JPanel implements KeyListener{
	private static final long serialVersionUID = 1L;
	public ListInstance list;
	public JButton tab;
	ListPanelType(List list, JFrame frame){
		update(list);
		frame.addKeyListener(this);
		setBackground(Color.WHITE);
		setFocusTraversalKeysEnabled(false);
		addKeyListener(this);
	}
	@Override
	public abstract void keyTyped(KeyEvent e);
	@Override
	public abstract void keyPressed(KeyEvent e);
	@Override
	public abstract void keyReleased(KeyEvent e);

	public void update(){
		list.update();
	}
	public void update(List l){
		list = new ListInstance(l, l);
		update();
	}
}