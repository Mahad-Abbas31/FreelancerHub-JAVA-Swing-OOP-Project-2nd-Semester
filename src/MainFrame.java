import javax.swing.*;
import java.awt.*;

/**
 * MainFrame
 * ---------
 * The main application window. Uses a CardLayout to switch between the
 * authentication screen and the role-specific dashboards. Owns the single
 * DataManager instance and remembers who is logged in. Acts as the controller
 * that wires the GUI together.
 */
public class MainFrame extends JFrame {

    private final DataManager data = new DataManager();
    private User currentUser;

    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);
    private final AuthScreen auth;

    public MainFrame() {
        this(false);
    }

    /** Internal constructor; when skipWindow is true, no window chrome is built
     *  (used only for offscreen rendering / testing in headless environments). */
    MainFrame(boolean skipWindow) {
        if (skipWindow) {
            auth = new AuthScreen(this);
            return;
        }
        setTitle("FreelanceHub \u2014 Freelancing Marketplace");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);

        container.setBackground(UI.PAGE);
        auth = new AuthScreen(this);
        container.add(auth, "auth");
        add(container);
        showLogin();
    }

    public void showLogin() {
        auth.showLogin();
        cards.show(container, "auth");
    }

    public void showRegister() {
        auth.showRegister();
        cards.show(container, "auth");
    }

    /** Builds the correct dashboard for the user's role (POLYMORPHISM). */
    public void showDashboardFor(User user) {
        this.currentUser = user;
        JComponent dash = (user instanceof Freelancer)
                ? new FreelancerDashboard(this, (Freelancer) user)
                : new ClientDashboard(this, (Client) user);
        container.add(dash, "dashboard");
        cards.show(container, "dashboard");
    }

    public void logout() {
        currentUser = null;
        showLogin();
    }

    public DataManager getData() { return data; }
    public User getCurrentUser() { return currentUser; }
}
