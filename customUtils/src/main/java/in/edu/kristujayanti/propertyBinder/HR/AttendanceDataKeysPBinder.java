package in.edu.kristujayanti.propertyBinder.HR;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import org.bson.Document;

public enum AttendanceDataKeysPBinder implements KJUSYSPropertyBinder {

    ENTRY_ID("entryId_AttendanceData_Int", "Entry Id", Integer.class),
    ATTENDANCE_ENTRY_DATE("attendanceEntryDate_AttendanceData_Date", "Attendance Entry Date", Long.class),
    ATTENDANCE_PUNCH_IN("attendancePunchIn_AttendanceData_DateTime", "Attendance PunchIn Timestamp", Long.class),
    ATTENDANCE_PUNCH_OUT("attendancePunchOut_AttendanceData_DateTime", "Attendance PunchOut Timestamp", Long.class),
    LEAVE_START_DATE("leaveStartDate_AttendanceData_Date", "Leave Start Date", Long.class),
    LEAVE_END_DATE("leaveEndDate_AttendanceData_Date", "Leave End Date", Long.class),
    LEAVE_CODE("leaveCode_AttendanceData_Text", "Leave Code", String.class),
    ATTENDANCE_DATA_DOCUMENT_ARRAY("attendanceDataDocumentArray_AttendanceData_DocumentArray", "Attendance Data Document Array", Document.class),

    ;

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;

    private final int maxLength;

    private AttendanceDataKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private AttendanceDataKeysPBinder(String property, String label, Class<?> dataType) {
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

    private static AttendanceDataKeysPBinder[] $values() {
        return AttendanceDataKeysPBinder.class.getEnumConstants();
    }

}