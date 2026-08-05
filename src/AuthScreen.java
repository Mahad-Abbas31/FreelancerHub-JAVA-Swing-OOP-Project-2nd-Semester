import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * AuthScreen
 * ----------
 * A modern split-screen authentication page:
 *   - LEFT  : a green gradient "hero" panel with the brand and selling points
 *             (painted with 2D graphics).
 *   - RIGHT : a white card that switches between Login and Register modes.
 *
 * Demonstrates GUI interaction, input validation, friendly error dialogs and
 * polymorphic object creation (Client vs Freelancer).
 */
public class AuthScreen extends JPanel {

    private final MainFrame app;
    private boolean registerMode = false;

    // shared fields
    private JTextField     fullName, email, username, skills;
    private JPasswordField password;
    private JComboBox<String> roleBox;
    private JPanel formArea;
    private JLabel titleLbl, subtitleLbl, switchLbl;
    private RoundedButton primaryBtn, switchBtn;
    private JLabel skillsLabel;

    public AuthScreen(MainFrame app) {
        this.app = app;
        setLayout(new GridLayout(1, 2));
        add(new HeroPanel());
        add(buildFormSide());
        showLogin();
    }

    // ===== LEFT: gradient hero =====
    private static class HeroPanel extends JPanel {
        HeroPanel() { setPreferredSize(new Dimension(420, 600)); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            UI.aa(g);
            int w = getWidth(), h = getHeight();
            g.setPaint(new GradientPaint(0, 0, UI.PRIMARY, w, h, UI.darker(UI.PRIMARY, 0.55f)));
            g.fillRect(0, 0, w, h);

            // decorative circles
            g.setColor(new Color(255, 255, 255, 26));
            g.fillOval(w - 120, -80, 260, 260);
            g.fillOval(-90, h - 160, 240, 240);

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 34));
            g.drawString("\u25C8 FreelanceHub", 48, 110);

            g.setFont(new Font("SansSerif", Font.PLAIN, 17));
            g.setColor(new Color(255, 255, 255, 225));
            g.drawString("Hire talent. Sell your skills.", 48, 146);

            String[] points = {
                "Browse hundreds of freelance gigs",
                "Order in one click & track progress",
                "Rate freelancers and build reputation",
                "Your data saved securely on disk"
            };
            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            int y = 230;
            for (String p : points) {
                g.setColor(new Color(255, 255, 255, 235));
                // check bullet
                g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Line2D.Float(48, y - 5, 53, y));
                g.draw(new Line2D.Float(53, y, 62, y - 12));
                g.drawString(p, 76, y);
                y += 48;
            }

            g.setColor(new Color(255, 255, 255, 150));
            g.setFont(UI.SMALL);
            g.drawString("Object-Oriented Programming Project  \u00B7  Java Swing", 48, h - 40);
            g.dispose();
        }
    }

    // ===== RIGHT: the form =====
    private JComponent buildFormSide() {
        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(UI.PAGE);

        RoundCard card = new RoundCard();
        card.arc(20).shadow(true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.contentPadding(28, 30, 28, 30);
        card.setPreferredSize(new Dimension(380, 540));

        titleLbl = UI.label("Welcome back", UI.H1, UI.INK);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLbl = UI.label("Sign in to continue", UI.BODY, UI.MUTED);
        subtitleLbl.setAlignmentX(LEFT_ALIGNMENT);

        // build all possible fields up front
        fullName = field();
        email    = field();
        username = field();
        password = passwordField();
        skills   = field();
        roleBox  = new JComboBox<>(new String[]{"Client (I want to hire)", "Freelancer (I want to sell)"});
        roleBox.setFont(UI.BODY);
        roleBox.setBackground(Color.WHITE);
        UI.capHeight(roleBox, 40);
        roleBox.setAlignmentX(LEFT_ALIGNMENT);
        roleBox.addActionListener(e -> updateSkills());

        skillsLabel = lbl("Your Skills (comma separated)");

        formArea = new JPanel();
        formArea.setOpaque(false);
        formArea.setLayout(new BoxLayout(formArea, BoxLayout.Y_AXIS));
        formArea.setAlignmentX(LEFT_ALIGNMENT);

        primaryBtn = RoundedButton.filled("Login", UI.PRIMARY);
        primaryBtn.setAlignmentX(LEFT_ALIGNMENT);
        UI.capHeight(primaryBtn, 46);
        primaryBtn.addActionListener(e -> submit());

        switchLbl = UI.label("New here?", UI.SMALL, UI.MUTED);
        switchBtn = RoundedButton.ghost("Create an account", UI.PRIMARY);
        switchBtn.setFont(UI.SMALL_B);
        switchBtn.addActionListener(e -> { registerMode = !registerMode; rebuild(); });

        JPanel switchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        switchRow.setOpaque(false);
        switchRow.setAlignmentX(LEFT_ALIGNMENT);
        switchRow.add(switchLbl);
        switchRow.add(switchBtn);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitleLbl);
        card.add(Box.createVerticalStrut(20));
        card.add(formArea);
        card.add(Box.createVerticalStrut(8));
        card.add(primaryBtn);
        card.add(Box.createVerticalStrut(6));
        card.add(switchRow);

        side.add(card);
        this.cardRef = card;
        rebuild();
        return side;
    }

    private RoundCard cardRef;

    private void rebuild() {
        formArea.removeAll();
        if (registerMode) {
            titleLbl.setText("Create account");
            subtitleLbl.setText("Join the marketplace in seconds");
            primaryBtn.setText("Create Account");
            switchLbl.setText("Already a member?");
            switchBtn.setText("Sign in instead");
            formArea.add(lbl("Full Name"));   formArea.add(fullName);   gap();
            formArea.add(lbl("Email"));       formArea.add(email);      gap();
            formArea.add(lbl("Username"));    formArea.add(username);   gap();
            formArea.add(lbl("Password"));    formArea.add(password);   gap();
            formArea.add(lbl("Account Type"));formArea.add(roleBox);    gap();
            formArea.add(skillsLabel);        formArea.add(skills);
            cardRef.setPreferredSize(new Dimension(380, 620));
        } else {
            titleLbl.setText("Welcome back");
            subtitleLbl.setText("Sign in to continue");
            primaryBtn.setText("Login");
            switchLbl.setText("New here?");
            switchBtn.setText("Create an account");
            formArea.add(lbl("Username")); formArea.add(username); gap();
            formArea.add(lbl("Password")); formArea.add(password);
            cardRef.setPreferredSize(new Dimension(380, 420));
        }
        clearFields();
        updateSkills();
        revalidate();
        repaint();
    }

    private void updateSkills() {
        boolean isFreelancer = registerMode && roleBox.getSelectedIndex() == 1;
        skillsLabel.setVisible(isFreelancer);
        skills.setVisible(isFreelancer);
        revalidate(); repaint();
    }

    public void showLogin()    { registerMode = false; if (cardRef != null) rebuild(); }
    public void showRegister() { registerMode = true;  if (cardRef != null) rebuild(); }

    private void submit() {
        if (registerMode) doRegister(); else doLogin();
    }

    private void doLogin() {
        String u = username.getText().trim();
        String p = new String(password.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            warn("Please enter both username and password."); return;
        }
        User user = app.getData().authenticate(u, p);
        if (user == null) { error("Invalid username or password."); return; }
        app.showDashboardFor(user);
    }

    private void doRegister() {
        String fn = fullName.getText().trim();
        String em = email.getText().trim();
        String un = username.getText().trim();
        String pw = new String(password.getPassword());
        boolean isFreelancer = roleBox.getSelectedIndex() == 1;
        String sk = skills.getText().trim();

        if (fn.isEmpty() || em.isEmpty() || un.isEmpty() || pw.isEmpty()) {
            warn("Please fill in all required fields."); return;
        }
        if (!em.contains("@") || !em.contains(".")) { warn("Please enter a valid email address."); return; }
        if (pw.length() < 4) { warn("Password must be at least 4 characters."); return; }
        if (app.getData().usernameExists(un)) { warn("That username is already taken."); return; }
        if (isFreelancer && sk.isEmpty()) { warn("Freelancers must list at least one skill."); return; }

        User newUser = isFreelancer
                ? new Freelancer(DataManager.clean(un), DataManager.clean(pw),
                        DataManager.clean(fn), DataManager.clean(em), DataManager.clean(sk))
                : new Client(DataManager.clean(un), DataManager.clean(pw),
                        DataManager.clean(fn), DataManager.clean(em));
        app.getData().addUser(newUser);
        JOptionPane.showMessageDialog(this,
                "Account created! Please sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
        registerMode = false;
        rebuild();
    }

    // ---- small field helpers ----
    private JTextField field() {
        JTextField t = new JTextField();
        styleField(t);
        return t;
    }
    private JPasswordField passwordField() {
        JPasswordField t = new JPasswordField();
        styleField(t);
        return t;
    }
    private void styleField(JComponent c) {
        c.setFont(UI.BODY);
        c.setBorder(BorderFactory.createCompoundBorder(
                new HintField.RoundedBorder(10, UI.LINE, 9),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        c.setBackground(Color.WHITE);
        c.setAlignmentX(LEFT_ALIGNMENT);
        UI.capHeight(c, 40);
    }
    private JLabel lbl(String t) {
        JLabel l = UI.label(t, UI.SMALL_B, UI.INK);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 0));
        return l;
    }
    private void gap() { formArea.add(Box.createVerticalStrut(10)); }

    private void clearFields() {
        fullName.setText(""); email.setText(""); username.setText("");
        password.setText(""); skills.setText("");
    }

    private void warn(String m)  { JOptionPane.showMessageDialog(this, m, "Check your input", JOptionPane.WARNING_MESSAGE); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
