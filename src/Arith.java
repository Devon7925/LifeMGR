import java.awt.FontMetrics;
import java.awt.Graphics2D;

class Arith{
    public static int lineheight(Graphics2D g2) {
        FontMetrics metric = g2.getFontMetrics(g2.getFont());
        return metric.getAscent()-metric.getDescent()-metric.getLeading();
    }
    public static int linewidth(Graphics2D g2, MutableString s) {
        FontMetrics metric = g2.getFontMetrics(g2.getFont());
        return metric.stringWidth(s.getValue());
    }
}