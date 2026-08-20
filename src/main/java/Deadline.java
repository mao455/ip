/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The date or time by which this task should be completed. */
    private final String by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline value exactly as entered by the user.
     *
     * @return the deadline date or time
     */
    public String getBy() {
        return by;
    }

    /**
     * Returns the icon used for deadline tasks.
     *
     * @return {@code D}
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the task including its deadline.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
