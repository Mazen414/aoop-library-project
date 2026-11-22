package com.library.model.item;

import com.library.util.Validation;

import java.util.Objects;

/**
 * Represents a magazine issue in the library catalog with a 3-day loan period.
 */
public final class Magazine extends LibraryItem {

    public static final int LOAN_PERIOD_DAYS = 3;

    private final int issueNumber;
    private final String publisher;

    public Magazine(String id, String title, int issueNumber, String publisher) {
        super(id, title);
        this.issueNumber = Validation.requirePositive(issueNumber, "issueNumber");
        this.publisher = Validation.requireNonBlank(publisher, "publisher");
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public String getPublisher() {
        return publisher;
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
        Magazine magazine = (Magazine) o;
        return issueNumber == magazine.issueNumber
                && Objects.equals(publisher, magazine.publisher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), issueNumber, publisher);
    }

    @Override
    public String toString() {
        return "Magazine{id='" + getId() + "'"
                + ", title='" + getTitle() + "'"
                + ", issueNumber=" + issueNumber
                + ", publisher='" + publisher + "'"
                + ", available=" + isAvailable()
                + "}";
    }
}
