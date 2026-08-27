/**
 * Review
 * ------
 * A rating (1-5 stars) and comment left by a Client about a Freelancer
 * after an order is completed.
 */
public class Review {

    private int    id;
    private int    orderId;
    private String freelancerUsername;
    private String clientUsername;
    private int    stars;        // 1 - 5
    private String comment;

    public Review(int id, int orderId, String freelancerUsername,
                  String clientUsername, int stars, String comment) {
        this.id = id;
        this.orderId = orderId;
        this.freelancerUsername = freelancerUsername;
        this.clientUsername = clientUsername;
        this.stars = stars;
        this.comment = comment;
    }

    public int    getId()                 { return id; }
    public int    getOrderId()            { return orderId; }
    public String getFreelancerUsername() { return freelancerUsername; }
    public String getClientUsername()     { return clientUsername; }
    public int    getStars()              { return stars; }
    public String getComment()            { return comment; }

    /** File format: id|orderId|freelancer|client|stars|comment */
    public String toFileString() {
        return id + "|" + orderId + "|" + freelancerUsername
                + "|" + clientUsername + "|" + stars + "|" + comment;
    }

    /** A row of star characters, e.g. "★★★★☆". */
    public String getStarString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) sb.append(i <= stars ? '\u2605' : '\u2606');
        return sb.toString();
    }
}
