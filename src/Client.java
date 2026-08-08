/**
 * Client
 * ------
 * A buyer on the platform. INHERITS everything from User and overrides
 * the abstract methods (runtime POLYMORPHISM).
 */
public class Client extends User {

    public Client(String username, String password, String fullName, String email) {
        super(username, password, fullName, email); // call base-class constructor
    }

    @Override
    public String getRole() {
        return "Client";
    }

    @Override
    public String getDashboardSummary() {
        return "Welcome back, " + getFullName() + "! Browse gigs and hire talented freelancers.";
    }

    /** File format:  CLIENT|username|password|fullName|email */
    @Override
    public String toFileString() {
        return "CLIENT|" + getUsername() + "|" + getPassword()
                + "|" + getFullName() + "|" + getEmail();
    }
}
