package in.edu.kristujayanti.enums;

public enum ResponseCode {
    TYPE("type"),
    MESSAGE("message"),
    STATUS_CODE("statusCode"),
    RESPONSE_DATA("responseData"),
    DATA("data"),
    FLAGS("flags");

    private String property;

    private ResponseCode(String property) {
        this.property = property;
    }

    public String getProperty() {
        return this.property;
    }
}