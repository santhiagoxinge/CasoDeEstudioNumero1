package service;


public enum AptitudeStatus {
    APTA("APTA"),
    NO_APTA("NO APTA");

    private final String label;

    AptitudeStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
