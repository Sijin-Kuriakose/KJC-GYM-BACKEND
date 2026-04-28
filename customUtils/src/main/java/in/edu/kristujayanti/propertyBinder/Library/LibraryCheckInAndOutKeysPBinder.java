package in.edu.kristujayanti.propertyBinder.Library;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

public enum LibraryCheckInAndOutKeysPBinder implements KJUSYSPropertyBinder {

    LIBRARY_CHECK_IN_DATE_MILLISECONDS("libraryCheckInDateMilliseconds_LibraryCheckInAndOut_Long", "Library Check In Date Milliseconds", Long.class),
    LIBRARY_CHECK_IN_TIME_MILLISECONDS("libraryCheckInTimeMilliseconds_LibraryCheckInAndOut_Long", "Library Check In Time Milliseconds", Long.class),
    LIBRARY_CHECK_OUT_TIME_MILLISECONDS("libraryCheckOutTimeMilliseconds_LibraryCheckInAndOut_Long", "Library Check Out Time Milliseconds", Long.class),
    LIBRARY_CHECK_IN_CHECK_OUT_PURPOSE("libraryCheckInCheckOutPurpose_LibraryCheckInAndOut_Text", "Library Check In Check Out Purpose", String.class);

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private LibraryCheckInAndOutKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private LibraryCheckInAndOutKeysPBinder(String property, String label, Class<?> dataType) {
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
    private static LibraryCheckInAndOutKeysPBinder[] $values() {
        return LibraryCheckInAndOutKeysPBinder.class.getEnumConstants();
    }
}