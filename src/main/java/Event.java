/**
 * A task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    /** The date or time at which this event starts. */
    private final String from;

    /** The date or time at which this event ends. */
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the task
     * @param from the date or time at which the event starts
     * @param to the date or time at which the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's starting value exactly as entered by the user.
     *
     * @return the event start date or time
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's ending value exactly as entered by the user.
     *
     * @return the event end date or time
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the icon used for event tasks.
     *
     * @return {@code E}
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the task including its start and end values.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
