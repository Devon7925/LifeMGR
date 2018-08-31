import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

abstract class ListPanelType extends JPanel implements KeyListener{
	private static final long serialVersionUID = 1L;
	List list;
	JButton tab;
	ListPanelType(List list, JFrame frame){
		this.list = list;
		frame.addKeyListener(this);
	}
	@Override
	public abstract void keyTyped(KeyEvent e);
	@Override
	public abstract void keyPressed(KeyEvent e);
	@Override
	public abstract void keyReleased(KeyEvent e);

	void update(){
		list.update();
		repaint();
	}
}