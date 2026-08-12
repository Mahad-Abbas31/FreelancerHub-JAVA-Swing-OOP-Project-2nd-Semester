/**
 * Freelancer
 * ----------
 * A seller on the platform. INHERITS from User, adds its own data
 * (skills, rating) and behaviour. Demonstrates:
 *   - INHERITANCE     : extends User.
 *   - POLYMORPHISM    : overrides abstract methods AND overloads the constructor.
 *   - ENCAPSULATION   : rating logic is hidden behind addRating().
 */
public class Freelancer extends User {

    private String skills;       // e.g. "Logo Design, Branding"
    private double rating;       // average rating 0.0 - 5.0
    private int totalReviews;    // how many reviews the average is based on

    /**
     * CONSTRUCTOR OVERLOAD #1 — for a brand new freelancer (no reviews yet).
     * Demonstrates compile-time polymorphism (two constructors, same name).
     */
    public Freelancer(String username, String password, String fullName,
                      String email, String skills) {
        this(username, password, fullName, email, skills, 0.0, 0);
    }

    /**
     * CONSTRUCTOR OVERLOAD #2 — used when loading an existing freelancer
     * from file, where rating and review count are already known.
     */
    public Freelancer(String username, String password, String fullName,
                      String email, String skills, double rating, int totalReviews) {
        super(username, password, fullName, email);
        this.skills = skills;
        this.rating = rating;
        this.totalReviews = totalReviews;
    }

    // ---- ENCAPSULATED ACCESS ----
    public String getSkills()      { return skills; }
    public void   setSkills(String skills) { this.skills = skills; }
    public double getRating()      { return rating; }
    public int    getTotalReviews(){ return totalReviews; }

    /**
     * Recalculates the running average rating when a new review arrives.
     * The internal maths is hidden from the rest of the program.
     */
    public void addRating(int newStars) {
        double runningTotal = rating * totalReviews; // undo previous average
        runningTotal += newStars;
        totalReviews++;
        rating = runningTotal / totalReviews;
    }

    /** Returns rating as a display string, e.g. "4.5 (12 reviews)". */
    public String getRatingDisplay() {
        if (totalReviews == 0) return "No reviews yet";
        return String.format("%.1f \u2605 (%d review%s)",
                rating, totalReviews, totalReviews == 1 ? "" : "s");
    }

    @Override
    public String getRole() {
        return "Freelancer";
    }

    @Override
    public String getDashboardSummary() {
        return "Welcome, " + getFullName() + "! Create gigs and manage your orders. "
                + "Rating: " + getRatingDisplay();
    }

    /** File format: FREELANCER|username|password|fullName|email|skills|rating|totalReviews */
    @Override
    public String toFileString() {
        return "FREELANCER|" + getUsername() + "|" + getPassword()
                + "|" + getFullName() + "|" + getEmail()
                + "|" + skills + "|" + rating + "|" + totalReviews;
    }
}
