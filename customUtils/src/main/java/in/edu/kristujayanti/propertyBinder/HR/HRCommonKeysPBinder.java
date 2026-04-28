package in.edu.kristujayanti.propertyBinder.HR;

import in.edu.kristujayanti.propertyBinder.Eform.Applicant.ApplicantProgrammeKeysPBinder;
import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

public enum HRCommonKeysPBinder implements KJUSYSPropertyBinder {

    EMPLOYEE_OLD_PASSWORD("employeeOldPassword_HRCommon_Text", "Employee Old Password", String.class)

    ;



    private final String property;
    private final String label;
    private final Class dataType;
    private final int minLength;
    private final int maxLength;

    private HRCommonKeysPBinder(String property, String label, Class dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private HRCommonKeysPBinder(String property, String label, Class dataType) {
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

    public Class getDataType() {
        return this.dataType;
    }

    public Integer getMinLength() {
        return this.minLength;
    }

    public Integer getMaxLength() {
        return this.maxLength;
    }

    // $FF: synthetic method
    private static HRCommonKeysPBinder[] $values() {
        return HRCommonKeysPBinder.class.getEnumConstants();
    }
}
