package service;

import modality.DonationModality;


public class ModalitySummary {
    private final DonationModality modality;
    private final int fitCount;
    private final int unfitCount;
    private final double totalUsableVolumeMl;
    private final double utilizationPercentage;
    private final double averagePunctureTimeMinutes;

    public ModalitySummary(DonationModality modality, int fitCount, int unfitCount,
                           double totalUsableVolumeMl, double utilizationPercentage,
                           double averagePunctureTimeMinutes) {
        this.modality = modality;
        this.fitCount = fitCount;
        this.unfitCount = unfitCount;
        this.totalUsableVolumeMl = totalUsableVolumeMl;
        this.utilizationPercentage = utilizationPercentage;
        this.averagePunctureTimeMinutes = averagePunctureTimeMinutes;
    }

    public DonationModality getModality() {
        return modality;
    }

    public int getFitCount() {
        return fitCount;
    }

    public int getUnfitCount() {
        return unfitCount;
    }

    public int getTotalCount() {
        return fitCount + unfitCount;
    }

    public double getTotalUsableVolumeMl() {
        return totalUsableVolumeMl;
    }

    public double getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public double getAveragePunctureTimeMinutes() {
        return averagePunctureTimeMinutes;
    }
}
