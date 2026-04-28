package in.edu.kristujayanti.propertyBinder.Eform;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

public enum EformCommonKeysPBinder implements KJUSYSPropertyBinder {

    APPLICANT_EMAIL("applicantEmail_EformCommon_Text", "Applicant Email", String.class),
    APPLICANT_NAME_AS_PER_MARKSHEET("applicantNameAsPerMarksheet_EformCommon_Text", "Applicant Name As Per Marksheet", String.class),
    CLEANED_APPLICANT_NAME_AS_PER_MARKSHEET("cleanedApplicantNameAsPerMarksheet_EformCommon_Text", "Cleaned Applicant Name As Per Marksheet", String.class),
    APPLICANT_GENDER("applicantGender_EformCommon_Text", "Applicant Gender", String.class),
    APPLICANT_WHATSAPP_NUMBER("applicantWhatsappNumber_EformCommon_Text", "Applicant Whatsapp Number", String.class),
    APPLICANT_DATE_OF_BIRTH("applicantDateOfBirth_EformCommon_Date", "Applicant Date Of Birth", Long.class),
    APPLICANT_PASSWORD("applicantPassword_EformCommon_Text", "Applicant Password", String.class, 6 , 20),
    APPLICANT_OLD_PASSWORD("applicantOldPassword_EformCommon_Text", "Applicant Old Password", String.class, 6, 20),
    APPLICANT_AUTH_OBJECTID("applicantAuthObjectid_EformCommon_ObjectId", "Applicant Auth Objectid", ObjectId.class),
    APPLICATION_NUMBER("applicationNumber_EformCommon_Text", "Application Number", String.class),
    APPLICATION_NUMBERS_ARRAY("applicationNumbersArray_EformCommon_TextArray", "Application Numbers", String.class),


    FORM_CONFIG_ID("formConfigId_EformCommon_Int", "Form Config Id", Integer.class),
    EFORM_BATCH_YEAR("eformBatchYear_EformCommon_Text", "Eform Batch Year", String.class),
    EFORM_BATCH_IDS("eformBatchIds_EformCommon_TextArray", "Eform Batch IDS", String.class),
    APPLICATION_FORM_ID("applicationFormId_EformCommon_Int", "Application Form ID", Integer.class),
    APPLICATION_FORM_NAME("applicationFormName_EformCommon_Text", "Application Form Name", String.class),
    APPLICATION_FORM_START_DATE("applicationFormStartDate_EformCommon_Date" , "Application Form Start Date", Long.class),
    APPLICATION_FORM_END_DATE("applicationFormEndDate_EformCommon_Date" , "Application Form End Date", Long.class),
    APPLICATION_FEES("applicationFees_EformCommon_Double", "Application Fees", Double.class),
    APPLICATION_ACTIVE("applicationActive_EformCommon_Bool", "Application Active", Boolean.class),

    EFORM_STATUS("eformStatus_EformCommon_Text", "Eform Status", String.class),
    EFORM_STATUS_SEARCH_ARRAY("eformStatusSearchArray_EformCommon_TextArray", "Eform Status Array", String.class),
    EFORM_REMARKS("eformRemarks_EformCommon_Text", "Eform Remarks", String.class),

    APPLICATION_FORM_SECTIONS("applicationFormSections_EformCommon_TextArray","application Form Sections",String.class),
    APPLICATION_FORM_FILE_UPLOAD_SECTIONS("applicationFormFileUploadSections_EformCommon_TextArray","application file upload sections",String.class),

    SECTION_IS_LOCKED("sectionIsLocked_EformCommon_Bool", "Section Is Locked", Boolean.class),
    SECTION_IS_DRAFT("sectionIsDraft_EformCommon_Bool", "Section Is Draft", Boolean.class),

    APPLICATION_SEQUENCE_PREFIX("applicationSequencePrefix_EformCommon_Text", "Application Sequence Prefix", String.class),
    APPLICATION_SEQUENCE_BASE_NUMBER("applicationSequenceBaseNumber_EformCommon_Int", "Application Sequence Base Number", Integer.class),
    LATEST_APPLICATION_SEQUENCE_NUMBER("latestApplicationSequenceNumber__EformCommon_Text", "Latest Application Sequence Number", String.class),
    IS_APPLICATION_FORM_OR_OPERATION("isApplicationFormOrOperation_EformCommon_Bool","Is Application Form OR Operation",Boolean.class),
    HAS_APPLICATION_FORM_ELIGIBILITY_SUBJECT("hasApplicationFormEligibilitySubject_EformCommon_Bool","Has Application Form Eligibility Subject",Boolean.class),
    APPLICATION_FORM_ELIGIBILITY_SUBJECTS("applicationFormEligibilitySubjects_EformCommon_TextArray","Application Form Eligibility Subject",String.class),
    APPLICATION_FORM_ELIGIBILITY_SUBJECT_NAME_ONE("applicationFormEligibilitySubjectNameOne_EformCommon_Text","Application Form  Eligibility Subject Name One",String.class),
    APPLICATION_FORM_ELIGIBILITY_SUBJECT_NAME_TWO("applicationFormEligibilitySubjectNameTwo_EformCommon_Text","Application Form  Eligibility Subject Name Two",String.class),
    FORM_SUBMITTED("formSubmitted_EformCommon_Bool", "Form Submitted", Boolean.class),

    APPLICANT_ALLOTTED_BATCH_CODE("applicantAllottedBatchCode_EformCommon_Text", "Applicant Alloted Batch Code", String.class),
    APPLICANT_ALLOTTED_BATCH_NAME("applicantAllottedBatchName_EformCommon_Text", "Applicant Allotted Batch Name", String.class),
    APPLICANT_ALLOTTED_PROGRAM_NAME("applicantAllottedProgramName_EformCommon_Text", "Allotted Program Name", String.class),
    APPLICANT_ALLOCATION_LIST("applicantAllocationList_EformCommon_DocumentArray", "AllocationList", Document.class),
    APPLICANT_FEE_DESCRIPTION("applicantFeeDescription_EformCommon_Text", "Applicant Fee Description", String.class),
    COUNTRY_CODE("countryCode_EformCommon_Text","Country Code", String.class);


    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private EformCommonKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private EformCommonKeysPBinder(String property, String label, Class<?> dataType) {
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
    private static EformCommonKeysPBinder[] $values() {
        return EformCommonKeysPBinder.class.getEnumConstants();
    }
}
