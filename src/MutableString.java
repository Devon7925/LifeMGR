import java.io.Serializable;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

class MutableString implements Serializable {
    private static final long serialVersionUID = 3L;
	private String value;
    MutableString(String s){
        value = s;
    }
    public void setValue(String val){value = val;}
    public String getValue(){return value;}
    public void append(String val){value+=val;}
    public int lineheight(Graphics2D g2) {
        FontMetrics metric = g2.getFontMetrics(g2.getFont());
        return metric.getAscent()-metric.getDescent()-metric.getLeading();
    }
    public int linewidth(Graphics2D g2) {
        FontMetrics metric = g2.getFontMetrics(g2.getFont());
        return metric.stringWidth(value);
    }
  }