package com.importknowledge.lifemgr.rendering;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.importknowledge.lifemgr.panels.ListPanelType;
import com.importknowledge.lifemgr.util.Settings;

class JButab extends JButton{

    private static final long serialVersionUID = 1L;
    ListPanelType panel;

    JButab(String name, ActionListener a, ListPanelType panel, JPanel tabs){
        super(name);
        setActionCommand(name);
        tabs.add(this);
        this.panel = panel;
        panel.tab = this;
        addActionListener(a);
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Settings.tabBorder));
        setBackground(Settings.tabBackground);
    }
} 