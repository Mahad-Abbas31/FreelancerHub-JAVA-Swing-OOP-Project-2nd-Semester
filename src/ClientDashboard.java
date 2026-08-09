import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * ClientDashboard
 * ---------------
 * The client workspace. A dark Sidebar on the left switches between two
 * content pages (CardLayout):
 *   - Browse : a search bar + a responsive grid of GigCards.
 *   - Orders : a scrollable list of OrderRows with actions
 *              (Complete, Review, Cancel).
 *
 * Main functionalities: Browse/Search Gigs, Place Order, Complete Order,
 * Leave Review, Cancel Order.
 */
public class ClientDashboard extends JPanel {

    private final MainFrame app;
    private final Client me;

    private final CardLayout pages = new CardLayout();
    private final JPanel content = new JPanel(pages);

    private JPanel gigGrid;
    private HintField search;
    private JComboBox<String> categoryFilter;
    private JPanel ordersList;

    public ClientDashboard(MainFrame app, Client me) {
        this.app = app;
        this.me = me;
        setLayout(new BorderLayout());
        setBackground(UI.PAGE);

        Sidebar sidebar = new Sidebar("FreelanceHub", me, this::navigate, app::logout);
        sidebar.addItem("browse", "Browse Gigs", "grid");
        sidebar.addItem("orders", "My Orders", "bag");
        add(sidebar, BorderLayout.WEST);

        content.setBackground(UI.PAGE);
        content.add(buildBrowse(), "browse");
        content.add(buildOrders(), "orders");
        add(content, BorderLayout.CENTER);

        refreshGigs();
        refreshOrders();
    }

    private void navigate(String key) {
        if (key.equals("orders")) refreshOrders();
        if (key.equals("browse")) refreshGigs();
        pages.show(content, key);
    }

    // ============ BROWSE PAGE ============
    private JComponent buildBrowse() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(UI.PAGE);

        // header with greeting + search
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        UI.pad(header, 28, 32, 8, 32);

        JLabel hi = UI.label("Find the perfect freelance services", UI.H1, UI.INK);
        hi.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = UI.label("Welcome back, " + me.getFullName() + " \uD83D\uDC4B", UI.BODY, UI.MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);
        searchRow.setAlignmentX(LEFT_ALIGNMENT);
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        searchRow.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        search = new HintField("Search gigs by title, category or seller...", 30);
        UI.capHeight(search, 44);
        search.addCaretListener(e -> refreshGigs());

        categoryFilter = new JComboBox<>(new String[]{"All Categories"});
        categoryFilter.setFont(UI.BODY);
        categoryFilter.setBackground(Color.WHITE);
        categoryFilter.setPreferredSize(new Dimension(180, 44));
        categoryFilter.addActionListener(e -> refreshGigs());

        searchRow.add(search, BorderLayout.CENTER);
        searchRow.add(categoryFilter, BorderLayout.EAST);

        header.add(hi);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        header.add(searchRow);
        page.add(header, BorderLayout.NORTH);

        // gig grid
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
        // refresh category filter options
        List<Gig> all = app.getData().getAllGigs();
        if (categoryFilter.getItemCount() <= 1) {
            java.util.LinkedHashSet<String> cats = new java.util.LinkedHashSet<>();
            for (Gig g : all) cats.add(g.getCategory());
            for (String c : cats) categoryFilter.addItem(c);
        }

        String q = search == null ? "" : search.getText().trim().toLowerCase();
        String cat = (String) categoryFilter.getSelectedItem();

        gigGrid.removeAll();
        int shown = 0;
        for (Gig g : all) {
            boolean matchesText = q.isEmpty()
                    || g.getTitle().toLowerCase().contains(q)
                    || g.getCategory().toLowerCase().contains(q)
                    || g.getFreelancerUsername().toLowerCase().contains(q);
            boolean matchesCat = cat == null || cat.equals("All Categories")
                    || g.getCategory().equals(cat);
            if (matchesText && matchesCat) {
                Freelancer seller = app.getData().getFreelancer(g.getFreelancerUsername());
                gigGrid.add(new GigCard(g, seller, "Order", this::placeOrder));
                shown++;
            }
        }
        if (shown == 0) {
            JLabel empty = UI.label("No gigs match your search. Try a different keyword.",
                    UI.BODY, UI.MUTED);
            gigGrid.add(empty);
        }
        gigGrid.revalidate();
        gigGrid.repaint();
    }

    private void placeOrder(Gig g) {
        int ok = JOptionPane.showConfirmDialog(this,
                "Order \"" + g.getTitle() + "\"\nPrice: $" + g.getPrice()
                        + "   Delivery: " + g.getDeliveryDays() + " days?",
                "Confirm Order", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        Order o = new Order(app.getData().getNextOrderId(), g.getId(), g.getTitle(),
                me.getUsername(), g.getFreelancerUsername(), g.getPrice(),
                OrderStatus.PENDING, LocalDate.now().toString(), false);
        app.getData().addOrder(o);
        JOptionPane.showMessageDialog(this,
                "Order placed! Track it under 'My Orders'.", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        refreshOrders();
    }

    // ============ ORDERS PAGE ============
    private JComponent buildOrders() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(UI.PAGE);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        UI.pad(header, 28, 32, 8, 32);
        JLabel h = UI.label("My Orders", UI.H1, UI.INK);
        h.setAlignmentX(LEFT_ALIGNMENT);
        JLabel s = UI.label("Track, complete and review your purchases", UI.BODY, UI.MUTED);
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
        List<Order> orders = app.getData().getOrdersByClient(me.getUsername());
        if (orders.isEmpty()) {
            ordersList.add(UI.label("You haven't ordered anything yet. Browse gigs to get started!",
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

        if (o.getStatus() == OrderStatus.DELIVERED) {
            RoundedButton complete = RoundedButton.filled("Confirm & Complete", UI.PRIMARY);
            complete.setFont(UI.SMALL_B);
            complete.addActionListener(e -> {
                o.setStatus(OrderStatus.COMPLETED);
                app.getData().saveOrders();
                refreshOrders();
                JOptionPane.showMessageDialog(this, "Order completed! You can now leave a review.",
                        "Completed", JOptionPane.INFORMATION_MESSAGE);
            });
            acts.add(complete);
        }
        if (o.getStatus() == OrderStatus.COMPLETED && !o.isReviewed()) {
            RoundedButton review = RoundedButton.outline("Leave Review", UI.PRIMARY);
            review.setFont(UI.SMALL_B);
            review.addActionListener(e -> leaveReview(o));
            acts.add(review);
        }
        if (o.getStatus() == OrderStatus.PENDING) {
            RoundedButton cancel = RoundedButton.outline("Cancel", UI.DANGER);
            cancel.setFont(UI.SMALL_B);
            cancel.addActionListener(e -> {
                int ok = JOptionPane.showConfirmDialog(this, "Cancel order #" + o.getId() + "?",
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    o.setStatus(OrderStatus.CANCELLED);
                    app.getData().saveOrders();
                    refreshOrders();
                }
            });
            acts.add(cancel);
        }
        if (o.getStatus() == OrderStatus.COMPLETED && o.isReviewed()) {
            acts.add(UI.label("\u2713 Reviewed", UI.SMALL_B, UI.PRIMARY));
        }

        return new OrderRow(o, o.getFreelancerUsername(), "Freelancer:",
                acts.toArray(new JComponent[0]));
    }

    private void leaveReview(Order o) {
        Integer[] stars = {1, 2, 3, 4, 5};
        Integer chosen = (Integer) JOptionPane.showInputDialog(this,
                "Rate @" + o.getFreelancerUsername() + " (1-5 stars):", "Leave a Review",
                JOptionPane.QUESTION_MESSAGE, null, stars, 5);
        if (chosen == null) return;
        String comment = JOptionPane.showInputDialog(this, "Write a short comment:", "Review",
                JOptionPane.QUESTION_MESSAGE);
        if (comment == null) return;
        if (comment.trim().isEmpty()) comment = "(no comment)";

        Review r = new Review(app.getData().getNextReviewId(), o.getId(),
                o.getFreelancerUsername(), me.getUsername(), chosen, DataManager.clean(comment));
        app.getData().addReview(r);

        Freelancer f = app.getData().getFreelancer(o.getFreelancerUsername());
        if (f != null) { f.addRating(chosen); app.getData().saveUsers(); }

        o.setReviewed(true);
        app.getData().saveOrders();
        JOptionPane.showMessageDialog(this, "Thanks! Your review has been saved.",
                "Review submitted", JOptionPane.INFORMATION_MESSAGE);
        refreshOrders();
    }
}
