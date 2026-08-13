import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * FreelancerDashboard
 * -------------------
 * The freelancer workspace. A dark Sidebar switches between four pages:
 *   - Dashboard : stat cards (gigs, orders, earnings, rating) + reviews list.
 *   - Create Gig: a styled form to publish a new gig (with validation).
 *   - My Gigs   : a responsive grid of the freelancer's own GigCards.
 *   - Orders    : incoming OrderRows with status actions (Accept, Deliver, Cancel).
 *
 * Main functionalities: Create Gig, View Gigs, Manage Orders, View Reviews.
 */
public class FreelancerDashboard extends JPanel {

    private final MainFrame app;
    private final Freelancer me;

    private final CardLayout pages = new CardLayout();
    private final JPanel content = new JPanel(pages);

    private JPanel statRow;
    private JPanel reviewsList;
    private JPanel gigGrid;
    private JPanel ordersList;

    public FreelancerDashboard(MainFrame app, Freelancer me) {
        this.app = app;
        this.me = me;
        setLayout(new BorderLayout());
        setBackground(UI.PAGE);

        Sidebar sidebar = new Sidebar("FreelanceHub", me, this::navigate, app::logout);
        sidebar.addItem("home",   "Dashboard",  "home");
        sidebar.addItem("create", "Create Gig", "plus");
        sidebar.addItem("gigs",   "My Gigs",    "grid");
        sidebar.addItem("orders", "Orders",     "bag");
        add(sidebar, BorderLayout.WEST);

        content.setBackground(UI.PAGE);
        content.add(buildHome(),   "home");
        content.add(buildCreate(), "create");
        content.add(buildGigs(),   "gigs");
        content.add(buildOrders(), "orders");
        add(content, BorderLayout.CENTER);

        refreshHome();
        refreshGigs();
        refreshOrders();
    }

    private void navigate(String key) {
        switch (key) {
            case "home":   refreshHome();   break;
            case "gigs":   refreshGigs();   break;
            case "orders": refreshOrders(); break;
        }
        pages.show(content, key);
    }

    // ============ HOME / DASHBOARD ============
    private JComponent buildHome() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(UI.PAGE);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        UI.pad(header, 28, 32, 4, 32);
        JLabel h = UI.label("Dashboard", UI.H1, UI.INK);
        h.setAlignmentX(LEFT_ALIGNMENT);
        JLabel s = UI.label("Welcome back, " + me.getFullName() + " \uD83D\uDC4B", UI.BODY, UI.MUTED);
        s.setAlignmentX(LEFT_ALIGNMENT);
        header.add(h); header.add(Box.createVerticalStrut(4)); header.add(s);
        page.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setBackground(UI.PAGE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        UI.pad(body, 16, 26, 26, 26);

        statRow = new JPanel(new WrapLayout(FlowLayout.LEFT, 16, 16));
        statRow.setOpaque(false);
        statRow.setAlignmentX(LEFT_ALIGNMENT);
        body.add(statRow);

        JLabel rev = UI.label("Recent Reviews", UI.H2, UI.INK);
        rev.setAlignmentX(LEFT_ALIGNMENT);
        rev.setBorder(BorderFactory.createEmptyBorder(18, 8, 10, 0));
        body.add(rev);

        reviewsList = new JPanel();
        reviewsList.setOpaque(false);
        reviewsList.setLayout(new BoxLayout(reviewsList, BoxLayout.Y_AXIS));
        reviewsList.setAlignmentX(LEFT_ALIGNMENT);
        body.add(reviewsList);

        JScrollPane scroll = new JScrollPane(body,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getViewport().setBackground(UI.PAGE);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private void refreshHome() {
        if (statRow == null) return;
        List<Gig> gigs = app.getData().getGigsByFreelancer(me.getUsername());
        List<Order> orders = app.getData().getOrdersByFreelancer(me.getUsername());

        int active = 0;
        double earnings = 0;
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.IN_PROGRESS || o.getStatus() == OrderStatus.PENDING) active++;
            if (o.getStatus() == OrderStatus.COMPLETED) earnings += o.getPrice();
        }

        statRow.removeAll();
        statRow.add(new StatCard(String.valueOf(gigs.size()), "Published Gigs", UI.INFO, "bag"));
        statRow.add(new StatCard(String.valueOf(active), "Active Orders", UI.ACCENT, "check"));
        statRow.add(new StatCard("$" + trim(earnings), "Total Earnings", UI.PRIMARY, "cash"));
        statRow.add(new StatCard(me.getTotalReviews() == 0 ? "New"
                : String.format("%.1f", me.getRating()), "Avg. Rating", UI.STAR, "star"));

        reviewsList.removeAll();
        List<Review> reviews = app.getData().getReviewsForFreelancer(me.getUsername());
        if (reviews.isEmpty()) {
            reviewsList.add(UI.label("No reviews yet \u2014 complete some orders to earn them!",
                    UI.BODY, UI.MUTED));
        } else {
            for (Review r : reviews) {
                reviewsList.add(buildReviewCard(r));
                reviewsList.add(Box.createVerticalStrut(10));
            }
        }
        statRow.revalidate(); statRow.repaint();
        reviewsList.revalidate(); reviewsList.repaint();
    }

    private RoundCard buildReviewCard(Review r) {
        RoundCard c = new RoundCard();
        c.arc(14).shadow(true);
        c.setLayout(new BorderLayout(12, 0));
        c.contentPadding(12, 14, 12, 14);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        c.setPreferredSize(new Dimension(600, 86));

        Avatar av = new Avatar(r.getClientUsername(), 40);
        c.add(av, BorderLayout.WEST);

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.add(UI.label("@" + r.getClientUsername(), UI.SMALL_B, UI.INK));
        top.add(new StarRatingView(r.getStars(), 0, 12, false));
        JLabel comment = UI.label("\u201C" + r.getComment() + "\u201D", UI.BODY, UI.MUTED);
        comment.setAlignmentX(LEFT_ALIGNMENT);
        txt.add(top);
        txt.add(Box.createVerticalStrut(4));
        txt.add(comment);
        c.add(txt, BorderLayout.CENTER);
        return c;
    }

    // ============ CREATE GIG ============
    private JComponent buildCreate() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(UI.PAGE);

        RoundCard card = new RoundCard();
        card.arc(18).shadow(true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.contentPadding(24, 28, 24, 28);
        card.setPreferredSize(new Dimension(560, 560));

        JLabel title = UI.label("Publish a New Gig", UI.H1, UI.INK);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = UI.label("Describe the service you're offering", UI.BODY, UI.MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JTextField titleF = field();
        JTextField catF   = field();
        JTextArea  descF  = new JTextArea(4, 10);
        descF.setLineWrap(true); descF.setWrapStyleWord(true);
        descF.setFont(UI.BODY);
        descF.setBorder(BorderFactory.createCompoundBorder(
                new HintField.RoundedBorder(10, UI.LINE, 8),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        JScrollPane descScroll = new JScrollPane(descF);
        descScroll.setBorder(null);
        descScroll.setAlignmentX(LEFT_ALIGNMENT);
        UI.capHeight(descScroll, 90);
        JTextField priceF = field();
        JTextField daysF  = field();

        RoundedButton publish = RoundedButton.filled("Publish Gig", UI.PRIMARY);
        publish.setAlignmentX(LEFT_ALIGNMENT);
        UI.capHeight(publish, 46);

        card.add(title);
        card.add(Box.createVerticalStrut(2));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(lbl("Gig Title (e.g. I will design a modern logo)")); card.add(titleF); card.add(g());
        card.add(lbl("Category (e.g. Logo Design, Web Dev, Writing)")); card.add(catF); card.add(g());
        card.add(lbl("Description")); card.add(descScroll); card.add(g());

        JPanel twoCol = new JPanel(new GridLayout(1, 2, 14, 0));
        twoCol.setOpaque(false);
        twoCol.setAlignmentX(LEFT_ALIGNMENT);
        UI.capHeight(twoCol, 64);
        JPanel pc = new JPanel(); pc.setOpaque(false); pc.setLayout(new BoxLayout(pc, BoxLayout.Y_AXIS));
        pc.add(lbl("Price ($)")); pc.add(priceF);
        JPanel dc = new JPanel(); dc.setOpaque(false); dc.setLayout(new BoxLayout(dc, BoxLayout.Y_AXIS));
        dc.add(lbl("Delivery (days)")); dc.add(daysF);
        twoCol.add(pc); twoCol.add(dc);
        card.add(twoCol);
        card.add(Box.createVerticalStrut(20));
        card.add(publish);

        publish.addActionListener(e -> {
            String t = titleF.getText().trim(), cat = catF.getText().trim(),
                    d = descF.getText().trim(), ps = priceF.getText().trim(), ds = daysF.getText().trim();
            if (t.isEmpty() || cat.isEmpty() || d.isEmpty() || ps.isEmpty() || ds.isEmpty()) {
                warn("Please fill in every field."); return;
            }
            double price; int days;
            try { price = Double.parseDouble(ps); days = Integer.parseInt(ds); }
            catch (NumberFormatException ex) { err("Price and delivery days must be numbers."); return; }
            if (price <= 0 || days <= 0) { warn("Price and days must be greater than zero."); return; }

            app.getData().addGig(new Gig(app.getData().getNextGigId(),
                    DataManager.clean(t), DataManager.clean(d), DataManager.clean(cat),
                    price, days, me.getUsername()));
            JOptionPane.showMessageDialog(this, "Gig published successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            titleF.setText(""); catF.setText(""); descF.setText("");
            priceF.setText(""); daysF.setText("");
            refreshGigs(); refreshHome();
        });

        page.add(card);
        return new JScrollPane(page);
    }

    // ============ MY GIGS ============
    private JComponent buildGigs() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(UI.PAGE);
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        UI.pad(header, 28, 32, 8, 32);
        JLabel h = UI.label("My Gigs", UI.H1, UI.INK);
        h.setAlignmentX(LEFT_ALIGNMENT);
        JLabel s = UI.label("Services you currently offer", UI.BODY, UI.MUTED);
        s.setAlignmentX(LEFT_ALIGNMENT);
        header.add(h); header.add(Box.createVerticalStrut(4)); header.add(s);
        page.add(header, BorderLayout.NORTH);

        gigGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 18, 18));
        gigGrid.setBackground(UI.PAGE);
        UI.pad(gigGrid, 14, 26, 26, 26);
        JScrollPane scroll = new JScrollPane(gigGrid,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getViewport().setBackground(UI.PAGE);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private void refreshGigs() {
        if (gigGrid == null) return;
        gigGrid.removeAll();
        List<Gig> gigs = app.getData().getGigsByFreelancer(me.getUsername());
        if (gigs.isEmpty()) {
            gigGrid.add(UI.label("You haven't published any gigs yet. Use 'Create Gig' to add one.",
                    UI.BODY, UI.MUTED));
        }
        for (Gig g : gigs) {
            gigGrid.add(new GigCard(g, me, "View", gig ->
                    JOptionPane.showMessageDialog(this,
                            "Title: " + gig.getTitle()
                            + "\nCategory: " + gig.getCategory()
                            + "\nPrice: $" + gig.getPrice()
                            + "\nDelivery: " + gig.getDeliveryDays() + " days\n\n"
                            + gig.getDescription(),
                            "Gig Details", JOptionPane.INFORMATION_MESSAGE)));
        }
        gigGrid.revalidate();
        gigGrid.repaint();
    }

    // ============ ORDERS ============
    private JComponent buildOrders() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(UI.PAGE);
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        UI.pad(header, 28, 32, 8, 32);
        JLabel h = UI.label("Incoming Orders", UI.H1, UI.INK);
        h.setAlignmentX(LEFT_ALIGNMENT);
        JLabel s = UI.label("Accept work, deliver it, and get paid", UI.BODY, UI.MUTED);
        s.setAlignmentX(LEFT_ALIGNMENT);
        header.add(h); header.add(Box.createVerticalStrut(4)); header.add(s);
        page.add(header, BorderLayout.NORTH);

        ordersList = new JPanel();
        ordersList.setBackground(UI.PAGE);
        ordersList.setLayout(new BoxLayout(ordersList, BoxLayout.Y_AXIS));
        UI.pad(ordersList, 12, 26, 26, 26);
        JScrollPane scroll = new JScrollPane(ordersList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getViewport().setBackground(UI.PAGE);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private void refreshOrders() {
        if (ordersList == null) return;
        ordersList.removeAll();
        List<Order> orders = app.getData().getOrdersByFreelancer(me.getUsername());
        if (orders.isEmpty()) {
            ordersList.add(UI.label("No orders yet. Publish attractive gigs to get hired!",
                    UI.BODY, UI.MUTED));
        }
        for (Order o : orders) {
            ordersList.add(buildOrderRow(o));
            ordersList.add(Box.createVerticalStrut(12));
        }
        ordersList.revalidate();
        ordersList.repaint();
    }

    private OrderRow buildOrderRow(Order o) {
        java.util.List<JComponent> acts = new java.util.ArrayList<>();
        if (o.getStatus() == OrderStatus.PENDING) {
            RoundedButton accept = RoundedButton.filled("Accept", UI.PRIMARY);
            accept.setFont(UI.SMALL_B);
            accept.addActionListener(e -> { o.setStatus(OrderStatus.IN_PROGRESS);
                app.getData().saveOrders(); refreshOrders(); refreshHome(); });
            acts.add(accept);

            RoundedButton decline = RoundedButton.outline("Decline", UI.DANGER);
            decline.setFont(UI.SMALL_B);
            decline.addActionListener(e -> { o.setStatus(OrderStatus.CANCELLED);
                app.getData().saveOrders(); refreshOrders(); refreshHome(); });
            acts.add(decline);
        } else if (o.getStatus() == OrderStatus.IN_PROGRESS) {
            RoundedButton deliver = RoundedButton.filled("Mark Delivered", UI.PRIMARY);
            deliver.setFont(UI.SMALL_B);
            deliver.addActionListener(e -> { o.setStatus(OrderStatus.DELIVERED);
                app.getData().saveOrders(); refreshOrders(); refreshHome(); });
            acts.add(deliver);
        } else if (o.getStatus() == OrderStatus.DELIVERED) {
            acts.add(UI.label("Awaiting client confirmation", UI.SMALL, UI.MUTED));
        }
        return new OrderRow(o, o.getClientUsername(), "Client:",
                acts.toArray(new JComponent[0]));
    }

    // ---- helpers ----
    private JTextField field() {
        JTextField t = new JTextField();
        t.setFont(UI.BODY);
        t.setBackground(Color.WHITE);
        t.setBorder(BorderFactory.createCompoundBorder(
                new HintField.RoundedBorder(10, UI.LINE, 9),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        t.setAlignmentX(LEFT_ALIGNMENT);
        UI.capHeight(t, 40);
        return t;
    }
    private JLabel lbl(String t) {
        JLabel l = UI.label(t, UI.SMALL_B, UI.INK);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 0));
        return l;
    }
    private Component g() { return Box.createVerticalStrut(10); }
    private static String trim(double p) {
        if (p == Math.floor(p)) return String.valueOf((long) p);
        return String.format("%.2f", p);
    }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Check your input", JOptionPane.WARNING_MESSAGE); }
    private void err(String m)  { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
