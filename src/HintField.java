import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * HintField
 * ---------
 * A rounded text field that displays grey placeholder ("hint") text when it
 * is empty and unfocused. Used for the gig search bar.
 */
public class HintField extends JTextField {

    private final String hint;

    public HintField(String hint, int columns) {
        super(columns);
        this.hint = hint;
        setFont(UI.BODY);
        setForeground(UI.INK);
        setOpaque(false);
        setBorder(new RoundedBorder(12, UI.LINE, 10));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        UI.aa(g);
        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g.dispose();
        super.paintComponent(g0);

        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g0.create();
            UI.aa(g2);
            g2.setColor(UI.MUTED);
            g2.setFont(UI.BODY);
            Insets in = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(hint, in.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    /** A simple rounded border with inner padding. */
    static class RoundedBorder extends AbstractBorder {
        private final int arc, pad; private final Color color;
        RoundedBorder(int arc, Color color, int pad) { this.arc = arc; this.color = color; this.pad = pad; }
        public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
            Graphics2D g = (Graphics2D) g0.create();
            UI.aa(g);
            g.setColor(color);
            g.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g.dispose();
        }
        public Insets getBorderInsets(Component c) { return new Insets(pad, pad + 4, pad, pad + 4); }
        public Insets getBorderInsets(Component c, Insets i) {
            i.left = i.right = pad + 4; i.top = i.bottom = pad; return i;
        }
    }
}
