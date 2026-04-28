package in.edu.kristujayanti.propertyBinder.HR;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

public enum AwardsDataKeyPBinders implements KJUSYSPropertyBinder {
    AWARD_NAME("awardName_AwardsDataKeys_Text","Award Name ",String.class),
    EVENT_NAME("eventName_AwardsDataKeys_Text","Event Name",String.class),
    AWARDED_ON("awardedOn_AwardsDataKeys_Date","Awarded on",Long.class),
    ORGANIZED_BY("organizedBy_AwardsDataKeys_Text","Organized By",String.class),
    AWARD_CREATED_BY("awardCreatedBy_AwardsDataKeys_Text"," created by staff name",String.class),
       AWARD_OID("awardOid_AwardsDataKeys_Text"," created by staff name",String.class)


    ;

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;

    private final int maxLength;

    private AwardsDataKeyPBinders(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private AwardsDataKeyPBinders(String property, String label, Class<?> dataType) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = -1;
        this.maxLength = -1;
    }

    public String getPropertyName() {
        return this.property;
    }

    public String getDisplayName() {
        return this.label;
    }

    public Class<?> getDataType() {
        return this.dataType;
    }

    public Integer getMinLength() {
        return this.minLength;
    }

    public Integer getMaxLength() {
        return this.maxLength;
    }

    private static PrincipalViewKeysPBinder[] $values() {
        return PrincipalViewKeysPBinder.class.getEnumConstants();
    }

}
