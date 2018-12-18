package com;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

abstract class ListPanelType extends JPanel implements KeyListener{
	private static final long serialVersionUID = 1L;
	ListInstance list;
	List orig;
	JButton tab;
	ListPanelType(List list, JFrame frame){
		orig = list;
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

	void update(){
		list.update();
	}
	void update(List l){
		list = new ListInstance(l);
		update();
	}
}