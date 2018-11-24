import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

class QuickPanel extends ListPanelType implements Runnable{
	private static final long serialVersionUID = 1L;
	List active, oldactive;
	boolean h = false;
	double anim = 1;
	double oldprogress;
	public QuickPanel(List list, JFrame frame){
		super(list, frame);
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Drawer d = new Drawer(new Rectangle2D.Double(0, 0, getWidth(), getHeight()), g2);
		d.setFont(new Font("TimesRoman", Font.PLAIN, 50));
		if(active != null) {
			double len = active.name.getValue().length()*0.01;
			d.setColor(new Color(100, 100, 255));
			d.drawEllipCent(new Rectangle2D.Double(1.5-anim, 0.5, (0.5+len), 0.5));
			d.setColor(Color.white);
			if(active.persistant) d.drawEllipCent(new Rectangle2D.Double(1.5-anim, 0.35, 0.05, 0.05));
			d.setColor(Color.black);
			d.drawString(active.name.getValue(), 1.5-anim, 0.5);
			d.setColor(Color.black);
			if(active.holder != null){
				d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				double x = (oldactive.holder == active.holder)?-0.5:1.5-anim;
				len = active.holder.name.getValue().length()*0.01;
				d.setColor(new Color(100, 100, 255));
				d.drawEllipCent(new Rectangle2D.Double(x, 0.15, 0.25+len, 0.1));
				d.setColor(Color.GREEN);
				d.drawEllipCent(new Rectangle2D.Double(x, 0.15, active.holder.progress*(0.25+len), active.holder.progress*0.1));
				d.setColor(Color.black);
				d.drawString(active.holder.name.getValue(), x, 0.15);
				d.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			}
		}
		if(oldactive != null) {
			double len = oldactive.name.getValue().length()*0.01;
			d.setColor(new Color(100, 100, 255));
			d.drawEllipCent(new Rectangle2D.Double(0.5-anim, 0.5, 0.5+len, 0.5));
			d.setColor(Color.green);
			if(oldactive.progress > 0.5) d.drawEllipCent(new Rectangle2D.Double(0.5-anim, 0.5, 1.2*anim*0.5, 1.2*anim*0.5));
			d.setColor(Color.white);
			if(oldactive.persistant) d.drawEllipCent(new Rectangle2D.Double(0.5-anim, 0.35, 0.05, 0.05));
			d.setColor(Color.black);
			d.drawString(oldactive.name.getValue(), 0.5-anim, 0.5);
			if(oldactive.holder != null){
				d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				double x = (oldactive.holder == active.holder)?0.5:0.5-anim;
				len = oldactive.holder.name.getValue().length()*0.01;
				d.setColor(new Color(100, 100, 255));
				d.drawEllipCent(new Rectangle2D.Double(x, 0.15, 0.25+len, 0.1));
				d.setColor(Color.GREEN);
				double val = oldprogress+(oldactive.holder.progress-oldprogress)*anim;
				d.drawEllipCent(new Rectangle2D.Double(x, 0.15, val*(0.25+len), val*0.1));
				d.setColor(Color.black);
				d.drawString(oldactive.holder.name.getValue(), x, 0.15);
				d.setFont(new Font("TimesRoman", Font.PLAIN, 50));
			}
		}
	}
	
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_F1){
			list.clear();
		}else if(e.getKeyCode() == KeyEvent.VK_SPACE){
			if(active != null) {
				if(active.holder != null)oldprogress = active.holder.progress;
				active.check(true);
			}
			list.update();
			new Thread(this).start();
			oldactive = active;
			active = list.getFirst();
		}else if(e.getKeyCode() == KeyEvent.VK_S){
			if(active.holder != null)oldprogress = active.holder.progress;
			list.update();
			new Thread(this).start();
			oldactive = active;
			do{
				if(active != null) active = 
					list.getNext(
						list.get(active));
				else active = list;
			}while(active.getFirst() == null);
			if(active != null) active = active.getFirst();
		}
		repaint();
	}
	@Override
	public void run() {
		anim = 0;
		while(anim < 1){
			anim += 0.04;
			repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	void update() {
		super.update();
		list = new ListInstance(orig).prioitysort();
		oldactive = active = list.getFirst();
	}
}