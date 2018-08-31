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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class Main extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	public List list = new List("ToDo", null);
	JSplitPane pane;
	ListPanel listpanel;
	QuickPanel quickpanel;
	JPanel tabs;
	static JButab l;

	public Main() {
		list = deserialzeAddress("C:/Users/Public/eclipse-workspace/LifeMGR/yay.list");
		setLocation(20, 20);
		setSize(700, 1200);
		setTitle("LifeMGR");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabs = new JPanel();
		tabs.setMinimumSize(new Dimension(0, getHeight()/20));
		tabs.setLayout(new GridLayout(1, 2));
		l = new JButab("List", this);
		listpanel = new ListPanel(list, l, this);
		listpanel.frame = this;
		quickpanel = new QuickPanel(list, new JButab("Quick", this), this);		
		pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabs, listpanel);
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
				serializeAddress(listpanel.list);
                e.getWindow().dispose();
            }
        });
	}
	public static void main(String[] args) {
		Main main = new Main();
		main.setVisible(true);
		l.doClick();
	}
	class JButab extends JButton{

		private static final long serialVersionUID = 1L;

		JButab(String name, ActionListener a){
			super(name);
			setActionCommand(name);
			tabs.add(this);
			addActionListener(a);
			setBackground(Color.WHITE);
			setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
		}
	}
	public static void serializeAddress(List address) {

		FileOutputStream fout = null;
		ObjectOutputStream oos = null;

		try {

			fout = new FileOutputStream("C:/Users/Public/eclipse-workspace/LifeMGR/yay.list");
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
		((ListPanelType) pane.getBottomComponent()).tab.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
		((ListPanelType) pane.getBottomComponent()).tab.setBackground(new Color(240, 240, 240));
		if (e.getActionCommand().equals("List")) {
			pane.setBottomComponent(listpanel);
		}else if(e.getActionCommand().equals("Quick")){
			pane.setBottomComponent(quickpanel);
		}
		((ListPanelType) pane.getBottomComponent()).tab.setBackground(Color.WHITE);
		((ListPanelType) pane.getBottomComponent()).grabFocus();
		((ListPanelType) pane.getBottomComponent()).tab.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.BLACK));
		((ListPanelType) pane.getBottomComponent()).update();
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