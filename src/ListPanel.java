import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

class ListPanel extends ListPanelType implements MouseInputListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	Graphics2D g2 = (Graphics2D) getGraphics();
	double scroll = 0;
	Point click, click2 = new Point(0, 0);
	JFrame frame;
	boolean selectdrag = false;
	String path;

	public ListPanel(List list, JFrame frame, String path){
		super(list, frame);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		addMouseWheelListener(this);
		setBackground(Color.white);
		setFocusTraversalKeysEnabled(false);
		this.frame = frame;
		this.path = path;
		list.update();
		repaint();
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
		g2.setFont(Settings.font.deriveFont((float) Settings.fontsize));
        g2.translate(0, scroll);
		list.draw(g2, 0, new Point(Settings.indent, Settings.indent), list.hover(g2, click2.x, click2.y-(int) scroll));
        g2.translate(0, -scroll);
	}
	//region mouse
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		click2 = click = e.getPoint();
		selectdrag = list.get(g2, (int) click.getX(), (int) click.getY()-(int) scroll).equals(list.getfocus());
	}
	public void mouseReleased(MouseEvent e) {
		if(Math.abs(click.x-e.getX())<5 && Math.abs(click.y-e.getY())<5){
			list.get(g2, e.getX()-Settings.indent, e.getY()-Settings.indent-(int) scroll).click(g2, e.getX());
		}else if(!selectdrag){
			scroll -= click2.y-e.getY();
		}
		list.update();
		if(scroll<-Settings.line+getHeight()+(-list.countit())*(1.0*Settings.line+Arith.lineheight(g2))) scroll = -Settings.line+getHeight()+(-list.countit())*(1.0*Settings.line+Arith.lineheight(g2));
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(selectdrag){
			List temp = list.getfocus();
			List temp2 = list.get(g2, (int) e.getX(), (int) e.getY()-(int) scroll);
			if(temp == temp2 && e.getX()-click.getX() > Settings.indent){
				int i = temp.holder.items.indexOf(temp);
				if(i > 0){
					click.x = e.getX();
					list.remFrom(temp.holder, temp);
					list.addTo(temp.holder.items.get(i-1), temp);
				}
			}else if(temp == temp2 && click.getX()-e.getX() > Settings.indent){
				if(temp.holder.holder != null){
					int i = temp.holder.holder.items.indexOf(temp.holder);
					click.x = e.getX();
					list.remFrom(temp.holder, temp);
					list.addTo(temp.holder.holder, i+1, temp);
				}
			}else if(temp.level() == temp2.level() && temp.holder == temp2.holder){
				if(click2.getY() < e.getY()){
					list.set(temp2, temp);
					list.set(list.getfocus(), temp2);
				}else{
					list.set(list.getfocus(), temp2);
					list.set(temp2, temp);
				}
			}else if(temp.level() > temp2.level() && temp.level() > 1){
				if(click2.getY() > e.getY()){
					List foc = list.getfocus();
					list.remFrom(foc.holder, foc);
					list.addTo(foc.holder.holder, foc.holder.holder.items.indexOf(foc.holder), foc);
				}else{
					List foc = list.getfocus();
					list.remFrom(foc.holder, foc);
					list.addTo(foc.holder.holder, foc.holder.holder.items.indexOf(foc.holder)+1, foc);
				}
			}
		}else if(click2.getY()-e.getY()>0 || scroll<0) scroll -= click2.getY()-e.getY();
		click2 = e.getPoint();
		list.update();
		repaint();
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		click2 = e.getPoint();
		repaint();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if((e.getWheelRotation()>0 && scroll>-Settings.line+getHeight()+(-list.countit())*(1.0*Settings.line+Arith.lineheight(g2))) || (scroll<0 && e.getWheelRotation()<0))scroll -= Settings.scroll*e.getWheelRotation();
		repaint();
	}
	//endregion
	//region key
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {
		ListInstance foc = list.getfocus();
		if(e.getKeyCode() == KeyEvent.VK_F1){
			list.clear();
		}else if(e.getKeyCode() == KeyEvent.VK_F2){
			if(!foc.persistant){
				foc.setpersistant();
			}else{
				foc.persistant = false;
			}
		}else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			list.resetfocus();
		}else if(foc != null){
			if(e.getKeyCode() == KeyEvent.VK_TAB){
				if(e.isControlDown()) ((ListInstance) foc.holder).setfocus((ListInstance) foc.prev());
				else if(foc.holder != null)((ListInstance) foc.holder).setfocus((ListInstance) foc.propnext());
				else foc.setfocus((ListInstance) foc.propnext());
			}else if(e.getKeyCode() == KeyEvent.VK_ENTER){
				if(e.isControlDown()){
					ListInstance l = new ListInstance("", foc);
					list.setfocus(l);
					foc.add(0, l);
				}else if(foc.holder != null){
					ListInstance l = new ListInstance("", foc.holder);
					list.setfocus(l);
					foc.holder.add(foc.holder.items.indexOf(foc)+1, l);
				}
				scroll -= Arith.lineheight(g2)+Settings.line;
			}else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				int i = foc.holder.items.indexOf(foc)-1;
				if(i < foc.holder.items.size() && i >= 0){
					foc.holder.remove(foc);
					foc.holder.items.get(i).add(foc);
				}
			}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
				if(foc.holder.holder != null){
					foc.holder.remove(foc);
					foc.holder.holder.add(foc.holder.holder.items.indexOf(foc.holder)+1, foc);
				}
			}else if(e.getKeyCode() == KeyEvent.VK_UP){
				if(foc.holder.items.indexOf(foc)-1 >= 0)Collections.swap(foc.holder.items, foc.holder.items.indexOf(foc), foc.holder.items.indexOf(foc)-1);
			}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
				if(foc.holder.items.indexOf(foc)+1 < foc.holder.items.size())Collections.swap(foc.holder.items, foc.holder.items.indexOf(foc), foc.holder.items.indexOf(foc)+1);
			}else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
				foc.name.setValue(foc.name.getValue().substring(0, foc.name.getValue().length()-1));
			}else if(e.isControlDown()){
				if((e.getKeyChar()+"").matches("\\d")){
					foc.setpriority(Integer.parseInt(e.getKeyChar()+""));
				}
			}else if(isPrintableChar(e.getKeyChar())){
				foc.name.setValue(foc.name.getValue() + e.getKeyChar());
				int a = (1+foc.level())*Settings.indent+Arith.linewidth(g2, foc.name)+Settings.linespace+5*Arith.lineheight(g2);
				frame.setSize(frame.getWidth()>a?frame.getWidth():a, frame.getHeight());
			}
		}
		list.update();
		Main.serializeAddress(new List(list), path);
		repaint();
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
	public boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }
	//endregion
	@Override
	void update() {
		super.update();
	}
}