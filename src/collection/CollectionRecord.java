package collection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import modality.CollectionBag;
import modality.DonationModality;
import modality.ModalityFactory;
import modality.PreservationProtocol;
import modality.TraceabilityLabel;


public final class CollectionRecord {


    private final int consecutive;
    private final String donorId;
    private final String siteCode;
    private final LocalDate collectionDate;
    private final DonationModality modality;
    private final double actualVolumeMl;
    private final double punctureTimeMinutes;
    private final String phlebotomist;
    private final String supplyBatch;


    private final CollectionBag bag;
    private final TraceabilityLabel label;
    private final PreservationProtocol preservationProtocol;
    private final String unitCode;


    private final String campaign;
    private final String observations;
    private final List<String> adverseEvents;
    private final boolean firstTimeDonor;

    private CollectionRecord(Builder builder) {
        this.consecutive = builder.consecutive;
        this.donorId = builder.donorId;
        this.siteCode = builder.siteCode;
        this.collectionDate = builder.collectionDate;
        this.modality = builder.modalityFactory.getModality();
        this.actualVolumeMl = builder.actualVolumeMl;
        this.punctureTimeMinutes = builder.punctureTimeMinutes;
        this.phlebotomist = builder.phlebotomist;
        this.supplyBatch = builder.supplyBatch;


        this.bag = builder.modalityFactory.createBag();
        this.label = builder.modalityFactory.createLabel();
        this.preservationProtocol = builder.modalityFactory.createPreservationProtocol();
        this.unitCode = this.label.generateCode(this.siteCode, this.collectionDate.getYear() % 100, this.consecutive);


        this.campaign = builder.campaign;
        this.observations = builder.observations;
        this.adverseEvents = Collections.unmodifiableList(new ArrayList<>(builder.adverseEvents));
        this.firstTimeDonor = builder.firstTimeDonor;
    }

    public static Builder builder() {
        return new Builder();
    }


    public int getConsecutive() {
        return consecutive;
    }

    public String getDonorId() {
        return donorId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public DonationModality getModality() {
        return modality;
    }

    public double getActualVolumeMl() {
        return actualVolumeMl;
    }

    public double getPunctureTimeMinutes() {
        return punctureTimeMinutes;
    }

    public String getPhlebotomist() {
        return phlebotomist;
    }

    public String getSupplyBatch() {
        return supplyBatch;
    }

    public CollectionBag getBag() {
        return bag;
    }

    public TraceabilityLabel getLabel() {
        return label;
    }

    public PreservationProtocol getPreservationProtocol() {
        return preservationProtocol;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getCampaign() {
        return campaign;
    }

    public String getObservations() {
        return observations;
    }

    public List<String> getAdverseEvents() {
        return adverseEvents;
    }

    public boolean isFirstTimeDonor() {
        return firstTimeDonor;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %.0f mL | Puncture: %.1f min | Batch: %s",
                unitCode, modality, actualVolumeMl, punctureTimeMinutes, supplyBatch);
    }


    public static class Builder {
        private Integer consecutive;
        private String donorId;
        private String siteCode;
        private LocalDate collectionDate;
        private ModalityFactory modalityFactory;
        private Double actualVolumeMl;
        private Double punctureTimeMinutes;
        private String phlebotomist;
        private String supplyBatch;

        private String campaign;
        private String observations;
        private List<String> adverseEvents = new ArrayList<>();
        private boolean firstTimeDonor = false;

        public Builder consecutive(int consecutive) {
            this.consecutive = consecutive;
            return this;
        }

        public Builder donorId(String donorId) {
            this.donorId = donorId;
            return this;
        }

        public Builder siteCode(String siteCode) {
            this.siteCode = siteCode;
            return this;
        }

        public Builder collectionDate(LocalDate collectionDate) {
            this.collectionDate = collectionDate;
            return this;
        }

        public Builder modalityFactory(ModalityFactory modalityFactory) {
            this.modalityFactory = modalityFactory;
            return this;
        }

        public Builder actualVolumeMl(double actualVolumeMl) {
            this.actualVolumeMl = actualVolumeMl;
            return this;
        }

        public Builder punctureTimeMinutes(double punctureTimeMinutes) {
            this.punctureTimeMinutes = punctureTimeMinutes;
            return this;
        }

        public Builder phlebotomist(String phlebotomist) {
            this.phlebotomist = phlebotomist;
            return this;
        }

        public Builder supplyBatch(String supplyBatch) {
            this.supplyBatch = supplyBatch;
            return this;
        }

        public Builder campaign(String campaign) {
            this.campaign = campaign;
            return this;
        }

        public Builder observations(String observations) {
            this.observations = observations;
            return this;
        }

        public Builder addAdverseEvent(String adverseEvent) {
            if (adverseEvent != null && !adverseEvent.isBlank()) {
                this.adverseEvents.add(adverseEvent);
            }
            return this;
        }

        public Builder adverseEvents(List<String> events) {
            if (events != null) {
                this.adverseEvents = new ArrayList<>(events);
            }
            return this;
        }

        public Builder firstTimeDonor(boolean firstTimeDonor) {
            this.firstTimeDonor = firstTimeDonor;
            return this;
        }


        public CollectionRecord build() {

            if (consecutive == null || consecutive <= 0) {
                throw new IllegalStateException(
                        "Cannot build record: missing or invalid mandatory field 'consecutive'");
            }
            if (donorId == null || donorId.isBlank()) {
                throw new IllegalStateException("Cannot build record: missing mandatory field 'donorId'");
            }
            if (siteCode == null || siteCode.isBlank()) {
                throw new IllegalStateException("Cannot build record: missing mandatory field 'siteCode'");
            }
            if (collectionDate == null) {
                throw new IllegalStateException("Cannot build record: missing mandatory field 'collectionDate'");
            }
            if (modalityFactory == null) {
                throw new IllegalStateException("Cannot build record: missing mandatory field 'modalityFactory'");
            }
            if (actualVolumeMl == null || actualVolumeMl <= 0) {
                throw new IllegalStateException(
                        "Cannot build record: missing or invalid mandatory field 'actualVolumeMl'");
            }
            if (punctureTimeMinutes == null || punctureTimeMinutes <= 0) {
                throw new IllegalStateException(
                        "Cannot build record: missing or invalid mandatory field 'punctureTimeMinutes'");
            }
            if (supplyBatch == null || supplyBatch.isBlank()) {
                throw new IllegalStateException(
                        "Cannot build record: missing mandatory field 'supplyBatch' (loteInsumos)");
            }


            boolean hasAdverseEvents = adverseEvents != null && !adverseEvents.isEmpty();
            if (hasAdverseEvents && (phlebotomist == null || phlebotomist.isBlank())) {
                throw new IllegalStateException(
                        "Cannot build record: adverse events are present but responsible 'phlebotomist' is missing");
            }


            if (phlebotomist == null || phlebotomist.isBlank()) {
                throw new IllegalStateException("Cannot build record: missing mandatory field 'phlebotomist'");
            }


            if (punctureTimeMinutes > 15.0 && (observations == null || observations.isBlank())) {
                throw new IllegalStateException(
                        "Cannot build record: puncture time exceeds 15 minutes but no 'observations' were registered");
            }

            return new CollectionRecord(this);
        }
    }
}
