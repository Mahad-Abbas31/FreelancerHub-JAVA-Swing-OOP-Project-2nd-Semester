import javax.swing.*;
import java.awt.*;

/**
 * Avatar
 * ------
 * A circular avatar showing a person's initials on a colourful gradient.
 * The colour is derived deterministically from the name, so each user keeps
 * a consistent avatar. Pure 2D-graphics, no image files needed.
 */
public class Avatar extends JComponent {

    private final String initials;
    private final Color color;
    private final int size;

    public Avatar(String name, int size) {
        this.size = size;
        this.initials = initialsOf(name);
        this.color = UI.colorFor(name);
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    private static String initialsOf(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        String s = String.valueOf(Character.toUpperCase(parts[0].charAt(0)));
        if (parts.length > 1) s += Character.toUpperCase(parts[1].charAt(0));
        return s;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        UI.aa(g);
        g.setPaint(new GradientPaint(0, 0, color, size, size, UI.darker(color, 0.7f)));
        g.fillOval(0, 0, size, size);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, (int) (size * 0.42)));
        FontMetrics fm = g.getFontMetrics();
        int tx = (size - fm.stringWidth(initials)) / 2;
        int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(initials, tx, ty);
        g.dispose();
    }
}
