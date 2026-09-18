package drive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modality.DonationModality;


public class MobileDriveTemplate implements Cloneable {
    private String name;
    private String site;
    private String municipality;
    private int targetUnits;
    private DonationModality defaultModality;
    private Schedule schedule;
    private List<String> requiredSupplies;

    public MobileDriveTemplate(String name, String site, String municipality,
                               int targetUnits, DonationModality defaultModality,
                               Schedule schedule, List<String> requiredSupplies) {
        this.name = name;
        this.site = site;
        this.municipality = municipality;
        this.targetUnits = targetUnits;
        this.defaultModality = defaultModality;
        this.schedule = schedule;
        this.requiredSupplies = new ArrayList<>(requiredSupplies != null ? requiredSupplies : List.of());
    }


    @Override
    public MobileDriveTemplate clone() {
        try {
            MobileDriveTemplate cloned = (MobileDriveTemplate) super.clone();

            cloned.schedule = (this.schedule != null) ? this.schedule.clone() : null;
            cloned.requiredSupplies = new ArrayList<>(this.requiredSupplies);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning failed on Cloneable object", e);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public int getTargetUnits() {
        return targetUnits;
    }

    public void setTargetUnits(int targetUnits) {
        this.targetUnits = targetUnits;
    }

    public DonationModality getDefaultModality() {
        return defaultModality;
    }

    public void setDefaultModality(DonationModality defaultModality) {
        this.defaultModality = defaultModality;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<String> getRequiredSupplies() {
        return Collections.unmodifiableList(requiredSupplies);
    }

    public int getSupplyCount() {
        return requiredSupplies.size();
    }

    public void addSupply(String supply) {
        if (supply != null && !supply.isBlank()) {
            this.requiredSupplies.add(supply);
        }
    }

    @Override
    public String toString() {
        return String.format("%s | site: %s | mun: %s | target: %d | default: %s | schedule: %s | supplies: %d",
                name, site, municipality, targetUnits, defaultModality, schedule, requiredSupplies.size());
    }
}
