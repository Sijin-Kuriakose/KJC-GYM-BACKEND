package in.edu.kristujayanti.enums;

public enum UIKeyEnum {
    KEY("key");

    private final String property;

    private UIKeyEnum(String property) {
        this.property = property;
    }

    public String getProperty() {
        return this.property;
    }
}