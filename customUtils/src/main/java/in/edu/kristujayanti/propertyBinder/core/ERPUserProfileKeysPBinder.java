package in.edu.kristujayanti.propertyBinder.core;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import org.bson.Document;

public enum ERPUserProfileKeysPBinder implements KJUSYSPropertyBinder {
    // Personal Details
    EMPLOYEE_SALUTATION("employeeSalutation_ERPUserProfile_Text", "Employee Salutation", String.class),
    EMPLOYEE_FULL_NAME("employeeFullName_ERPUserProfile_Text", "Employee Full Name", String.class),
    EMPLOYEE_FATHER_NAME("employeeFatherName_ERPUserProfile_Text", "Employee Father Name", String.class),
    EMPLOYEE_MOTHER_NAME("employeeMotherName_ERPUserProfile_Text", "Employee Mother Name", String.class),
    EMPLOYEE_SPOUSE_NAME("employeeSpouseName_ERPUserProfile_Text", "Employee Spouse Name", String.class),
    EMPLOYEE_DOB("employeeDob_ERPUserProfile_Date", "Employee DOB", Long.class),
    EMPLOYEE_MONTH_OF_BIRTH("employeeMonthOfBirth_ERPUserProfile_Int", "Employee Month of Birth", Integer.class),
    EMPLOYEE_DAY_OF_BIRTH("employeeDayOfBirth_ERPUserProfile_Int", "Employee Day of Birth", Integer.class),
    EMPLOYEE_PERSONAL_EMAIL("employeePersonalEmail_ERPUserProfile_Text", "Employee Personal Email", String.class),
    EMPLOYEE_MARITAL_STATUS("employeeMaritalStatus_ERPUserProfile_Text", "Employee Marital Status", String.class),
    EMPLOYEE_GENDER("employeeGender_ERPUserProfile_Text", "Employee Gender", String.class),
    EMPLOYEE_MOBILE_NUMBER("employeeMobileNumber_ERPUserProfile_Text", "Employee Mobile Number", String.class),
    EMPLOYEE_WHATSAPP_NUMBER("employeeWhatsappNumber_ERPUserProfile_Text", "Employee Whatsapp Number", String.class),
    EMPLOYEE_EMERGENCY_CONTACT_NAME("employeeEmergencyContactName_ERPUserProfile_Text", "Employee Emergency Contact Name", String.class),
    EMPLOYEE_EMERGENCY_CONTACT_NUMBER("employeeEmergencyContactNumber_ERPUserProfile_Text", "Employee Emergency Contact Number", String.class),

    // Additional Details
    EMPLOYEE_AADHAR_NUMBER("employeeAadharNumber_ERPUserProfile_Text", "Employee Aadhar Number", String.class),
    EMPLOYEE_PAN_NUMBER("employeePanNumber_ERPUserProfile_Text", "Employee Pan Number", String.class),
    EMPLOYEE_NATIONALITY("employeeNationality_ERPUserProfile_Text", "Employee Nationality", String.class),
    EMPLOYEE_MOTHER_TONGUE("employeeMotherTongue_ERPUserProfile_Text", "Employee Mother Tongue", String.class),
    EMPLOYEE_RELIGION("employeeReligion_ERPUserProfile_Text", "Employee Religion", String.class),
    EMPLOYEE_SOCIAL_CATEGORY("employeeSocialCategory_ERPUserProfile_Text", "Employee Social Category", String.class),
    EMPLOYEE_BLOOD_GROUP("employeeBloodGroup_ERPUserProfile_Text", "Employee Blood Group", String.class),
    EMPLOYEE_IS_DIFFERENTLY_ABLED("employeeIsDifferentlyAbled_ERPUserProfile_Bool", "Employee Has Disability", Boolean.class),

    // Professional Details
    EMPLOYEE_CODE("employeeCode_ERPUserProfile_Text", "Employee Code", String.class),
    EMPLOYEE_APPOINTMENT_ORDER_NUMBER("employeeAppointmentOrderNumber_ERPUserProfile_Text", "Employee Appointment Order Number", String.class),
    EMPLOYEE_JOINING_DATE("employeeJoiningDate_ERPUserProfile_Date", "Employee Joining Date", Long.class),
    EMPLOYEE_TYPE("employeeType_ERPUserProfile_Text", "Employee Type", String.class),
    EMPLOYEE_JOB_TYPE("employeeJobType_ERPUserProfile_Text", "Employee Job Type", String.class),
    EMPLOYEE_OFFICIAL_EMAIL("employeeOfficialEmail_ERPUserProfile_Text", "Employee Official Email", String.class),
    EMPLOYEE_PROBATION_START_DATE("employeeProbationStartDate_ERPUserProfile_Date", "Employee Probation Start Date", Long.class),
    EMPLOYEE_PROBATION_END_DATE("employeeProbationEndDate_ERPUserProfile_Date", "Employee Probation End Date", Long.class),
    EMPLOYEE_CATEGORY("employeeCategory_ERPUserProfile_Text", "Employee Category", String.class),
    EMPLOYEE_PF_NUMBER("employeePfNumber_ERPUserProfile_Text", "Employee PF Number", String.class),
    EMPLOYEE_CURRENT_CAMPUS("employeeCurrentCampus_ERPUserProfile_Text", "Employee Current Campus", String.class),
    EMPLOYEE_DEPARTMENT("employeeDepartment_ERPUserProfile_Text", "Employee Department", String.class),
    EMPLOYEE_SUB_DEPARTMENT("employeeSubDepartment_ERPUserProfile_Text", "Employee Sub Department", String.class),
    EMPLOYEE_DESIGNATION("employeeDesignation_ERPUserProfile_Text", "Employee Designation", String.class),

    EMPLOYEE_ID_PHOTO_PATH("employeeIdPhotoPath_ERPUserProfile_Text", "Employee Id Photo Path", String.class),

    EMPLOYEE_WORK_SHIFT("employeeWorkShift_ERPUserProfile_DocumentArray", "Employee Work Shift", Document.class),

    ;

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private ERPUserProfileKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private ERPUserProfileKeysPBinder(String property, String label, Class<?> dataType) {
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

    // $FF: synthetic method
    private static ERPUserProfileKeysPBinder[] $values() {
        return ERPUserProfileKeysPBinder.class.getEnumConstants();
    }
}