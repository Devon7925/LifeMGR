//region imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class Main extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	public List list;
	JSplitPane pane;
	QuickPanel quickpanel;
	static JPanel tabs;
	String path = "C:/Users/Public/eclipse-workspace/LifeMGR/yay.list";

	public Main() {
		list = new List("ToDo", null);
		list = deserialzeAddress(path);
		setLocation(20, 20);
		setSize(700, 1200);
		setTitle("LifeMGR");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabs = new JPanel();
		tabs.setMinimumSize(new Dimension(0, getHeight()/20));
		new JButab("List"  , this, new     ListPanel(list, this, path), tabs);
		new JButab("Quick" , this, new    QuickPanel(list, this      ), tabs);
		new JButab("Sorted", this, new PriorityPanel(list, this, path), tabs);
		new JButab("Stats" , this, new    StatsPanel(list, this      ), tabs);
		tabs.setLayout(new GridLayout(1, tabs.getComponentCount()));		
		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabs, ((JButab) tabs.getComponent(0)).panel);
		pane.setDividerLocation(getHeight()/20);
		pane.setDividerSize(0);
		setContentPane(pane);
		setFocusTraversalKeysEnabled(false);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage("C:\\Users\\Extra\\Pictures\\icon.png");
		setIconImage(img);
		addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
				serializeAddress(new List(((ListPanelType) pane.getBottomComponent()).list.merge(list)), path);
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
		bottom.tab.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.BLACK));
		bottom.tab.setBackground(new Color(240, 240, 240));
		list = new List(bottom.list.merge(list));
		bottom = ((JButab) e.getSource()).panel;
		pane.setBottomComponent(bottom);
		bottom.tab.setBackground(Color.WHITE);
		bottom.grabFocus();
		bottom.tab.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
		bottom.orig = list;
		bottom.update(list);
		bottom.update();
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