package service;

import collection.CollectionRecord;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import modality.DonationModality;


public class InventoryService {


    public LocalDate calculateExpirationDate(CollectionRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Record cannot be null");
        }
        return record.getCollectionDate().plusDays(record.getPreservationProtocol().getShelfLifeDays());
    }


    public AptitudeEvaluation evaluateFitness(CollectionRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Record cannot be null");
        }

        double nominal = record.getBag().getNominalVolumeMl();
        double minAllowed = nominal * 0.90;
        double maxAllowed = nominal * 1.10;
        double actual = record.getActualVolumeMl();

        LocalDate expirationDate = calculateExpirationDate(record);

        if (actual >= minAllowed && actual <= maxAllowed) {
            return new AptitudeEvaluation(record, AptitudeStatus.APTA, null, expirationDate);
        } else {
            return new AptitudeEvaluation(record, AptitudeStatus.NO_APTA, "VOLUMEN_FUERA_DE_RANGO", expirationDate);
        }
    }


    public List<ModalitySummary> generateModalityConsolidated(List<CollectionRecord> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        Map<DonationModality, List<CollectionRecord>> recordsByModality = new EnumMap<>(DonationModality.class);
        for (DonationModality modality : DonationModality.values()) {
            recordsByModality.put(modality, new ArrayList<>());
        }

        for (CollectionRecord record : records) {
            recordsByModality.get(record.getModality()).add(record);
        }

        List<ModalitySummary> summaries = new ArrayList<>();

        for (DonationModality modality : DonationModality.values()) {
            List<CollectionRecord> modalityRecords = recordsByModality.get(modality);
            int fitCount = 0;
            int unfitCount = 0;
            double totalUsableVolume = 0.0;
            double totalPunctureTime = 0.0;

            for (CollectionRecord record : modalityRecords) {
                AptitudeEvaluation evaluation = evaluateFitness(record);
                totalPunctureTime += record.getPunctureTimeMinutes();

                if (evaluation.isFit()) {
                    fitCount++;
                    totalUsableVolume += record.getActualVolumeMl();
                } else {
                    unfitCount++;
                }
            }

            int total = fitCount + unfitCount;
            double utilizationPercentage = (total > 0) ? (fitCount * 100.0 / total) : 0.0;
            double averagePunctureTime = (total > 0) ? (totalPunctureTime / total) : 0.0;

            summaries.add(new ModalitySummary(
                    modality,
                    fitCount,
                    unfitCount,
                    totalUsableVolume,
                    utilizationPercentage,
                    averagePunctureTime
            ));
        }

        return summaries;
    }


    public List<FefoAlertItem> generateFefoAlert(List<CollectionRecord> records, LocalDate cutoffDate) {
        if (records == null || records.isEmpty() || cutoffDate == null) {
            return Collections.emptyList();
        }

        List<FefoAlertItem> alertItems = new ArrayList<>();

        for (CollectionRecord record : records) {
            AptitudeEvaluation evaluation = evaluateFitness(record);
            if (evaluation.isFit()) {
                LocalDate expirationDate = evaluation.getExpirationDate();
                long daysRemaining = ChronoUnit.DAYS.between(cutoffDate, expirationDate);

                if (daysRemaining <= 7) {
                    alertItems.add(new FefoAlertItem(record, expirationDate, daysRemaining));
                }
            }
        }

        Collections.sort(alertItems);
        return alertItems;
    }


    public double calculateAveragePunctureTime(List<CollectionRecord> records, DonationModality modality) {
        if (records == null || records.isEmpty() || modality == null) {
            return 0.0;
        }

        double totalTime = 0.0;
        int count = 0;

        for (CollectionRecord record : records) {
            if (record.getModality() == modality) {
                totalTime += record.getPunctureTimeMinutes();
                count++;
            }
        }

        return (count > 0) ? (totalTime / count) : 0.0;
    }
}
