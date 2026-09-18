package service;

import collection.CollectionRecord;
import java.time.LocalDate;


public class AptitudeEvaluation {
    private final CollectionRecord record;
    private final AptitudeStatus status;
    private final String rejectionReason;
    private final LocalDate expirationDate;

    public AptitudeEvaluation(CollectionRecord record, AptitudeStatus status,
                              String rejectionReason, LocalDate expirationDate) {
        this.record = record;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.expirationDate = expirationDate;
    }

    public CollectionRecord getRecord() {
        return record;
    }

    public AptitudeStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public boolean isFit() {
        return status == AptitudeStatus.APTA;
    }
}
