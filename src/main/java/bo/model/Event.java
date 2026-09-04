package bo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import bo.time.DateTimeParser;

/**
 * A task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    /** The parsed date/time at which this event starts. */
    private final LocalDateTime startDateTime;

    /** The parsed date/time at which this event ends. */
    private final LocalDateTime endDateTime;

    /** Whether the original start value included a time. */
    private final boolean includesStartTime;

    /** Whether the original end value included a time. */
    private final boolean includesEndTime;

    /** The original free-form start value, if it could not be parsed. */
    private final String legacyStart;

    /** The original free-form end value, if it could not be parsed. */
    private final String legacyEnd;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the task.
     * @param startDateTime the date or time at which the event starts.
     * @param endDateTime the date or time at which the event ends.
     */
    public Event(String description, String startDateTime, String endDateTime) {
        super(description);
        DateTimeParser.ParsedDateTime parsedStart = tryParse(startDateTime);
        DateTimeParser.ParsedDateTime parsedEnd = tryParse(endDateTime);
        this.startDateTime = parsedStart == null ? null : parsedStart.value();
        this.endDateTime = parsedEnd == null ? null : parsedEnd.value();
        this.includesStartTime = parsedStart != null && parsedStart.includesTime();
        this.includesEndTime = parsedEnd != null && parsedEnd.includesTime();
        this.legacyStart = parsedStart == null ? startDateTime : null;
        this.legacyEnd = parsedEnd == null ? endDateTime : null;
    }

    /**
     * Creates an incomplete event with date-only start and end values.
     *
     * @param description the text describing the event.
     * @param startDate the event start date.
     * @param endDate the event end date.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDateTime = startDate.atStartOfDay();
        this.endDateTime = endDate.atStartOfDay();
        this.includesStartTime = false;
        this.includesEndTime = false;
        this.legacyStart = null;
        this.legacyEnd = null;
    }

    /**
     * Creates an incomplete event with date and time start and end values.
     *
     * @param description the text describing the event.
     * @param startDateTime the event start date and time.
     * @param endDateTime the event end date and time.
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.includesStartTime = true;
        this.includesEndTime = true;
        this.legacyStart = null;
        this.legacyEnd = null;
    }

    /**
     * Returns the parsed event start value.
     *
     * @return the event start as a {@link LocalDateTime}, or {@code null} for
     *         an old free-form value that could not be parsed.
     */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the parsed event end value.
     *
     * @return the event end as a {@link LocalDateTime}, or {@code null} for an
     *         old free-form value that could not be parsed.
     */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * Returns the event start value used when displaying the task.
     *
     * @return a formatted date/time, or the old free-form value.
     */
    public String getDisplayStart() {
        if (legacyStart != null) {
            return legacyStart;
        }
        return DateTimeParser.format(new DateTimeParser.ParsedDateTime(startDateTime, includesStartTime));
    }

    /**
     * Returns the event end value used when displaying the task.
     *
     * @return a formatted date/time, or the old free-form value.
     */
    public String getDisplayEnd() {
        if (legacyEnd != null) {
            return legacyEnd;
        }
        return DateTimeParser.format(new DateTimeParser.ParsedDateTime(endDateTime, includesEndTime));
    }

    /**
     * Returns the event start value used in the storage file.
     *
     * @return an ISO date/time, or the old free-form value.
     */
    public String getStoredStart() {
        if (legacyStart != null) {
            return legacyStart;
        }
        return DateTimeParser.serialize(new DateTimeParser.ParsedDateTime(startDateTime, includesStartTime));
    }

    /**
     * Returns the event end value used in the storage file.
     *
     * @return an ISO date/time, or the old free-form value.
     */
    public String getStoredEnd() {
        if (legacyEnd != null) {
            return legacyEnd;
        }
        return DateTimeParser.serialize(new DateTimeParser.ParsedDateTime(endDateTime, includesEndTime));
    }

    /**
     * Returns the icon used for event tasks.
     *
     * @return {@code E}.
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the task including its start and end values.
     *
     * @return the formatted event task.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + getDisplayStart() + " to: " + getDisplayEnd() + ")";
    }

    /**
     * Returns the parsed event start value using the legacy accessor name.
     *
     * @return the event start as a {@link LocalDateTime}, or {@code null} for
     *         an old free-form value.
     * @deprecated Use {@link #getStartDateTime()} instead.
     */
    @Deprecated
    public LocalDateTime getFrom() {
        return getStartDateTime();
    }

    /**
     * Returns the parsed event end value using the legacy accessor name.
     *
     * @return the event end as a {@link LocalDateTime}, or {@code null} for an
     *         old free-form value.
     * @deprecated Use {@link #getEndDateTime()} instead.
     */
    @Deprecated
    public LocalDateTime getTo() {
        return getEndDateTime();
    }

    /**
     * Returns the event start display value using the legacy accessor name.
     *
     * @return a formatted date/time, or the old free-form value.
     * @deprecated Use {@link #getDisplayStart()} instead.
     */
    @Deprecated
    public String getDisplayFrom() {
        return getDisplayStart();
    }

    /**
     * Returns the event end display value using the legacy accessor name.
     *
     * @return a formatted date/time, or the old free-form value.
     * @deprecated Use {@link #getDisplayEnd()} instead.
     */
    @Deprecated
    public String getDisplayTo() {
        return getDisplayEnd();
    }

    /**
     * Returns the event start storage value using the legacy accessor name.
     *
     * @return an ISO date/time, or the old free-form value.
     * @deprecated Use {@link #getStoredStart()} instead.
     */
    @Deprecated
    public String getStoredFrom() {
        return getStoredStart();
    }

    /**
     * Returns the event end storage value using the legacy accessor name.
     *
     * @return an ISO date/time, or the old free-form value.
     * @deprecated Use {@link #getStoredEnd()} instead.
     */
    @Deprecated
    public String getStoredTo() {
        return getStoredEnd();
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
