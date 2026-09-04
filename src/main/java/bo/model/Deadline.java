package bo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import bo.time.DateTimeParser;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The parsed date/time by which this task should be completed. */
    private final LocalDateTime deadlineDateTime;

    /** Whether the original value included a time. */
    private final boolean includesTime;

    /** The original free-form value, used for backwards-compatible old data. */
    private final String legacyDeadline;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task.
     * @param deadlineDateTime the date or time by which the task should be completed.
     */
    public Deadline(String description, String deadlineDateTime) {
        super(description);
        DateTimeParser.ParsedDateTime parsed = tryParse(deadlineDateTime);
        this.deadlineDateTime = parsed == null ? null : parsed.value();
        this.includesTime = parsed != null && parsed.includesTime();
        this.legacyDeadline = parsed == null ? deadlineDateTime : null;
    }

    /**
     * Creates an incomplete deadline with a date-only value.
     *
     * @param description the text describing the task.
     * @param deadlineDate the date by which the task should be completed.
     */
    public Deadline(String description, LocalDate deadlineDate) {
        super(description);
        this.deadlineDateTime = deadlineDate.atStartOfDay();
        this.includesTime = false;
        this.legacyDeadline = null;
    }

    /**
     * Creates an incomplete deadline with a date and time.
     *
     * @param description the text describing the task.
     * @param deadlineDateTime the date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime deadlineDateTime) {
        super(description);
        this.deadlineDateTime = deadlineDateTime;
        this.includesTime = true;
        this.legacyDeadline = null;
    }

    /**
     * Returns the parsed deadline value.
     *
     * @return the deadline as a {@link LocalDateTime}, or {@code null} for an
     *         old free-form value that could not be parsed.
     */
    public LocalDateTime getDeadlineDateTime() {
        return deadlineDateTime;
    }

    /**
     * Returns the value used when displaying this task.
     *
     * @return a formatted date/time, or the old free-form value.
     */
    public String getDisplayDeadline() {
        if (legacyDeadline != null) {
            return legacyDeadline;
        }
        return DateTimeParser.format(new DateTimeParser.ParsedDateTime(deadlineDateTime, includesTime));
    }

    /**
     * Returns the value used in the storage file.
     *
     * @return an ISO date/time, or the old free-form value.
     */
    public String getStoredDeadline() {
        if (legacyDeadline != null) {
            return legacyDeadline;
        }
        return DateTimeParser.serialize(new DateTimeParser.ParsedDateTime(deadlineDateTime, includesTime));
    }

    /**
     * Returns the icon used for deadline tasks.
     *
     * @return {@code D}.
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the task including its deadline.
     *
     * @return the formatted deadline task.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + getDisplayDeadline() + ")";
    }

    /**
     * Returns the parsed deadline value using the legacy accessor name.
     *
     * @return the deadline as a {@link LocalDateTime}, or {@code null} for an
     *         old free-form value.
     * @deprecated Use {@link #getDeadlineDateTime()} instead.
     */
    @Deprecated
    public LocalDateTime getBy() {
        return getDeadlineDateTime();
    }

    /**
     * Returns the deadline display value using the legacy accessor name.
     *
     * @return a formatted date/time, or the old free-form value.
     * @deprecated Use {@link #getDisplayDeadline()} instead.
     */
    @Deprecated
    public String getDisplayBy() {
        return getDisplayDeadline();
    }

    /**
     * Returns the deadline storage value using the legacy accessor name.
     *
     * @return an ISO date/time, or the old free-form value.
     * @deprecated Use {@link #getStoredDeadline()} instead.
     */
    @Deprecated
    public String getStoredBy() {
        return getStoredDeadline();
    }

    /**
     * Tries to parse a value while retaining old free-form values from earlier
     * versions of Bo.
     *
     * @param value the value to parse.
     * @return the parsed value, or {@code null} when it is legacy text.
     */
    private static DateTimeParser.ParsedDateTime tryParse(String value) {
        try {
            return DateTimeParser.parse(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
