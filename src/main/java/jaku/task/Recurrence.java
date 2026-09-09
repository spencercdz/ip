package jaku.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Describes the interval between consecutive occurrences of a recurring task. */
public enum Recurrence {
    DAILY("daily", 1),
    WEEKLY("weekly", 7);

    /** Text accepted from and displayed to the user. */
    private final String label;

    /** Number of calendar days between occurrences. */
    private final int days;

    /**
     * Creates a recurrence interval.
     *
     * @param label user-facing interval name
     * @param days number of days between occurrences
     */
    Recurrence(String label, int days) {
        this.label = label;
        this.days = days;
    }

    /**
     * Parses a recurrence interval entered by a user.
     *
     * @param text interval text
     * @return matching recurrence interval
     * @throws IllegalArgumentException if the interval is unsupported
     */
    public static Recurrence fromLabel(String text) {
        for (Recurrence recurrence : values()) {
            if (recurrence.label.equals(text)) {
                return recurrence;
            }
        }
        throw new IllegalArgumentException("Unsupported recurrence: " + text);
    }

    /** Returns the lower-case interval text shown to users. */
    public String getLabel() {
        return label;
    }

    /** Advances a calendar date by this interval. */
    public LocalDate advance(LocalDate date) {
        return date.plusDays(days);
    }

    /** Reverses a calendar date by this interval. */
    public LocalDate reverse(LocalDate date) {
        return date.minusDays(days);
    }

    /** Advances a date-time by this interval. */
    public LocalDateTime advance(LocalDateTime dateTime) {
        return dateTime.plusDays(days);
    }

    /** Reverses a date-time by this interval. */
    public LocalDateTime reverse(LocalDateTime dateTime) {
        return dateTime.minusDays(days);
    }
}
