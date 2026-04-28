package in.edu.kristujayanti.enums;

public enum UIPropertyKey {
    HTTP_CODE("HTTP_CODE."),
    FORM_VALIDATION("FORM_VALIDATION."),
    FAILED("FAILED"),
    CONTACT_SYSTEM_ADMIN("CONTACT_SYSTEM_ADMIN"),
    F_AND_B_PROJECT_IS_DOWN("F_AND_B_PROJECT_IS_DOWN"),
    REQUIRED("REQUIRED"),
    INVALID("INVALID"),
    ENUM("ENUM"),
    BOOLEANFIELD("BOOLEAN-FIELD"),
    NUMERICFIELD("NUMERIC-FIELD"),
    DATEFORMAT("DATE-FORMAT"),
    TIME("TIME"),
    STRING("STRING"),
    DOCUMENT("DOCUMENT"),
    DATETIME("DATE-TIME"),
    TEXTARRAY("TEXT-ARRAY"),
    INTARRAY("INT-ARRAY"),
    NOT_LIST("NOT-LIST"),
    CODE_SETTING_DOES_NOT_EXIST("CODE_SETTING_DOES_NOT_EXIST");

    private final String property;

    private UIPropertyKey(String uIProperty) {
        this.property = uIProperty;
    }

    public String getProperty() {
        return this.property;
    }

    // $FF: synthetic method
    private static UIPropertyKey[] $values() {
        return new UIPropertyKey[]{HTTP_CODE, FORM_VALIDATION, FAILED, CONTACT_SYSTEM_ADMIN, F_AND_B_PROJECT_IS_DOWN, REQUIRED, INVALID, ENUM, BOOLEANFIELD, NUMERICFIELD, DATEFORMAT, STRING, DOCUMENT, DATETIME, TEXTARRAY, NOT_LIST, CODE_SETTING_DOES_NOT_EXIST};
    }
}