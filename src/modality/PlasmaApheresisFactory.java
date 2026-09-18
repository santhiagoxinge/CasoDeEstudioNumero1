package modality;


public class PlasmaApheresisFactory implements ModalityFactory {

    @Override
    public DonationModality getModality() {
        return DonationModality.AFERESIS_PLASMA;
    }

    @Override
    public CollectionBag createBag() {
        return new CollectionBag() {
            @Override
            public String getBagType() {
                return "Kit aféresis PLS";
            }

            @Override
            public String getAnticoagulant() {
                return "ACD-A";
            }

            @Override
            public double getNominalVolumeMl() {
                return 600.0;
            }

            @Override
            public String toString() {
                return String.format("Bag[%s, %s, %.0f mL]", getBagType(), getAnticoagulant(), getNominalVolumeMl());
            }
        };
    }

    @Override
    public TraceabilityLabel createLabel() {
        return new TraceabilityLabel() {
            @Override
            public String getPrefix() {
                return "E70";
            }

            @Override
            public String toString() {
                return "Label[Prefix: " + getPrefix() + "]";
            }
        };
    }

    @Override
    public PreservationProtocol createPreservationProtocol() {
        return new PreservationProtocol() {
            @Override
            public String getStorageTemperature() {
                return "-25 °C o inferior";
            }

            @Override
            public int getShelfLifeDays() {
                return 365;
            }

            @Override
            public String toString() {
                return String.format("Protocol[%s, %d days]", getStorageTemperature(), getShelfLifeDays());
            }
        };
    }
}
