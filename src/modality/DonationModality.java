package modality;


public enum DonationModality {
    SANGRE_TOTAL("Whole Blood"),
    AFERESIS_PLAQUETAS("Platelet Apheresis"),
    AFERESIS_PLASMA("Plasma Apheresis");

    private final String description;

    DonationModality(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
