import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DataManager
 * -----------
 * The data-access layer of the application. It keeps the in-memory LISTS of
 * every entity and is responsible for ALL FILE HANDLING (reading on startup,
 * writing whenever something changes).
 *
 * This class demonstrates:
 *   - FILE HANDLING : BufferedReader / PrintWriter on .txt files (REQUIRED).
 *   - ENCAPSULATION : lists are private; access is through methods only.
 *   - ERROR HANDLING: every file operation is wrapped in try/catch.
 *   - Use of Arrays/Lists as required by the spec.
 *
 * "Clear separation of data and behavior": the GUI never touches files
 * directly — it always goes through this class.
 */
public class DataManager {

    // ---- file names (created in the program's working directory) ----
    private static final String USERS_FILE   = "users.txt";
    private static final String GIGS_FILE    = "gigs.txt";
    private static final String ORDERS_FILE  = "orders.txt";
    private static final String REVIEWS_FILE = "reviews.txt";

    private static final String SEP = "\\|"; // regex for splitting on '|'

    // ---- in-memory collections (LISTS) ----
    private final List<User>   users   = new ArrayList<>();
    private final List<Gig>    gigs    = new ArrayList<>();
    private final List<Order>  orders  = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();

    /** Loads everything from disk as soon as the manager is created. */
    public DataManager() {
        loadUsers();
        loadGigs();
        loadOrders();
        loadReviews();
    }

    /* =====================================================================
     *  SMALL HELPERS
     * ===================================================================== */

    /**
     * Removes characters that would corrupt our '|'-delimited file format.
     * Prevents a user from breaking the data files by typing '|' or newlines.
     */
    public static String clean(String s) {
        if (s == null) return "";
        return s.replace("|", "/").replace("\n", " ").replace("\r", " ").trim();
    }

    /* =====================================================================
     *  USERS
     * ===================================================================== */

    private void loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return;                 // first run — nothing to load
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(SEP, -1);
                try {
                    if (p[0].equals("CLIENT") && p.length >= 5) {
                        users.add(new Client(p[1], p[2], p[3], p[4]));
                    } else if (p[0].equals("FREELANCER") && p.length >= 8) {
                        users.add(new Freelancer(p[1], p[2], p[3], p[4], p[5],
                                Double.parseDouble(p[6]), Integer.parseInt(p[7])));
                    }
                } catch (Exception parseErr) {
                    // skip a single malformed line instead of crashing
                    System.err.println("Skipping bad user line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read users file: " + e.getMessage());
        }
    }

    public void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(USERS_FILE)))) {
            for (User u : users) pw.println(u.toFileString());
        } catch (IOException e) {
            System.err.println("Could not save users: " + e.getMessage());
        }
    }

    /** Adds a user and immediately persists. */
    public void addUser(User u) {
        users.add(u);
        saveUsers();
    }

    /** Returns the user if username+password match, otherwise null. */
    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)
                    && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public boolean usernameExists(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    public Freelancer getFreelancer(String username) {
        for (User u : users) {
            if (u instanceof Freelancer && u.getUsername().equalsIgnoreCase(username)) {
                return (Freelancer) u;  // safe down-cast (POLYMORPHISM check)
            }
        }
        return null;
    }

    /* =====================================================================
     *  GIGS
     * ===================================================================== */

    private void loadGigs() {
        File f = new File(GIGS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(SEP, -1);
                if (p.length < 7) continue;
                try {
                    gigs.add(new Gig(Integer.parseInt(p[0]), p[1], p[2], p[3],
                            Double.parseDouble(p[4]), Integer.parseInt(p[5]), p[6]));
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping bad gig line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read gigs file: " + e.getMessage());
        }
    }

    public void saveGigs() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(GIGS_FILE)))) {
            for (Gig g : gigs) pw.println(g.toFileString());
        } catch (IOException e) {
            System.err.println("Could not save gigs: " + e.getMessage());
        }
    }

    public void addGig(Gig g) {
        gigs.add(g);
        saveGigs();
    }

    public List<Gig> getAllGigs() {
        return new ArrayList<>(gigs); // defensive copy (ENCAPSULATION)
    }

    public List<Gig> getGigsByFreelancer(String username) {
        List<Gig> result = new ArrayList<>();
        for (Gig g : gigs) {
            if (g.getFreelancerUsername().equalsIgnoreCase(username)) result.add(g);
        }
        return result;
    }

    /** Auto-increment id: one higher than the current maximum. */
    public int getNextGigId() {
        int max = 0;
        for (Gig g : gigs) max = Math.max(max, g.getId());
        return max + 1;
    }

    /* =====================================================================
     *  ORDERS
     * ===================================================================== */

    private void loadOrders() {
        File f = new File(ORDERS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(SEP, -1);
                if (p.length < 9) continue;
                try {
                    orders.add(new Order(
                            Integer.parseInt(p[0]), Integer.parseInt(p[1]), p[2],
                            p[3], p[4], Double.parseDouble(p[5]),
                            OrderStatus.valueOf(p[6]), p[7],
                            Boolean.parseBoolean(p[8])));
                } catch (Exception parseErr) {
                    System.err.println("Skipping bad order line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read orders file: " + e.getMessage());
        }
    }

    public void saveOrders() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(ORDERS_FILE)))) {
            for (Order o : orders) pw.println(o.toFileString());
        } catch (IOException e) {
            System.err.println("Could not save orders: " + e.getMessage());
        }
    }

    public void addOrder(Order o) {
        orders.add(o);
        saveOrders();
    }

    public List<Order> getOrdersByClient(String username) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getClientUsername().equalsIgnoreCase(username)) result.add(o);
        }
        return result;
    }

    public List<Order> getOrdersByFreelancer(String username) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getFreelancerUsername().equalsIgnoreCase(username)) result.add(o);
        }
        return result;
    }

    public int getNextOrderId() {
        int max = 0;
        for (Order o : orders) max = Math.max(max, o.getId());
        return max + 1;
    }

    /* =====================================================================
     *  REVIEWS
     * ===================================================================== */

    private void loadReviews() {
        File f = new File(REVIEWS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(SEP, -1);
                if (p.length < 6) continue;
                try {
                    reviews.add(new Review(Integer.parseInt(p[0]), Integer.parseInt(p[1]),
                            p[2], p[3], Integer.parseInt(p[4]), p[5]));
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping bad review line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read reviews file: " + e.getMessage());
        }
    }

    public void saveReviews() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(REVIEWS_FILE)))) {
            for (Review r : reviews) pw.println(r.toFileString());
        } catch (IOException e) {
            System.err.println("Could not save reviews: " + e.getMessage());
        }
    }

    public void addReview(Review r) {
        reviews.add(r);
        saveReviews();
    }

    public List<Review> getReviewsForFreelancer(String username) {
        List<Review> result = new ArrayList<>();
        for (Review r : reviews) {
            if (r.getFreelancerUsername().equalsIgnoreCase(username)) result.add(r);
        }
        return result;
    }

    public int getNextReviewId() {
        int max = 0;
        for (Review r : reviews) max = Math.max(max, r.getId());
        return max + 1;
    }
}
