package com.importknowledge.lifemgr.panels;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.importknowledge.lifemgr.lists.List;
import com.importknowledge.lifemgr.lists.ListInstance;
import com.importknowledge.lifemgr.util.Settings;

abstract public class ListPanelType extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    public ListInstance list;
    public JButton tab;
    String path;

    ListPanelType(List list, JFrame frame, String path) {
        update(list);
        frame.addKeyListener(this);
        setBackground(Settings.backgroundColor);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);
        this.path = path;
    }

    @Override
    public abstract void keyTyped(KeyEvent e);

    @Override
    public abstract void keyPressed(KeyEvent e);

    @Override
    public abstract void keyReleased(KeyEvent e);

    public void update() {
        list.update();
    }

    public void update(List l) {
        list = new ListInstance(l, l);
        update();
    }
}