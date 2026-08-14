/**
 * Gig
 * ---
 * Represents a service a Freelancer offers (the "product" on the platform).
 * A plain ENCAPSULATED data class with private fields and getters/setters.
 *
 * Relationship: a Gig is ASSOCIATED with a Freelancer (by username).
 */
public class Gig {

    private int    id;                 // unique gig id
    private String title;
    private String description;
    private String category;
    private double price;              // in dollars
    private int    deliveryDays;
    private String freelancerUsername; // the owner of this gig

    public Gig(int id, String title, String description, String category,
               double price, int deliveryDays, String freelancerUsername) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.deliveryDays = deliveryDays;
        this.freelancerUsername = freelancerUsername;
    }

    // ---- GETTERS ----
    public int    getId()                 { return id; }
    public String getTitle()              { return title; }
    public String getDescription()        { return description; }
    public String getCategory()           { return category; }
    public double getPrice()              { return price; }
    public int    getDeliveryDays()       { return deliveryDays; }
    public String getFreelancerUsername() { return freelancerUsername; }

    // ---- SETTERS (allow editing a gig) ----
    public void setTitle(String title)             { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category)       { this.category = category; }
    public void setPrice(double price)             { this.price = price; }
    public void setDeliveryDays(int deliveryDays)  { this.deliveryDays = deliveryDays; }

    /** File format: id|title|description|category|price|deliveryDays|freelancerUsername */
    public String toFileString() {
        return id + "|" + title + "|" + description + "|" + category
                + "|" + price + "|" + deliveryDays + "|" + freelancerUsername;
    }

    @Override
    public String toString() {
        return "#" + id + "  " + title + "  —  $" + price
                + "  (" + deliveryDays + " day delivery)  by " + freelancerUsername;
    }
}
