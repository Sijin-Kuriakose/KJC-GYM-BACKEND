package in.edu.kristujayanti.propertyBinder.HR;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import org.bson.Document;


public enum OnBoardingKeysPBinder implements KJUSYSPropertyBinder {

    EMPLOYEE_AGREE_INSTITUTIONAL_POLICY("employeeAgreeInstitutionalPolicy_EmployeeOnBoarding_Bool","Employee Accept Terms and condition Flag", Boolean.class),
//    EMPLOYEE_INSTITUTIONAL_POLICY("employeeInstitutionalPolicy_EmployeeOnBoarding_Bool","Employee Institutional Flag", Boolean.class),
    EMPLOYEE_DRESS_CODE_POLICY("employeeDressCodePolicy_EmployeeOnBoarding_Bool","Employee Dress Code Flag", Boolean.class),
    EMPLOYEE_LEAVE_POLICY("employeeLeavePolicy_EmployeeOnBoarding_Bool","Employee Leave Flag", Boolean.class),
    EMPLOYEE_ON_OFFICIAL_DUTY_POLICY("employeeOnOfficialDutyPolicy_EmployeeOnBoarding_Bool","Employee On Duty Flag", Boolean.class),

    EMPLOYEE_DECLARATION("employeeDeclaration_EmployeeOnBoarding_Bool", "Employee Declaration ", Boolean.class),

    //Other
    EMPLOYEE_BANK_NAME("employeeBankName_EmployeeOnBoarding_Text", "Employee Bank  Name", String.class),
    EMPLOYEE_BANK_BRANCH_NAME("employeeBankBranchName_EmployeeOnBoarding_Text", "Employee Bank Branch Name", String.class),
    EMPLOYEE_BANK_IFSC_CODE("employeeBankIfscCode_EmployeeOnBoarding_Text", "Employee Bank IFSC Code", String.class),
    EMPLOYEE_BANK_ACCOUNT_NUMBER("employeeBankAccountNumber_EmployeeOnBoarding_Text", "Employee Bank Account Number", String.class),
    EMPLOYEE_BANK_ACCOUNT_HOLDER_NAME("employeeBankAccountHolderName_EmployeeOnBoarding_Text", "Employee Bank Account Holder Name", String.class),

    //Official Bank Details
    EMPLOYEE_OFFICIAL_BANK_NAME("employeeOfficialBankName_EmployeeOnBoarding_Text", "Employee Bank Branch Name", String.class),
    EMPLOYEE_OFFICIAL_BANK_BRANCH_NAME("employeeOfficialBankBranchName_EmployeeOnBoarding_Text", "Employee Bank Branch Name", String.class),
    EMPLOYEE_OFFICIAL_BANK_IFSC_CODE("employeeOfficialBankIfscCode_EmployeeOnBoarding_Text", "Employee Bank IFSC Code", String.class),
    EMPLOYEE_OFFICIAL_BANK_ACCOUNT_NUMBER("employeeOfficialBankAccountNumber_EmployeeOnBoarding_Text", "Employee Bank Account Number", String.class),
    EMPLOYEE_OFFICIAL_BANK_ACCOUNT_HOLDER_NAME("employeeOfficialBankAccountHolderName_EmployeeOnBoarding_Text", "Employee Bank Account Holder Name", String.class),

    EMPLOYEE_PRESENT_ADDRESS_LINE_1("employeePresentAddressLine1_EmployeeOnBoarding_Text", "Employee Present Address Line 1", String.class),
    EMPLOYEE_PRESENT_ADDRESS_LINE_2("employeePresentAddressLine2_EmployeeOnBoarding_Text", "Employee Present Address Line 2", String.class),
    EMPLOYEE_PRESENT_STATE("employeePresentState_EmployeeOnBoarding_Text", "Employee Present State", String.class),
    EMPLOYEE_PRESENT_DISTRICT("employeePresentDistrict_EmployeeOnBoarding_Text", "Employee Present District", String.class),
    EMPLOYEE_PRESENT_PINCODE("employeePresentPincode_EmployeeOnBoarding_Text", "Employee Present Pincode", String.class),
    EMPLOYEE_PRESENT_COUNTRY("employeePresentCountry_EmployeeOnBoarding_Text", "Employee Present Country", String.class),

    EMPLOYEE_PERMANENT_ADDRESS_LINE_1("employeePermanentAddressLine1_EmployeeOnBoarding_Text", "Employee Permanent Address", String.class),
    EMPLOYEE_PERMANENT_ADDRESS_LINE_2("employeePermanentAddressLine2_EmployeeOnBoarding_Text", "Employee Permanent Address", String.class),
    EMPLOYEE_PERMANENT_STATE("employeePermanentState_EmployeeOnBoarding_Text", "Employee Permanent State", String.class),
    EMPLOYEE_PERMANENT_DISTRICT("employeePermanentDistrict_EmployeeOnBoarding_Text", "Employee Permanent District", String.class),
    EMPLOYEE_PERMANENT_PINCODE("employeePermanentPincode_EmployeeOnBoarding_Text", "Employee Permanent Pincode", String.class),
    EMPLOYEE_PERMANENT_COUNTRY("employeePermanentCountry_EmployeeOnBoarding_Text", "Employee Permanent Country", String.class),

     //Academic Qualification details
    //10th
    EMPLOYEE_HAS_TENTH("employeeHasTenth_EmployeeOnBoarding_Bool","Employee Has Tenth", Boolean.class),
    EMPLOYEE_TENTH_MONTH_OF_PASSING("employeeTenthMonthOfPassing_EmployeeOnBoarding_Text","Employee 10th Month Of Passing", String.class),
    EMPLOYEE_TENTH_YEAR_OF_PASSING("employeeTenthYearOfPassing_EmployeeOnBoarding_Int","Employee 10th Year Of Passing", Integer.class),
    EMPLOYEE_TENTH_BOARD_UNIVERSITY("employeeTenthBoardUniversity_EmployeeOnBoarding_Text","Employee 10th Board Or University", String.class),
    EMPLOYEE_TENTH_REGISTRATION_NUMBER("employeeTenthRegistrationNumber_EmployeeOnBoarding_Text","Employee 10th Registration Number", String.class),
    EMPLOYEE_TENTH_CERTIFICATE("employeeTenthCertificate_EmployeeOnBoarding_File","Employee 10th Certificate", String.class),

    //12th
    EMPLOYEE_HAS_TWELFTH("employeeHasTwelfth_EmployeeOnBoarding_Bool","Employee Has Twelfth", Boolean.class),
    EMPLOYEE_TWELFTH_BOARD_UNIVERSITY("employeeTwelfthBoardUniversity_EmployeeOnBoarding_Text","Employee 12th Board Or University", String.class),
    EMPLOYEE_TWELFTH_REGISTRATION_NUMBER("employeeTwelfthRegistrationNumber_EmployeeOnBoarding_Text", "Employee 12th Registration Number", String.class),
    EMPLOYEE_TWELFTH_MONTH_OF_PASSING("employeeTwelfthMonthOfPassing_EmployeeOnBoarding_Text", "Employee 12th Month Of Passing", String.class),
    EMPLOYEE_TWELFTH_YEAR_OF_PASSING("employeeTwelfthYearOfPassing_EmployeeOnBoarding_Int", "Employee 12th Year Of Passing", Integer.class),
    EMPLOYEE_TWELFTH_CERTIFICATE("employeeTwelfthCertificate_EmployeeOnBoarding_File", "Employee 12th Certificate", String.class),

    //UG
    EMPLOYEE_HAS_UG("employeeHasUg_EmployeeOnBoarding_Bool","Employee Has UG", Boolean.class),
    EMPLOYEE_UNDER_GRADUATE_BOARD_UNIVERSITY("employeeUnderGraduateBoardUniversity_EmployeeOnBoarding_Text","Employee UnderGraduate Board Or University", String.class),
    EMPLOYEE_UNDER_GRADUATE_REGISTRATION_NUMBER("employeeUnderGraduateRegistrationNumber_EmployeeOnBoarding_Text", "Employee UnderGraduate Registration Number", String.class),
    EMPLOYEE_UNDERGRADUATE_QUALIFICATION("employeeUndergraduateQualification_EmployeeOnBoarding_Text","Employee UG Qualification", String.class),
    EMPLOYEE_UNDERGRADUATE_MONTH_OF_PASSING("employeeUndergraduateMonthOfPassing_EmployeeOnBoarding_Text", "Employee Undergraduate Month Of Passing", String.class),
    EMPLOYEE_UNDERGRADUATE_YEAR_OF_PASSING("employeeUndergraduateYearOfPassing_EmployeeOnBoarding_Int", "Employee Undergraduate Year Of Passing", Integer.class),
    EMPLOYEE_UNDERGRADUATE_CERTIFICATE("employeeUndergraduateCertificate_EmployeeOnBoarding_File", "Employee Undergraduate Certificate", String.class),

    //Pg
    EMPLOYEE_HAS_PG("employeeHasPg_EmployeeOnBoarding_Bool","Employee Has PG", Boolean.class),
    EMPLOYEE_POSTGRADUATE_BOARD_UNIVERSITY("employeePostgraduateBoardUniversity_EmployeeOnBoarding_Text","Employee Postgraduate Board Or University", String.class),
    EMPLOYEE_POSTGRADUATE_REGISTRATION_NUMBER("employeePostgraduateRegistrationNumber_EmployeeOnBoarding_Text", "Employee Postgraduate Registration Number", String.class),
    EMPLOYEE_POSTGRADUATE_QUALIFICATION("employeePostgraduateQualification_EmployeeOnBoarding_Text", "Employee PG Qualification", String.class),
    EMPLOYEE_POSTGRADUATE_MONTH_OF_PASSING("employeePostgraduateMonthOfPassing_EmployeeOnBoarding_Text", "Employee Postgraduate Month Of Passing", String.class),
    EMPLOYEE_POSTGRADUATE_YEAR_OF_PASSING("employeePostgraduateYearOfPassing_EmployeeOnBoarding_Int", "Employee Postgraduate Year Of Passing", Integer.class),
    EMPLOYEE_POSTGRADUATE_CERTIFICATE("employeePostgraduateCertificate_EmployeeOnBoarding_File", "Employee Postgraduate Certificate", String.class),

    //NET
    EMPLOYEE_IS_NET_EXAM_QUALIFIED("employeeIsNetExamQualified_EmployeeOnBoarding_Bool","Employee NET Exam Qualified", Boolean.class),
    EMPLOYEE_IS_NET_EXAM_BOARD_UNIVERSITY("employeeIsNetExamBoardUniversity_EmployeeOnBoarding_Text","Employee NET Exam Board Or University", String.class),
    EMPLOYEE_IS_NET_EXAM_REGISTRATION_NUMBER("employeeIsNetExamRegistrationNumber_EmployeeOnBoarding_Text", "Employee NET Exam Registration Number", String.class),
    EMPLOYEE_IS_JRF_QUALIFIED("employeeIsJrfQualified_EmployeeOnBoarding_Bool","Employee  is JRF Qualified", Boolean.class),
    EMPLOYEE_IS_JRF_CATEGORY("employeeIsJrfCategory_EmployeeOnBoarding_Text","Employee JRF Category", Boolean.class),
    EMPLOYEE_NET_EXAM_CLEARED_MONTH("employeeNetExamClearedMonth_EmployeeOnBoarding_Text","Employee NET Exam Qualified Month", String.class),
    EMPLOYEE_NET_EXAM_CLEARED_YEAR("employeeNetExamClearedYear_EmployeeOnBoarding_Int","Employee NET Exam Qualified Year", Integer.class),
    EMPLOYEE_NET_QUALIFICATION_CERTIFICATE("employeeNetQualificationCertificate_EmployeeOnBoarding_File","Employee NET Certificate", String.class),
    EMPLOYEE_JRF_CERTIFICATE("employeeJrfCertificate_EmployeeOnBoarding_File","Employee JRF Certificate", String.class),

    //SET
    EMPLOYEE_IS_SET_EXAM_QUALIFIED("employeeIsSetExamQualified_EmployeeOnBoarding_Bool", "Employee SET Exam Qualified", Boolean.class),
    EMPLOYEE_SET_DETAILS("employeeSetDetails_EmployeeOnBoarding_DocumentArray", "Employee SET Document array",  Document.class),
    EMPLOYEE_SET_EXAM_CLEARED_MONTH_1("employeeSetExamClearedMonth1_EmployeeOnBoarding_Text", "Employee SET Exam Qualified Month", String.class),
    EMPLOYEE_SET_EXAM_CLEARED_YEAR("employeeSetExamClearedYear_EmployeeOnBoarding_Int", "Employee SET Exam Qualified Year", Integer.class),
    EMPLOYEE_SET_EXAM_CLEARED_STATE_1("employeeSetExamClearedState1_EmployeeOnBoarding_Text", "Employee SET Exam Cleared State", Integer.class),
    EMPLOYEE_SET_QUALIFICATION_CERTIFICATE_1("employeeSetQualificationCertificate1_EmployeeOnBoarding_File", "Employee SET Certificate", String.class),


    EMPLOYEE_SET_EXAM_CLEARED_MONTH_2("employeeSetExamClearedMonth2_EmployeeOnBoarding_Text", "Employee SET Exam Qualified Month", String.class),
    EMPLOYEE_SET_EXAM_CLEARED_STATE_2("employeeSetExamClearedState2_EmployeeOnBoarding_Text", "Employee SET Exam Cleared State", Integer.class),
    EMPLOYEE_SET_QUALIFICATION_CERTIFICATE_2("employeeSetQualificationCertificate2_EmployeeOnBoarding_File", "Employee SET Certificate", String.class),

    EMPLOYEE_SET_EXAM_CLEARED_MONTH_3("employeeSetExamClearedMonth3_EmployeeOnBoarding_Text", "Employee SET Exam Qualified Month", String.class),
    EMPLOYEE_SET_EXAM_CLEARED_STATE_3("employeeSetExamClearedState3_EmployeeOnBoarding_Text", "Employee SET Exam Cleared State", Integer.class),
    EMPLOYEE_SET_QUALIFICATION_CERTIFICATE_3("employeeSetQualificationCertificate3_EmployeeOnBoarding_File", "Employee SET Certificate", String.class),

    //MPhil
    EMPLOYEE_IS_MPHIL_QUALIFIED("employeeIsMphilQualified_EmployeeOnBoarding_Bool", "Employee MPhil Qualified", Boolean.class),
    EMPLOYEE_MPHIL_CLEARED_MONTH("employeeMphilClearedMonth_EmployeeOnBoarding_Text", "Employee MPhil Qualified Month", String.class),
    EMPLOYEE_MPHIL_CLEARED_YEAR("employeeMphilClearedYear_EmployeeOnBoarding_Int", "Employee MPhil Qualified Year", Integer.class),
    EMPLOYEE_MPHIL_QUALIFICATION_CERTIFICATE("employeeMphilQualificationCertificate_EmployeeOnBoarding_File", "Employee MPhil Certificate", String.class),
    EMPLOYEE_MPHIL_BOARD_UNIVERSITY("employeeMphilBoardUniversity_EmployeeOnBoarding_Text","Employee MPhil Board Or University", String.class),
    EMPLOYEE_MPHIL_REGISTRATION_NUMBER("employeeMphilRegistrationNumber_EmployeeOnBoarding_Text", "Employee MPhil Registration Number", String.class),


    //PHD
    EMPLOYEE_PHD_STATUS("employeePhdStatus_EmployeeOnBoarding_Text","Employee PHD Status", String.class),
    EMPLOYEE_PHD_MODE("employeePhdMode_EmployeeOnBoarding_Text","Employee PHD Mode", String.class),
    EMPLOYEE_PHD_ENROLLED_MONTH("employeePhdEnrolledMonth_EmployeeOnBoarding_Text", "Employee PhD Enrolled Month", String.class),
    EMPLOYEE_PHD_ENROLLED_YEAR("employeePhdEnrolledYear_EmployeeOnBoarding_Int", "Employee PhD Enrolled Year", Integer.class),
    EMPLOYEE_PHD_ENROLLMENT_CERTIFICATE("employeePhdEnrollmentCertificate_EmployeeOnBoarding_File", "Employee PhD Enrollment Certificate", String.class),
    EMPLOYEE_PHD_EXAM_BOARD_UNIVERSITY("employeePhdExamBoardUniversity_EmployeeOnBoarding_Text","Employee PhD Exam Board Or University", String.class),
    EMPLOYEE_PHD_EXAM_REGISTRATION_NUMBER("employeePhdExamRegistrationNumber_EmployeeOnBoarding_Text", "Employee PhD Exam Registration Number", String.class),

    EMPLOYEE_PHD_COMPLETED_MONTH("employeePhdCompletedMonth_EmployeeOnBoarding_Text", "Employee PhD Completed Month", String.class),
    EMPLOYEE_PHD_COMPLETED_YEAR("employeePhdCompletedYear_EmployeeOnBoarding_Int", "Employee PhD Completed Year", Integer.class),
    EMPLOYEE_PHD_COMPLETION_CERTIFICATE("employeePhdCompletionCertificate_EmployeeOnBoarding_File", "Employee PhD Completion Certificate", String.class),

//    // post doc
//    EMPLOYEE_POST_DOCTORATE_STATUS("employeePostDoctorateStatus_EmployeeOnBoarding_Bool", "Employee Post Doctorate Status", Boolean.class),
//    EMPLOYEE_POST_DOCTORATE_ENROLLED_MONTH("employeePostDoctorateEnrolledMonth_EmployeeOnBoarding_Text", "Employee Post Doctorate Enrolled Month", String.class),
//    EMPLOYEE_POST_DOCTORATE_ENROLLED_YEAR("employeePostDoctorateEnrolledYear_EmployeeOnBoarding_Int", "Employee Post Doctorate Enrolled Year", Integer.class),
//    EMPLOYEE_POST_DOCTORATE_ENROLLMENT_CERTIFICATE("employeePostDoctorateEnrollmentCertificate_EmployeeOnBoarding_File", "Employee Post Doctorate Enrollment Certificate", String.class),
//
//    EMPLOYEE_POST_DOCTORATE_COMPLETED_MONTH("employeePostDoctorateCompletedMonth_EmployeeOnBoarding_Text", "Employee Post Doctorate Completed Month", String.class),
//    EMPLOYEE_POST_DOCTORATE_COMPLETED_YEAR("employeePostDoctorateCompletedYear_EmployeeOnBoarding_Int", "Employee Post Doctorate Completed Year", Integer.class),
//    EMPLOYEE_POST_DOCTORATE_COMPLETION_CERTIFICATE("employeePostDoctorateCompletionCertificate_EmployeeOnBoarding_File", "Employee Post Doctorate Completion Certificate", String.class),

    //Experience
    //Teaching
    EMPLOYEE_TEACHING_EXPERIENCE("employeeTeachingExperience_EmployeeOnBoarding_DocumentArray", "Employee Teaching Experience", Document.class ),
    EMPLOYEE_HAS_TEACHING_EXPERIENCE("employeeHasTeachingExperience_EmployeeOnBoarding_Bool","Employee Has Teaching experience", Boolean.class),
    EMPLOYEE_TEACHING_JOINING_DATE("employeeTeachingJoiningDate_EmployeeOnBoarding_Date", "Employee Joining Date in Teaching", Long.class),
    EMPLOYEE_TEACHING_EXPERIENCE_JOB_PROFILE("employeeTeachingExperienceJobProfile_EmployeeOnBoarding_Text", "Employee Teaching Experience Job Profile", String.class),
    EMPLOYEE_TEACHING_EXPERIENCE_INSTITUTION_NAME("employeeTeachingExperienceInstitutionName_EmployeeOnBoarding_Text", "Employee Teaching Experience Institution Name", String.class),
    EMPLOYEE_TEACHING_EXPERIENCE_FROM_DATE("employeeTeachingExperienceFromDate_EmployeeOnBoarding_Date", "Employee Teaching Experience From Date", Long.class),
    EMPLOYEE_TEACHING_EXPERIENCE_TO_DATE("employeeTeachingExperienceToDate_EmployeeOnBoarding_Date", "Employee Teaching Experience To Date", Long.class),


    //Industry experience
    EMPLOYEE_INDUSTRY_EXPERIENCE("employeeIndustryExperience_EmployeeOnBoarding_DocumentArray", "Employee Industry Experience", Document.class),
    EMPLOYEE_HAS_INDUSTRY_EXPERIENCE("employeeHasIndustryExperience_EmployeeOnBoarding_Bool","Employee Has Industry experience", Boolean.class),
    EMPLOYEE_INDUSTRY_EXPERIENCE_JOB_PROFILE("employeeIndustryExperienceJobProfile_EmployeeOnBoarding_Text", "Employee Industry Experience Job Profile", String.class),
    EMPLOYEE_INDUSTRY_EXPERIENCE_COMPANY_NAME("employeeIndustryExperienceCompanyName_EmployeeOnBoarding_Text", "Employee Industry Experience Company Name", String.class),
    EMPLOYEE_INDUSTRY_EXPERIENCE_FROM_DATE("employeeIndustryExperienceFromDate_EmployeeOnBoarding_Date", "Employee Industry Experience From Date", Long.class),
    EMPLOYEE_INDUSTRY_EXPERIENCE_TO_DATE("employeeIndustryExperienceToDate_EmployeeOnBoarding_Date", "Employee Industry Experience To Date", Long.class),


    //Research Experience
    EMPLOYEE_RESEARCH_EXPERIENCE("employeeResearchExperience_EmployeeOnBoarding_DocumentArray", "Employee Research Experience", Document.class),
    EMPLOYEE_HAS_RESEARCH_EXPERIENCE("employeeHasResearchExperience_EmployeeOnBoarding_Bool", "Employee Has Research Experience", Boolean.class),
    EMPLOYEE_RESEARCH_EXPERIENCE_PROFILE("employeeResearchExperienceProfile_EmployeeOnBoarding_Text", "Employee Research Experience Profile", String.class),
    EMPLOYEE_RESEARCH_EXPERIENCE_INSTITUTION_NAME("employeeResearchExperienceInstitutionName_EmployeeOnBoarding_Text", "Employee Research Experience Institution Name", String.class),
    EMPLOYEE_RESEARCH_EXPERIENCE_FROM_DATE("employeeResearchExperienceFromDate_EmployeeOnBoarding_Date", "Employee Research Experience From Date", Long.class),
    EMPLOYEE_RESEARCH_EXPERIENCE_TO_DATE("employeeResearchExperienceToDate_EmployeeOnBoarding_Date", "Employee Research Experience To Date", Long.class),


    //PHD experience
    EMPLOYEE_PHD_EXPERIENCE("employeePhdExperience_EmployeeOnBoarding_DocumentArray", "Employee Phd Experience", Document.class),
    EMPLOYEE_HAS_FULL_TIME_PHD("employeeHasFullTimePhd_EmployeeOnBoarding_Bool","Employee Has Research experience", Boolean.class),
    EMPLOYEE_FULL_TIME_PHD_INSTITUTION_NAME("employeeFullTimePhdInstitutionName_EmployeeOnBoarding_Text", "Employee Full-Time PhD Institution Name", String.class),
    EMPLOYEE_FULL_TIME_PHD_FROM_DATE("employeeFullTimePhdFromDate_EmployeeOnBoarding_Date", "Employee Full-Time PhD From Date", Long.class),
    EMPLOYEE_FULL_TIME_PHD_TO_DATE("employeeFullTimePhdToDate_EmployeeOnBoarding_Date", "Employee Full-Time PhD To Date", Long.class),

    //Post Doc Experience
    //PHD experience
    EMPLOYEE_POSTDOC_EXPERIENCE("employeePostdocExperience_EmployeeOnBoarding_DocumentArray", "Employee Post Doc Experience", Document.class),
    EMPLOYEE_HAS_POSTDOC("employeeHasPostdoc_EmployeeOnBoarding_Bool","Employee Has Post Doc", Boolean.class),
    EMPLOYEE_POSTDOC_INSTITUTION_NAME("employeePostdocInstitutionName_EmployeeOnBoarding_Text", "Employee PostDoc Institution Name", String.class),
    EMPLOYEE_POSTDOC_FROM_DATE("employeePostdocFromDate_EmployeeOnBoarding_Date", "Employee Post Doc From Date", Long.class),
    EMPLOYEE_POSTDOC_TO_DATE("employeePostdocToDate_EmployeeOnBoarding_Date", "Employee Post Doc To Date", Long.class),
    ;


    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private OnBoardingKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength= maxLength;
    }

    private OnBoardingKeysPBinder(String property, String label, Class<?> dataType) {
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
    private static OnBoardingKeysPBinder[] $values() {
        return OnBoardingKeysPBinder.class.getEnumConstants();
    }

}
