import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * GigCard
 * -------
 * A marketplace-style gig card: graphical thumbnail on top, then seller
 * avatar + name, the gig title, a star rating, and a footer with the price
 * and an action button. Hovering lifts the card (border highlight).
 */
public class GigCard extends RoundCard {

    public interface Action { void run(Gig gig); }

    private final Gig gig;
    private boolean hover = false;

    public GigCard(Gig gig, Freelancer seller, String buttonText, Action onClick) {
        this.gig = gig;
        arc(16).shadow(true);
        setLayout(null);                 // absolute layout inside fixed-size card
        setPreferredSize(new Dimension(250, 300));

        int pad = 8;                     // shadow margin from RoundCard
        int innerW = 250 - pad * 2;

        // thumbnail
        GigThumbnail thumb = new GigThumbnail(gig.getCategory(), gig.getTitle(), innerW, 120);
        thumb.setBounds(pad, pad, innerW, 120);
        add(thumb);

        // seller row
        Avatar av = new Avatar(seller != null ? seller.getFullName() : gig.getFreelancerUsername(), 26);
        av.setBounds(pad + 12, pad + 132, 26, 26);
        add(av);

        JLabel seg = UI.label("@" + gig.getFreelancerUsername(), UI.SMALL_B, UI.INK);
        seg.setBounds(pad + 46, pad + 132, innerW - 60, 26);
        add(seg);

        // title (wrapped, 2 lines)
        JLabel title = new JLabel("<html><div style='width:" + (innerW - 24)
                + "px'>" + escape(gig.getTitle()) + "</div></html>");
        title.setFont(UI.H3);
        title.setForeground(UI.INK);
        title.setVerticalAlignment(SwingConstants.TOP);
        title.setBounds(pad + 12, pad + 164, innerW - 20, 46);
        add(title);

        // rating
        double rating = seller != null ? seller.getRating() : 0;
        int reviews = seller != null ? seller.getTotalReviews() : 0;
        StarRatingView stars = new StarRatingView(rating, reviews, 13, true);
        stars.setBounds(pad + 12, pad + 212, innerW - 24, 20);
        add(stars);

        // separator + footer
        JLabel from = UI.label("STARTING AT", UI.TINY, UI.MUTED);
        from.setBounds(pad + 12, pad + 240, 100, 14);
        add(from);

        JLabel price = UI.label("$" + trimPrice(gig.getPrice()), UI.H2, UI.INK);
        price.setBounds(pad + 12, pad + 252, 110, 26);
        add(price);

        RoundedButton order = RoundedButton.filled(buttonText, UI.PRIMARY);
        order.setFont(UI.SMALL_B);
        order.setBounds(innerW - 92 + pad, pad + 250, 96, 30);
        order.addActionListener(e -> onClick.run(gig));
        add(order);

        // hover detection across the whole card
        MouseAdapter ma = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            public void mouseExited(MouseEvent e) {
                if (!getBounds().contains(e.getPoint())) { hover = false; repaint(); }
            }
        };
        addMouseListener(ma);
    }

    private static String trimPrice(double p) {
        if (p == Math.floor(p)) return String.valueOf((long) p);
        return String.format("%.2f", p);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (hover) {
            Graphics2D g = (Graphics2D) g0.create();
            UI.aa(g);
            UI.drawRound(g, 8, 8, getWidth() - 16, getHeight() - 16, 16, UI.PRIMARY, 2f);
            g.dispose();
        }
    }
}
