/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Task {
    /** Date or time by which the task should be completed. */
    private final String by;

    /**
     * Creates an incomplete deadline with the given description and due text.
     *
     * @param description the text describing the deadline
     * @param by the date or time by which it should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline's due text without display formatting.
     *
     * @return the due date or time text
     */
    public String getBy() {
        return by;
    }

    /**
     * Returns this deadline with its task-type icon and due text.
     *
     * @return the deadline formatted for display by Jaku
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
