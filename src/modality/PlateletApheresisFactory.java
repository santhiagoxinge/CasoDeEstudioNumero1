package modality;


public class PlateletApheresisFactory implements ModalityFactory {

    @Override
    public DonationModality getModality() {
        return DonationModality.AFERESIS_PLAQUETAS;
    }

    @Override
    public CollectionBag createBag() {
        return new CollectionBag() {
            @Override
            public String getBagType() {
                return "Kit aféresis PLT";
            }

            @Override
            public String getAnticoagulant() {
                return "ACD-A";
            }

            @Override
            public double getNominalVolumeMl() {
                return 300.0;
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
                return "E30";
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
                return "20 a 24 °C en agitación";
            }

            @Override
            public int getShelfLifeDays() {
                return 5;
            }

            @Override
            public String toString() {
                return String.format("Protocol[%s, %d days]", getStorageTemperature(), getShelfLifeDays());
            }
        };
    }
}
