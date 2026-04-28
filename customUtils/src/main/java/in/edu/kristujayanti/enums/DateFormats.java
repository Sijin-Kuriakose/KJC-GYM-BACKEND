package in.edu.kristujayanti.enums;

public enum DateFormats {
    YYYYMMDD("YYYYMMDD"),
    DDMMYYYY("DDMMYYYY"),
    DDMMYYYYHIPHENAVOIDED("DDMMYYYY"),
    DDMMYYYYHHMMSS("DDMMYYYY HHMMSS"),
    DDMMYYYYHHMM("DDMMYYYY HHMM"),
    YYYYMMDDHHMMSS("YYYYMMDD HHMMSS"),
    YYYYMMDDHHMM("YYYYMMDD HHMM"),
    DDMMYY("ddMMyy"),
    DMMMYYYY("d/MM/yyyy"),
    DMMMMYYYYHHMMZZZ("d MMMM yyyy HH:mm zzz"),
    DDMMYYYYHHMMSSSLASH("dd/MM/yyyy HH:mm:ss"),
    HHMMSSA("hh:mm:ss a"),
    YYYYMMdd("YYYYMMdd"),
    DMMMMYYYY("d MMMM yyyy"),
    HHMMDDMMYYY("HH:mm, dd-MM-yyyy");

    private final String displayName;

    private DateFormats(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    // $FF: synthetic method
    private static DateFormats[] $values() {
        return new DateFormats[]{YYYYMMDD, DDMMYYYY, DDMMYYYYHIPHENAVOIDED, DDMMYYYYHHMMSS, DDMMYYYYHHMM, YYYYMMDDHHMMSS, YYYYMMDDHHMM, DDMMYY, DMMMYYYY, DMMMMYYYYHHMMZZZ, DDMMYYYYHHMMSSSLASH, HHMMSSA, YYYYMMdd, DMMMMYYYY, HHMMDDMMYYY};
    }
}