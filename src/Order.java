/**
 * Order
 * -----
 * Represents a purchase: a Client buys a Gig from a Freelancer.
 * ASSOCIATES three entities together (client, freelancer, gig) and tracks
 * its OrderStatus through the workflow.
 */
public class Order {

    private int         id;
    private int         gigId;
    private String      gigTitle;            // stored so we can display it easily
    private String      clientUsername;
    private String      freelancerUsername;
    private double      price;
    private OrderStatus status;
    private String      date;                // simple yyyy-mm-dd string
    private boolean     reviewed;            // has the client left a review?

    public Order(int id, int gigId, String gigTitle, String clientUsername,
                 String freelancerUsername, double price, OrderStatus status,
                 String date, boolean reviewed) {
        this.id = id;
        this.gigId = gigId;
        this.gigTitle = gigTitle;
        this.clientUsername = clientUsername;
        this.freelancerUsername = freelancerUsername;
        this.price = price;
        this.status = status;
        this.date = date;
        this.reviewed = reviewed;
    }

    // ---- GETTERS ----
    public int         getId()                 { return id; }
    public int         getGigId()              { return gigId; }
    public String      getGigTitle()           { return gigTitle; }
    public String      getClientUsername()     { return clientUsername; }
    public String      getFreelancerUsername() { return freelancerUsername; }
    public double      getPrice()              { return price; }
    public OrderStatus getStatus()             { return status; }
    public String      getDate()               { return date; }
    public boolean     isReviewed()            { return reviewed; }

    // ---- SETTERS (state changes) ----
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setReviewed(boolean reviewed) { this.reviewed = reviewed; }

    /** File format: id|gigId|gigTitle|client|freelancer|price|status|date|reviewed */
    public String toFileString() {
        return id + "|" + gigId + "|" + gigTitle + "|" + clientUsername
                + "|" + freelancerUsername + "|" + price + "|" + status.name()
                + "|" + date + "|" + reviewed;
    }
}
