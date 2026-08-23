import javax.swing.*;
import java.awt.*;

/**
 * OrderRow
 * --------
 * A horizontal card representing a single order in a list: shows the gig
 * title, the other party (with avatar), the price, a coloured status badge
 * and any action buttons supplied by the caller.
 */
public class OrderRow extends RoundCard {

    public OrderRow(Order o, String otherPartyName, String otherPartyLabel,
                    JComponent[] actions) {
        arc(14).shadow(true);
        setLayout(new BorderLayout(14, 0));
        contentPadding(12, 16, 12, 16);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));
        setPreferredSize(new Dimension(700, 104));

        // left: avatar + texts
        JPanel left = new JPanel(new BorderLayout(12, 0));
        left.setOpaque(false);
        Avatar av = new Avatar(otherPartyName, 44);
        left.add(av, BorderLayout.WEST);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        JLabel title = UI.label("#" + o.getId() + "  ·  " + o.getGigTitle(), UI.H3, UI.INK);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = UI.label(otherPartyLabel + " " + otherPartyName + "   ·   " + o.getDate(),
                UI.SMALL, UI.MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(LEFT_ALIGNMENT);
        chips.add(Badge.solid(o.getStatus().label(), UI.statusColor(o.getStatus())));
        chips.add(UI.label("$" + trim(o.getPrice()), UI.BOLD, UI.INK));

        texts.add(title);
        texts.add(Box.createVerticalStrut(3));
        texts.add(sub);
        texts.add(Box.createVerticalStrut(5));
        texts.add(chips);
        left.add(texts, BorderLayout.CENTER);

        add(left, BorderLayout.CENTER);

        // right: actions
        if (actions != null && actions.length > 0) {
            JPanel act = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            act.setOpaque(false);
            for (JComponent c : actions) act.add(c);
            add(act, BorderLayout.EAST);
        }
    }

    private static String trim(double p) {
        if (p == Math.floor(p)) return String.valueOf((long) p);
        return String.format("%.2f", p);
    }
}
