package in.edu.kristujayanti.propertyBinder;

import org.bson.Document;

public enum KJUSYSCommonKeysPBinder implements KJUSYSPropertyBinder{

    JWT_EMAIL_CLAIM("email" , "User ID", String.class),
    JWT_USER_ROLES_CLAIM("roles", "Roles", String.class),
    OID("oid_KJUSYSCommon_ObjectId" , "Object ID", String.class),
    CREATED_ON("createdOn_KJUSYSCommon_DateTime", "Created On" , Long.class),
    OTP("otp_KJUSYSCommon_Text", "OTP", String.class),
    ENTITY_ID("entityId_KJUSYSCommon_Long", "Entity ID", Long.class),
    PROGRAM_NAME("programName_KJUSYSCommon_Text", "Program Name", String.class),
    START_DAY("startDay_KJUSYSCommon_Int", "Start Day", Integer.class ),
    START_MONTH("startMonth_KJUSYSCommon_Text", "Start Month", String.class),
    AWARD("award_KJUSYSCommon_Text", "Award", String.class),
    PROGRAM_CODE("programCode_KJUSYSCommon_Text", "Program Code", String.class),
    PROGRAM_DISPLAY_NAME("programDisplayName_KJUSYSCommon_Text", "Program Display Name", String.class),
    DURATION("duration_KJUSYSCommon_Int","Duration", Integer.class),
    DURATION_UNIT("durationUnit_KJUSYSCommon_Text", "Duration Unit", String.class),
    EDUCATION_TYPE("educationType_KJUSYSCommon_Text", "Education Type", String.class),
    FEE_CATEGORY("feeCategory_KJUSYSCommon_Text", "Fee Category", String.class),
    INSTALLMENT("installment_KJUSYSCommon_Int", "Installment", Integer.class),
    FEE_HEAD_CODE("feeHeadCode_KJUSYSCommon_Text", "Fee Head Code", String.class),
    START_DATE("startDate_KJUSYSCommon_Date", "Start Date", Long.class),
    FEE_AMOUNT("feeAmount_KJUSYSCommon_Double" , "Fee Amount" , Double.class),
    CURRENCY("currency_KJUSYSCommon_Text", "Currency" , String.class),
    SCHEDULE_TYPE("scheduleType_KJUSYSCommon_Text" , "Schedule Type", String.class),
    IS_LATERAL("isLateral_KJUSYSCommon_Text", "Is Lateral", String.class),
    LATE_FEE("lateFee_KJUSYSCommon_Text", "Late Fee", String.class),
    FOR_ADMISSION("forAdmission_KJUSYSCommon_Text", "For Admission", String.class),
    FOR_ENROLLED_STUDENT("forEnrolledStudent_KJUSYSCommon_Text", "For Enrolled Student", String.class),
    PAYMENT_STATUS("paymentStatus_KJUSYSCommon_Bool", "Payment Status", Boolean.class),
    INSTITUTION_NAME("institutionName_KJUSYSCommon_Text", "Institution Name", String.class),
    INSTITUTION_ID("institutionId_KJUSYSCommon_Int", "Institution ID", Integer.class),
    INSTITUTION_STATUS("institutionStatus_KJUSYSCommon_Bool", "Institution Status", Boolean.class),
    EDUCATION_CATEGORY("educationCategory_KJUSYSCommon_DocumentArray", "Education Category", Document.class),
    EDUCATION_TYPE_ID("educationTypeId_KJUSYSCommon_Int", "Education Type ID", Integer.class),
    EDUCATION_TYPE_STATUS("educationTypeStatus_KJUSYSCommon_Bool", "Education Type Status", Boolean.class),

    BATCH_CODE("batchCode_KJUSYSCommon_Text", "Batch Code", String.class),
    BATCH_NAME("batchName_KJUSYSCommon_Text", "Batch Name", String.class),
    ACADEMIC_YEAR("academicYear_KJUSYSCommon_Text", "Academic Year", String.class),
    BATCH_IS_ASSIGNED("batchIsAssigned_KJUSYSCommon_Bool","Is Assigned", Boolean.class),

    END_DATE("endDate_KJUSYSCommon_Date", "End Date", Long.class),
    IS_ACTIVE("isActive_KJUSYSCommon_Bool","Is Active ",Boolean.class),

    // ADMIN DB UPDATE ACTIVITY LOGS
    ACTIVITY_TIMESTAMP("timestamp_KJUSYSCommon_DateTime", "Activity Timestamp", Long.class),
    ACTIVITY_PERFORMED_BY("performedBy_KJUSYSCommon_Text", "Activity Performed By", String.class),
    ACTIVITY_UPDATED_ENTITY("updatedEntity_KJUSYSCommon_Text", "Activity Updated Entity", String.class),
    ACTIVITY_UPDATED_FIELD("updatedField_KJUSYSCommon_Text", "Activity Updated Field", String.class),
    ACTIVITY_OLD_VALUE("oldValue_KJUSYSCommon_Text", "Updated Field Old Value", String.class),
    ACTIVITY_NEW_VALUE("newValue_KJUSYSCommon_Text", "Updated Field New Value", String.class),

    UPLOADED_FILE_PATH("uploadedFilePath_KJUSYSCommon_Text", "Uploaded File Path", String.class),
    SECTION_NAME("sectionName_KJUSYSCommon_Text", "Section Name", String.class),
    ADMISSION_QUOTA("admissionQuota_KJUSYSCommon_Text", "Admission Quota", String.class),

    SEMESTER("semester_KJUSYSCommon_Int", "Semester", Integer.class),

    SEAT_AVAILABILITY("seatAvailability_KJUSYSCommon_Int", "Seats", Integer.class),


    LIST_OF_APPLICANTS("listOfApplicants_KJUSYSCommon_DocumentArray","List Of Applicants",Document.class)
    ;

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private KJUSYSCommonKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private KJUSYSCommonKeysPBinder(String property, String label, Class<?> dataType) {
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

    // New method to get label by property name
    public static String getLabelByProperty(String propertyName) {
        for (KJUSYSCommonKeysPBinder binder : values()) {
            if (binder.property.equals(propertyName)) {
                return binder.label;
            }
        }
        return null;
    }

    // New method to get property name by label
    public static String getPropertyByLabel(String label) {
        for (KJUSYSCommonKeysPBinder binder : values()) {
            if (binder.label.equals(label)) {
                return binder.property;
            }
        }
        return null;
    }

    // $FF: synthetic method
    private static KJUSYSCommonKeysPBinder[] $values() {
        return KJUSYSCommonKeysPBinder.class.getEnumConstants();
    }
}