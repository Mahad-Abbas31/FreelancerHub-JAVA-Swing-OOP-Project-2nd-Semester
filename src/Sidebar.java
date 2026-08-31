import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Sidebar
 * -------
 * The dark vertical navigation rail used by both dashboards. Shows the brand
 * at the top, a list of nav items (each with a hand-drawn icon and an active
 * highlight), and the logged-in user with a logout button at the bottom.
 */
public class Sidebar extends JPanel {

    /** One navigation entry. */
    public static class Item {
        final String key, label, icon;
        Item(String key, String label, String icon) { this.key = key; this.label = label; this.icon = icon; }
    }

    private final List<Item> items = new ArrayList<>();
    private final List<NavButton> buttons = new ArrayList<>();
    private String active;
    private final Consumer<String> onSelect;

    public Sidebar(String brand, User user, Consumer<String> onSelect, Runnable onLogout) {
        this.onSelect = onSelect;
        setPreferredSize(new Dimension(230, 100));
        setBackground(UI.SIDEBAR);
        setLayout(new BorderLayout());

        // ---- brand ----
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        UI.pad(top, 26, 22, 10, 22);

        JLabel logo = new JLabel("\u25C8 FreelanceHub");
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel role = new JLabel(user.getRole().toUpperCase() + " WORKSPACE");
        role.setFont(UI.TINY);
        role.setForeground(new Color(255, 255, 255, 120));
        role.setAlignmentX(LEFT_ALIGNMENT);

        top.add(logo);
        top.add(Box.createVerticalStrut(4));
        top.add(role);
        top.add(Box.createVerticalStrut(22));

        add(top, BorderLayout.NORTH);

        // ---- nav items container ----
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        UI.pad(nav, 0, 12, 0, 12);
        add(nav, BorderLayout.CENTER);
        this.navPanel = nav;

        // ---- user footer ----
        JPanel foot = new JPanel(new BorderLayout(10, 0));
        foot.setOpaque(false);
        UI.pad(foot, 14, 16, 18, 16);
        Avatar av = new Avatar(user.getFullName(), 38);
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(user.getFullName());
        name.setForeground(Color.WHITE);
        name.setFont(UI.SMALL_B);
        JLabel uname = new JLabel("@" + user.getUsername());
        uname.setForeground(new Color(255, 255, 255, 130));
        uname.setFont(UI.TINY);
        info.add(name);
        info.add(uname);

        RoundedButton logout = RoundedButton.outline("Logout", new Color(255, 255, 255, 200));
        logout.setFont(UI.SMALL_B);
        logout.setForeground(Color.WHITE);
        logout.addActionListener(e -> onLogout.run());

        JPanel footTop = new JPanel(new BorderLayout(10, 0));
        footTop.setOpaque(false);
        footTop.add(av, BorderLayout.WEST);
        footTop.add(info, BorderLayout.CENTER);

        JPanel footWrap = new JPanel();
        footWrap.setOpaque(false);
        footWrap.setLayout(new BoxLayout(footWrap, BoxLayout.Y_AXIS));
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        footTop.setAlignmentX(LEFT_ALIGNMENT);
        logout.setAlignmentX(LEFT_ALIGNMENT);
        UI.capHeight(logout, 36);
        footWrap.add(footTop);
        footWrap.add(Box.createVerticalStrut(12));
        footWrap.add(logout);
        UI.pad(footWrap, 14, 16, 18, 16);
        add(footWrap, BorderLayout.SOUTH);
    }

    private final JPanel navPanel;

    public Sidebar addItem(String key, String label, String icon) {
        Item it = new Item(key, label, icon);
        items.add(it);
        NavButton b = new NavButton(it);
        buttons.add(b);
        b.setAlignmentX(LEFT_ALIGNMENT);
        navPanel.add(b);
        navPanel.add(Box.createVerticalStrut(4));
        if (active == null) setActive(key);
        return this;
    }

    public void setActive(String key) {
        active = key;
        for (NavButton b : buttons) b.repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        UI.vGradient(g, 0, 0, getWidth(), getHeight(), UI.SIDEBAR, UI.darker(UI.SIDEBAR, 0.7f));
        g.dispose();
    }

    /** A single nav button: icon + label, with hover and active states. */
    private class NavButton extends JComponent {
        private final Item item;
        private boolean hover = false;

        NavButton(Item item) {
            this.item = item;
            setPreferredSize(new Dimension(206, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                public void mouseClicked(MouseEvent e) { setActive(item.key); onSelect.accept(item.key); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            UI.aa(g);
            int w = getWidth(), h = getHeight();
            boolean isActive = item.key.equals(active);
            if (isActive)      UI.fillRound(g, 0, 0, w, h, 10, UI.PRIMARY);
            else if (hover)    UI.fillRound(g, 0, 0, w, h, 10, UI.SIDEBAR_HOVER);

            Color fg = isActive ? Color.WHITE : new Color(255, 255, 255, 200);
            drawIcon(g, item.icon, 16, h / 2, 9, fg);

            g.setColor(fg);
            g.setFont(isActive ? UI.BOLD : UI.BODY);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(item.label, 46, (h - fm.getHeight()) / 2 + fm.getAscent());
            g.dispose();
        }

        private void drawIcon(Graphics2D g, String icon, int cx, int cy, int r, Color c) {
            g.setColor(c);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (icon) {
                case "home":
                    g.draw(new Line2D.Float(cx - r, cy, cx, cy - r));
                    g.draw(new Line2D.Float(cx, cy - r, cx + r, cy));
                    g.drawRect(cx - r + 2, cy, 2 * r - 4, r);
                    break;
                case "grid":
                    g.drawRect(cx - r, cy - r, r - 1, r - 1);
                    g.drawRect(cx + 1, cy - r, r - 1, r - 1);
                    g.drawRect(cx - r, cy + 1, r - 1, r - 1);
                    g.drawRect(cx + 1, cy + 1, r - 1, r - 1);
                    break;
                case "plus":
                    g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
                    g.draw(new Line2D.Float(cx - r / 2f, cy, cx + r / 2f, cy));
                    g.draw(new Line2D.Float(cx, cy - r / 2f, cx, cy + r / 2f));
                    break;
                case "bag":
                    g.drawRoundRect(cx - r, cy - r / 2, 2 * r, (int) (r * 1.6), 3, 3);
                    g.drawArc(cx - r / 2, cy - r, r, r, 0, 180);
                    break;
                case "star":
                    g.fill(UI.starShape(cx, cy, r, r * 0.42));
                    break;
                default:
                    g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
            }
        }
    }
}
