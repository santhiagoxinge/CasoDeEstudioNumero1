package modality;


public interface TraceabilityLabel {

    String getPrefix();


    default String generateCode(String siteCode, int yearTwoDigits, int consecutive) {
        String cleanSite = (siteCode == null ? "UNK" : siteCode.trim().toUpperCase());
        int cleanYear = Math.abs(yearTwoDigits) % 100;
        return String.format("%s-%s-%02d-%05d", getPrefix(), cleanSite, cleanYear, consecutive);
    }
}
