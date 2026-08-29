import javax.swing.*;
import java.awt.*;

/**
 * RoundCard
 * ---------
 * A rounded white panel with a soft drop shadow, used as the container for
 * most pieces of content (stat cards, forms, gig cards). Custom painted.
 */
public class RoundCard extends JPanel {

    private int arc = 16;
    private boolean withShadow = true;
    private Color fill = UI.CARD;

    public RoundCard() {
        setOpaque(false);
    }

    public RoundCard(LayoutManager lm) {
        this();
        setLayout(lm);
    }

    public RoundCard arc(int a)              { this.arc = a; return this; }
    public RoundCard shadow(boolean s)       { this.withShadow = s; return this; }
    public RoundCard fill(Color c)           { this.fill = c; return this; }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        UI.aa(g);
        int pad = withShadow ? 8 : 0;
        int w = getWidth() - pad * 2;
        int h = getHeight() - pad * 2;
        if (withShadow) UI.shadow(g, pad, pad, w, h, arc);
        UI.fillRound(g, pad, pad, w, h, arc, fill);
        g.dispose();
        super.paintComponent(g0);
    }

    /** Inset so children don't draw over the shadow margin. */
    public void contentPadding(int t, int l, int b, int r) {
        int s = withShadow ? 8 : 0;
        setBorder(BorderFactory.createEmptyBorder(t + s, l + s, b + s, r + s));
    }
}
