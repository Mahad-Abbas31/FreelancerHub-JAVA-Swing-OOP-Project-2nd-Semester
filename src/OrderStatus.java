/**
 * OrderStatus
 * -----------
 * A type-safe enumeration of the states an order can be in.
 * Using an enum (instead of plain Strings) prevents invalid status values
 * and makes the code self-documenting.
 */
public enum OrderStatus {
    PENDING,
    IN_PROGRESS,
    DELIVERED,
    COMPLETED,
    CANCELLED;

    /** Returns a nicely formatted label for display in the GUI. */
    public String label() {
        switch (this) {
            case IN_PROGRESS: return "In Progress";
            default:
                // Capitalise first letter, lowercase the rest (e.g. PENDING -> Pending)
                String s = name().toLowerCase();
                return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
    }
}
