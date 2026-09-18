package service;

import collection.CollectionRecord;
import java.time.LocalDate;


public class FefoAlertItem implements Comparable<FefoAlertItem> {
    private final CollectionRecord record;
    private final LocalDate expirationDate;
    private final long daysRemaining;

    public FefoAlertItem(CollectionRecord record, LocalDate expirationDate, long daysRemaining) {
        this.record = record;
        this.expirationDate = expirationDate;
        this.daysRemaining = daysRemaining;
    }

    public CollectionRecord getRecord() {
        return record;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public long getDaysRemaining() {
        return daysRemaining;
    }

    public boolean isExpired() {
        return daysRemaining < 0;
    }

    @Override
    public int compareTo(FefoAlertItem other) {
        int dateCompare = this.expirationDate.compareTo(other.expirationDate);
        if (dateCompare != 0) {
            return dateCompare;
        }
        return Integer.compare(this.record.getConsecutive(), other.record.getConsecutive());
    }

    @Override
    public String toString() {
        String statusNote = isExpired() ? " (VENCIDA)" : "";
        return String.format("%s | %s | vence %s | quedan %d dias%s",
                record.getUnitCode(),
                record.getModality(),
                expirationDate,
                daysRemaining,
                statusNote);
    }
}
