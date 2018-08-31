import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

class JButab extends JButton{

    private static final long serialVersionUID = 1L;
    ListPanelType panel;

    JButab(String name, ActionListener a, ListPanelType panel, JPanel tabs){
        super(name);
        setActionCommand(name);
        tabs.add(this);
        this.panel = panel;
        panel.tab = this;
        addActionListener(a);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
        setBackground(new Color(240, 240, 240));
    }
}