import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

class PriorityPanel extends ListPanel implements MouseInputListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	public PriorityPanel(List list, JFrame frame, String path){
		super(list, frame, path);
		update();
	}
	@Override
	void update() {
		list = new ListInstance(orig).prioitysort();
		super.update();
	}
}