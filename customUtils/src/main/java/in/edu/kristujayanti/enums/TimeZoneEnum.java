package in.edu.kristujayanti.enums;

public enum TimeZoneEnum {
    ASIA_KOLKATA("Asia/Kolkata"),
    UTC("UTC");

    private final String zoneId;

    private TimeZoneEnum(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneId() {
        return this.zoneId;
    }

    // $FF: synthetic method
    private static TimeZoneEnum[] $values() {
        return new TimeZoneEnum[]{ASIA_KOLKATA, UTC};
    }
}