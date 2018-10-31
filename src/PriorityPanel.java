import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

class PriorityPanel extends ListPanel implements MouseInputListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	List orig;
	public PriorityPanel(List list, JFrame frame){
		super(list, frame);
		this.orig = list;
		update();
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(Math.abs(click.x-e.getX())<5 && Math.abs(click.y-e.getY())<5){
			List l = list.get(g2, e.getX()-Settings.indent, e.getY()-Settings.indent-(int) scroll);
			orig.get(l).click(g2, e.getX()-Settings.indent*l.level());
			focus = orig.getfocus();
			list.setfocus(list.get(focus));
			update();
		}else if(!selectdrag){
			scroll -= click2.y-e.getY();
		}
		list.update();
		if(scroll<-Settings.line+getHeight()+(list.count()-list.countit()-1)*(1.0*Settings.line+Arith.lineheight(g2))) scroll = -Settings.line+getHeight()+(list.count()-list.countit()-1)*(1.0*Settings.line+Arith.lineheight(g2));
		repaint();
	}
	@Override
	void update() {
		super.update();
		list = new List(orig).prioitysort();
		list.update();
	}
}