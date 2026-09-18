package drive;


public class Schedule implements Cloneable {
    private String startTime;
    private String endTime;

    public Schedule(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public Schedule clone() {
        try {
            return (Schedule) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Schedule(this.startTime, this.endTime);
        }
    }

    @Override
    public String toString() {
        return startTime + " - " + endTime;
    }
}
