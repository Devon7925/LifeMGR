package com.importknowledge.lifemgr.rendering;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.importknowledge.lifemgr.lists.List;
import com.importknowledge.lifemgr.panels.ListPanel;
import com.importknowledge.lifemgr.panels.ListPanelType;
import com.importknowledge.lifemgr.panels.QuickPanel;
import com.importknowledge.lifemgr.panels.SortedPanel;
import com.importknowledge.lifemgr.panels.StatsPanel;
import com.importknowledge.lifemgr.util.Settings;


public class Main extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	public List list;
	JSplitPane pane;
	QuickPanel quickpanel;
	static JPanel tabs;

	public Main() {
		list = new List("ToDo", null);
		if(new File(Settings.inpath).exists()) list = deserialzeAddress(Settings.inpath);
		setLocation(20, 20);
		setSize(700, 1200);
		setTitle("LifeMGR");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabs = new JPanel();
		tabs.setMinimumSize(new Dimension(0, getHeight()/20));
		ArrayList<ListPanelType> panels = new ArrayList<>(Arrays.asList(
			new   ListPanel(list, this, Settings.outpath),
			new  QuickPanel(list, this, Settings.outpath),
			new SortedPanel(list, this, Settings.outpath),
			new  StatsPanel(list, this, Settings.outpath)
		));
		for(ListPanelType panel : panels)
			new JButab(panel.getClass().getName().replaceAll(".+\\.", "").replace("Panel", ""), this, panel, tabs);
		tabs.setLayout(new GridLayout(1, tabs.getComponentCount()));		
		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabs, ((JButab) tabs.getComponent(0)).panel);
		pane.setDividerLocation(getHeight()/20);
		pane.setDividerSize(0);
		setContentPane(pane);
		setFocusTraversalKeysEnabled(false);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage(Settings.iconpath);
		setIconImage(img);
		addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
				serializeAddress(((ListPanelType) pane.getBottomComponent()).list.correct(), Settings.outpath);
                e.getWindow().dispose();
            }
        });
	}
	public static void main(String[] args) {
		Main main = new Main();
		main.setVisible(true);
		((JButab) tabs.getComponent(1)).doClick();
		((JButab) tabs.getComponent(0)).doClick();
	}
	public static void serializeAddress(List address, String path) {
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;
		try {
			fout = new FileOutputStream(path);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(address);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		ListPanelType bottom = ((ListPanelType) pane.getBottomComponent());
		bottom.tab.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Settings.tabBorder));
		bottom.tab.setBackground(Settings.tabBackground);
		bottom = ((JButab) e.getSource()).panel;
		pane.setBottomComponent(bottom);
		bottom.tab.setBackground(Settings.selectedTabBackground);
		bottom.grabFocus();
		bottom.tab.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Settings.tabBorder));
		bottom.update(list);
	}
	public List deserialzeAddress(String filename) {
		List address = null;
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		try {
			fin = new FileInputStream(filename);
			ois = new ObjectInputStream(fin);
			address = (List) ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return address;
	}
}