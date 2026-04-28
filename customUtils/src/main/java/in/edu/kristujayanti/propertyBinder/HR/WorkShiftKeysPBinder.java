package in.edu.kristujayanti.propertyBinder.HR;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

import javax.swing.text.Document;

public enum WorkShiftKeysPBinder implements KJUSYSPropertyBinder {
    // TIMETABLE
    TIMETABLE_NAME("timetableName_WorkShift_Text", "Timetable Name", String.class),
    START_TIME("startTime_WorkShift_Time", "Shift Start Time", Long.class),
    END_TIME("endTime_WorkShift_Time", "Shift End Time", Long.class),

    // SHIFT
    SHIFT_NAME("shiftName_WorkShift_Text", "Shift Name", String.class),
    SHIFT_WEEKLY_TIMETABLES("shiftWeeklyTimetables_WorkShift_Document", "Shift Weekly Timetables", Document.class),
    DAY_1("day1_WorkShift_Document", "DAY_1", Document.class),
    DAY_2("day2_WorkShift_Document", "DAY_2", Document.class),
    DAY_3("day3_WorkShift_Document", "DAY_3", Document.class),
    DAY_4("day4_WorkShift_Document", "DAY_4", Document.class),
    DAY_5("day5_WorkShift_Document", "DAY_5", Document.class),
    DAY_6("day6_WorkShift_Document", "DAY_6", Document.class),
    DAY_7("day7_WorkShift_Document", "DAY_7", Document.class),

    // SHIFT MAPPING
    SHIFT_START_DATE("shiftStartDate_WorkShift_Date", "Shift Start Date", Long.class),
    SHIFT_END_DATE("shiftEndDate_WorkShift_Date", "Shift End Date", Long.class),
    SHIFT_EMPLOYEE_CODES("shiftEmployeeCodes_WorkShift_TextArray", "Shift Employee Codes", String.class),

    // HOLIDAYS
    HOLIDAY_DATE("holidayDate_WorkShift_Date", "Holiday Date", Long.class),
    HOLIDAY_DATES("holidayDates_WorkShift_TextArray", "Holiday Dates", String.class),
    HOLIDAY_EMPLOYEES_LIST("holidayEmployeesList_WorkShift_TextArray", "Holiday Employees List", String.class),
    HOLIDAY_TYPE("holidayType_WorkShift_Text", "Holiday Type", String.class),
    ;

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private WorkShiftKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength){
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private WorkShiftKeysPBinder(String property, String label, Class<?> dataType) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = -1;
        this.maxLength = -1;
    }

    @Override
    public String getPropertyName() {
        return this.property;
    }

    @Override
    public String getDisplayName() {
        return this.label;
    }

    @Override
    public Class<?> getDataType() {
        return this.dataType;
    }

    @Override
    public Integer getMinLength() {
        return this.minLength;
    }

    @Override
    public Integer getMaxLength() {
        return this.maxLength;
    }

    public static String getLabelByPropertyName(String propertyName) {
        for (WorkShiftKeysPBinder binder : WorkShiftKeysPBinder.values()) {
            if (binder.getPropertyName().equals(propertyName)) {
                return binder.getDisplayName();
            }
        }
        return null; // Or throw an exception if needed
    }

    // $FF: synthetic method
    private static WorkShiftKeysPBinder[] $values() {
        return WorkShiftKeysPBinder.class.getEnumConstants();
    }
}
