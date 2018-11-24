import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.stream.Collectors;

import javax.swing.JFrame;

class StatsPanel extends ListPanelType {

    private static final long serialVersionUID = 1L;

    StatsPanel(List list, JFrame frame){
		super(list, frame);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for(int i = 0; i <= 9; i++){
            g2.setColor(Color.BLUE);
            if(list.find(i).size() == 0) g2.setColor(Color.MAGENTA);
            g2.fillOval(i%3*getWidth()/3, i/3*getHeight()/3, getWidth()/3, getHeight()/3);
            g2.setColor(Color.GREEN);
            g2.fillArc(i%3*getWidth()/3, i/3*getHeight()/3, getWidth()/3, getHeight()/3, 90, (int) (-360*list.find(i).stream().collect(Collectors.averagingDouble(n->{n.update(); return n.progress;}))));
            Font f = g2.getFont();
            g2.setFont(new Font("Ubuntu", Font.PLAIN, 150));
            g2.setColor(Color.BLACK);
            g2.drawString(i+"", i%3*getWidth()/3, (1+i/3)*getHeight()/3);
            g2.setFont(f);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

}