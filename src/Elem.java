import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.TextAttribute;
import java.io.Serializable;
import java.util.Hashtable;

public class Elem implements Serializable{
	private static final long serialVersionUID = 1L;
	double progress = 0;
    MutableString name;
    boolean selected = false;
    List holder;
    boolean persistant = false;
    public Elem(String name, List holder){
        this.name = new MutableString(name);
        this.holder = holder;
        progress = 0;
    }
    public int draw(Graphics2D g2, int indent, Point loc, List hovered) {
        g2.translate(loc.x+Settings.indent*indent, loc.y);
        g2.setColor(Color.blue);
        int x = 0;
        g2.fillOval(0, 0, Arith.lineheight(g2), Arith.lineheight(g2));
        g2.setColor(Color.green);
        g2.fillArc(0, 0, (int) (1.1*Arith.lineheight(g2)), (int) (1.1*Arith.lineheight(g2)), 90, (int) (-360*progress));
        g2.setColor(Color.white);
        if(persistant)
            g2.fillOval(5*Arith.lineheight(g2)/12, 5*Arith.lineheight(g2)/12, Arith.lineheight(g2)/3, Arith.lineheight(g2)/3);
        x += Arith.lineheight(g2)+Settings.linespace;
        g2.setColor(Color.black);
        Font font = g2.getFont();
        if(progress == 1){
            g2.setColor(Color.gray);
            Hashtable<TextAttribute, Object> attributes = new Hashtable<TextAttribute, Object>();
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            g2.setFont(font.deriveFont(attributes)); 
        }
        if(selected)g2.setColor(Color.magenta);
        g2.drawString(name.getValue(), x, Arith.lineheight(g2));
        g2.setFont(font);
        if(this == hovered){
            x += Settings.linespace+((Arith.linewidth(g2, name)>Settings.buttondist)?Arith.linewidth(g2, name):Settings.buttondist);
            g2.fillOval(x, (int) (Arith.lineheight(g2)/6.0), (int) (Arith.lineheight(g2)/1.5), (int) (Arith.lineheight(g2)/1.5));
            g2.setColor(Color.red);
            g2.drawLine(x, Arith.lineheight(g2), (int) (2.0/3.0*Arith.lineheight(g2)+x), 0);
            g2.drawLine(x, 0, (int) (2.0/3.0*Arith.lineheight(g2)+x), Arith.lineheight(g2));
            x += Arith.lineheight(g2);
            g2.setColor(Color.black);
            g2.fillOval(x, (int) (Arith.lineheight(g2)/6.0), (int) (Arith.lineheight(g2)/1.5), (int) (Arith.lineheight(g2)/1.5));
            g2.setColor(Color.green);
            g2.drawLine(x, Arith.lineheight(g2)/2, (int) (2.0/3.0*Arith.lineheight(g2)+x), Arith.lineheight(g2)/2);
            g2.drawLine((int) (1.0/3.0*Arith.lineheight(g2)+x), 0, (int) (1.0/3.0*Arith.lineheight(g2)+x), Arith.lineheight(g2));
        }
        g2.setColor(Color.black);
        g2.translate(-loc.x-Settings.indent*indent, -loc.y);
        return 1;
    }
    public void check(boolean b){
        progress = b?1:0;
    }
    public int count() {
        return 1;
    }
    public int countit() {
        return 1;
    }
}