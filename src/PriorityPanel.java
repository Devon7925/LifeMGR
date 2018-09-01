import java.awt.Color;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

class PriorityPanel extends ListPanel implements MouseInputListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	public PriorityPanel(List list, JFrame frame){
		super(list, frame);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		addMouseWheelListener(this);
		setBackground(Color.white);
		setFocusTraversalKeysEnabled(false);
		this.frame = frame;
		list.update();
		repaint();
	}
	@Override
	void update() {
		super.update();
		list = list.prioitysort();
	}
}