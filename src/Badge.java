import javax.swing.*;
import java.awt.*;

/**
 * Badge
 * -----
 * A small rounded "pill" showing a short label in a colour. Used for gig
 * categories and order-status indicators. Two styles: solid and soft (tinted).
 */
public class Badge extends JComponent {

    private final String text;
    private final Color color;
    private final boolean soft;

    public Badge(String text, Color color, boolean soft) {
        this.text = text == null ? "" : text;
        this.color = color;
        this.soft = soft;
        setFont(UI.SMALL_B);
        Dimension d = preferred();
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }

    public static Badge soft(String t, Color c)  { return new Badge(t, c, true); }
    public static Badge solid(String t, Color c) { return new Badge(t, c, false); }

    private Dimension preferred() {
        FontMetrics fm = getFontMetrics(UI.SMALL_B);
        int w = fm.stringWidth(text) + 22;
        return new Dimension(w, 22);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        UI.aa(g);
        int w = getWidth(), h = getHeight();
        if (soft) {
            Color bg = new Color(color.getRed(), color.getGreen(), color.getBlue(), 38);
            UI.fillRound(g, 0, 0, w, h, h, bg);
            g.setColor(UI.darker(color, 0.85f));
        } else {
            UI.fillRound(g, 0, 0, w, h, h, color);
            g.setColor(Color.WHITE);
        }
        g.setFont(UI.SMALL_B);
        FontMetrics fm = g.getFontMetrics();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, tx, ty);
        g.dispose();
    }
}
