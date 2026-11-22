package com.library.model.item;

import com.library.util.Validation;

import java.util.Objects;

/**
 * Represents a DVD in the library catalog with a 7-day loan period.
 */
public final class Dvd extends LibraryItem {

    public static final int LOAN_PERIOD_DAYS = 7;

    private final String director;
    private final int durationMinutes;

    public Dvd(String id, String title, String director, int durationMinutes) {
        super(id, title);
        this.director = Validation.requireNonBlank(director, "director");
        this.durationMinutes = Validation.requirePositive(durationMinutes, "durationMinutes");
    }

    public String getDirector() {
        return director;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public int getLoanPeriodDays() {
        return LOAN_PERIOD_DAYS;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Dvd dvd = (Dvd) o;
        return durationMinutes == dvd.durationMinutes
                && Objects.equals(director, dvd.director);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), director, durationMinutes);
    }

    @Override
    public String toString() {
        return "Dvd{id='" + getId() + "'"
                + ", title='" + getTitle() + "'"
                + ", director='" + director + "'"
                + ", durationMinutes=" + durationMinutes
                + ", available=" + isAvailable()
                + "}";
    }
}
