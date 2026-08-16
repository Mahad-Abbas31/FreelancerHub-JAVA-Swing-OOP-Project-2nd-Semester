import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * GigThumbnail
 * ------------
 * A graphical "cover image" for a gig, generated entirely with 2D graphics:
 * a category-coloured diagonal gradient, a soft pattern, a simple icon glyph
 * drawn for the category, and the category name. No image files required.
 */
public class GigThumbnail extends JComponent {

    private final String category;
    private final String title;
    private final Color c1, c2;

    public GigThumbnail(String category, String title, int w, int h) {
        this.category = category == null ? "General" : category;
        this.title = title == null ? "" : title;
        Color base = UI.colorFor(this.category.toLowerCase());
        this.c1 = base;
        this.c2 = UI.darker(base, 0.62f);
        setPreferredSize(new Dimension(w, h));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        UI.aa(g);
        int w = getWidth(), h = getHeight();

        // rounded clip (top corners only feel; we round all then card crops)
        Shape clip = new RoundRectangle2D.Float(0, 0, w, h, 16, 16);
        g.setClip(clip);

        // diagonal gradient
        g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
        g.fillRect(0, 0, w, h);

        // subtle decorative circles
        g.setColor(new Color(255, 255, 255, 28));
        g.fillOval(w - 60, -30, 110, 110);
        g.fillOval(-40, h - 50, 90, 90);

        // category glyph
        g.setColor(new Color(255, 255, 255, 235));
        drawGlyph(g, category, w / 2, h / 2 - 6, Math.min(w, h) / 3);

        // category label chip
        g.setFont(UI.SMALL_B);
        FontMetrics fm = g.getFontMetrics();
        String cat = category.toUpperCase();
        int cw = fm.stringWidth(cat) + 16;
        g.setColor(new Color(0, 0, 0, 45));
        g.fill(new RoundRectangle2D.Float(10, h - 26, cw, 18, 18, 18));
        g.setColor(Color.WHITE);
        g.drawString(cat, 18, h - 13);

        g.dispose();
    }

    /** Draws a simple line-art icon based on the category keyword. */
    private void drawGlyph(Graphics2D g, String cat, int cx, int cy, int r) {
        g.setStroke(new BasicStroke(Math.max(2f, r / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        String c = cat.toLowerCase();
        if (c.contains("design") || c.contains("logo") || c.contains("art")) {
            // pen / brush
            g.draw(new Line2D.Float(cx - r, cy + r, cx + r * 0.3f, cy - r * 0.4f));
            g.draw(new Ellipse2D.Float(cx + r * 0.2f, cy - r * 0.7f, r * 0.5f, r * 0.5f));
        } else if (c.contains("write") || c.contains("content") || c.contains("blog")) {
            // document lines
            g.drawRoundRect(cx - r, cy - r, 2 * r, 2 * (int) (r * 1.1), 10, 10);
            for (int i = 0; i < 4; i++)
                g.draw(new Line2D.Float(cx - r * 0.6f, cy - r * 0.6f + i * r * 0.45f,
                        cx + r * 0.6f, cy - r * 0.6f + i * r * 0.45f));
        } else if (c.contains("web") || c.contains("dev") || c.contains("code") || c.contains("program")) {
            // code brackets </>
            g.draw(new Line2D.Float(cx - r * 0.3f, cy - r * 0.6f, cx - r, cy));
            g.draw(new Line2D.Float(cx - r, cy, cx - r * 0.3f, cy + r * 0.6f));
            g.draw(new Line2D.Float(cx + r * 0.3f, cy - r * 0.6f, cx + r, cy));
            g.draw(new Line2D.Float(cx + r, cy, cx + r * 0.3f, cy + r * 0.6f));
        } else if (c.contains("video") || c.contains("anim") || c.contains("edit")) {
            // play triangle in circle
            g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
            Path2D p = new Path2D.Float();
            p.moveTo(cx - r * 0.3f, cy - r * 0.45f);
            p.lineTo(cx + r * 0.5f, cy);
            p.lineTo(cx - r * 0.3f, cy + r * 0.45f);
            p.closePath();
            g.fill(p);
        } else if (c.contains("market") || c.contains("seo") || c.contains("social")) {
            // rising trend line
            g.draw(new Line2D.Float(cx - r, cy + r * 0.6f, cx - r * 0.2f, cy - r * 0.2f));
            g.draw(new Line2D.Float(cx - r * 0.2f, cy - r * 0.2f, cx + r, cy - r * 0.8f));
        } else if (c.contains("music") || c.contains("audio") || c.contains("voice")) {
            // note
            g.fill(new Ellipse2D.Float(cx - r * 0.7f, cy + r * 0.2f, r * 0.6f, r * 0.45f));
            g.draw(new Line2D.Float(cx - r * 0.15f, cy + r * 0.4f, cx - r * 0.15f, cy - r * 0.7f));
            g.draw(new Line2D.Float(cx - r * 0.15f, cy - r * 0.7f, cx + r * 0.5f, cy - r * 0.5f));
        } else {
            // generic star/sparkle
            g.fill(UI.starShape(cx, cy, r, r * 0.42));
        }
    }
}
