package in.edu.kristujayanti.propertyBinder.academics;

import in.edu.kristujayanti.propertyBinder.Eform.EformCommonKeysPBinder;
import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

public enum AcademicsCommonKeysPBinder implements KJUSYSPropertyBinder {

    // define the academic property binders

    //semester PBInder
//    SEMESTER("semester_AcademicsCommon_Text","Semester",Integer.class),
    SEMESTER_DISPLAY_NAME("semesterDisplayName_AcademicsCommon_Text","Semester Display Name",String.class),
    CASTE_CATEGORY_NAME("casteCategoryName_AcademicsCommon_Text","caste category",String.class);






    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private AcademicsCommonKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private AcademicsCommonKeysPBinder(String property, String label, Class<?> dataType) {
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
    private static AcademicsCommonKeysPBinder[] $values() {
        return AcademicsCommonKeysPBinder.class.getEnumConstants();
    }
}
