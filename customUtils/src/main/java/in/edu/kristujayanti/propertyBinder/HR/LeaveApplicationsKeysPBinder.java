package in.edu.kristujayanti.propertyBinder.HR;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import org.bson.types.ObjectId;

import javax.swing.text.Document;


public enum LeaveApplicationsKeysPBinder implements KJUSYSPropertyBinder {


    LEAVE_APPLIED_TYPE("leaveAppliedType_LeaveApplications_Text","Leave Applied Type",String.class),
    LEAVE_APPLIED_SLOT("leaveAppliedSlot_LeaveApplications_Text","Leave Applied Slot",String.class),
    LEAVE_APPLIED_SLOT_PERIODS("leaveAppliedSlotPeriods_LeaveApplications_Text","Leave Applied Slot Periods",String.class),
    LEAVE_APPLIED_START_DATE("leaveAppliedStartDate_LeaveApplications_Date","Leave Applied Start Date", Long.class),
    LEAVE_APPLIED_END_DATE("leaveAppliedEndDate_LeaveApplications_Date","Leave Applied End Date", Long.class),
    HOURLY_PERMISSION_DATE("hourlyPermissionDate_LeaveApplications_Date","Hourly permission Date",Long.class),
    HOURLY_PERMISSION_FROM_TIME("hourlyPermissionFromTime_LeaveApplications_Text","Hourly Permission From Time",String.class),
    HOURLY_PERMISSION_TO_TIME("hourlyPermissionToTime_LeaveApplications_Text","Hourly Permission To Time",String.class),
    HOURLY_PERMISSION_SPECIFIED_HOUR("hourlyPermissionSpecifiedHour_LeaveApplications_Text","Hourly Permission Specified Hour",String.class),

    LEAVE_APPLIED_REASON("leaveAppliedReason_LeaveApplications_Text","Leave Application Text", String.class),
    NUMBER_OF_HP_AVAILED("numberOfHpAvailed_LeaveApplications_Int","Number of HP Availed This Month",Integer.class),
    NUMBER_OF_CL_AVAILED("numberOfClAvailed_LeaveApplications_Int","Number of CL Availed So Far",Integer.class),
    NUMBER_OF_OD_AVAILED("numberOfOdAvailed_LeaveApplications_Int","Number Of OD Availed So Far", Integer.class),
    DATE_OF_CLASS_ARRANGEMENT("DateOfClassArrangement_LeaveApplications_Date","Class Arrangement Date",Long.class),
    NAME_OF_CLASS_ARRANGEMENT_MADE("nameOfClassArrangementMade_LeaveApplications_Text","Name Of Class Arrangement Made",String.class),
    CLASS_ARRANGEMENT_HOUR_FROM("classArrangementHourFrom_LeaveApplications_Text","Class Arrangement Hour From",String.class),
    CLASS_ARRANGEMENT_HOUR_TO("classArrangementHourTo_LeaveApplications_Text","Class Arrangement Hour To",String.class),
    NAME_OF_FACULTY_FOR_CLASS_ARRANGEMENT("nameOfFacultyForClassArrangement_LeaveApplications_Text","Name Of Faculty For Class Arrangement",String.class),
    ON_DUTY_LEAVE_APPLIED_FILE_UPLOAD("onDutyLeaveAppliedFileUpload_LeaveApplications_File","On Duty Leave Applied File",String.class),
    LEAVE_REQUEST_STATUS("leaveRequestStatus_LeaveApplications_Text","Leave Request Status", String.class),
    LEAVE_REQUEST_MEET_IN_PERSON_REMARK("leaveRequestMeetInPersonRemark_LeaveApplications_Text","Leave Request Meet In Person Remark",String.class),

    //REQUEST_TO_MEET_IN_PERSON_NAME

    UPDATE_CLASS_ARRANGEMENT_FLAG("updateClassArrangementFlag_LeaveApplications_Bool","Update  Class Arrangement Flag", Boolean.class),
    CLASS_ARRANGEMENT_DETAILS("classArrangementDetails_LeaveApplications_DocumentArray","Class Arrangement Details", Document.class),
    EMPLOYEE_AUTH_OBJECTID("employeeAuthObjectid_LeaveApplications_ObjectId", "Employee Auth Objectid", ObjectId.class),
    LEAVE_APPLICATION_OID("leaveApplicationOid_LeaveApplications_ObjectId","Leave Application Oid",ObjectId.class),
    NUMBER_OF_DAYS_LEAVE_APPLIED("numberOfDaysLeaveApplied_LeaveApplications_Int","Number Of Days Leave Applied", Integer.class)












    ;

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;

    private final int maxLength;

    private LeaveApplicationsKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private LeaveApplicationsKeysPBinder(String property, String label, Class<?> dataType) {
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

    private static LeaveApplicationsKeysPBinder[] $values() {
        return LeaveApplicationsKeysPBinder.class.getEnumConstants();
    }

}
