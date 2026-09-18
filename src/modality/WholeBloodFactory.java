package modality;


public class WholeBloodFactory implements ModalityFactory {

    @Override
    public DonationModality getModality() {
        return DonationModality.SANGRE_TOTAL;
    }

    @Override
    public CollectionBag createBag() {
        return new CollectionBag() {
            @Override
            public String getBagType() {
                return "Cuádruple con filtro";
            }

            @Override
            public String getAnticoagulant() {
                return "CPD-A1";
            }

            @Override
            public double getNominalVolumeMl() {
                return 450.0;
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
                return "E00";
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
                return "2 a 6 °C";
            }

            @Override
            public int getShelfLifeDays() {
                return 35;
            }

            @Override
            public String toString() {
                return String.format("Protocol[%s, %d days]", getStorageTemperature(), getShelfLifeDays());
            }
        };
    }
}
