package in.edu.kristujayanti.propertyBinder.Eform.Applicant;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import org.bson.types.ObjectId;
import org.w3c.dom.Document;

public enum ApplicantFormsKeysPBinder implements KJUSYSPropertyBinder {

//    APPLICANT_NAME_AS_PER_MARKSHEET("applicantNameAsPerMarksheet_ApplicantForms_Text","Applicant Name", String.class),
    APPLICANT_RELIGION("applicantReligion_ApplicantForms_Text","Applicant Religion", String.class),
//    APPLICANT_DATE_OF_BIRTH("applicantDateOfBirth_ApplicantForms_Date", "Applicant Date Of Birth", Long.class),
//    FORM_ID("formId_ApplicantForms_Text","formID",String.class),
//    APPLICANT_WHATSAPP_NUMBER("applicantWhatsappNumber_ApplicantForms_Text", "Applicant Whatsapp Number", String.class),
    LAST_UPDATED("lastUpdated_ApplicantForms_DateTime","last Updated By Applicant",Long.class),
    LAST_UPDATED_ADMIN("lastUpdatedAdmin_ApplicantForms_DateTime", "Last Updated By Scrutiny", Long.class),
    APPLICANT_NATIONALITY("applicantNationality_ApplicantForms_Text","Applicant Nationality",String.class),
    APPLICANT_CASTE_CATEGORY("applicantCasteCategory_ApplicantForms_Text","Applicant Caste Category",String.class),
    ALTERNATE_PHONE_NUMBER("alternatePhoneNumber_ApplicantForms_Text","Applicant Alternate Phone Number",String.class),
//    APPLICANT_BIRTH_PLACE("applicantBirthPlace_ApplicantForms_Text","Applicant Birth Place",String.class),
    APPLICANT_BLOOD_GROUP("applicantBloodGroup_ApplicantForms_Text","Applicant Blood Group",String.class),
//    APPLICANT_GENDER("applicantGender_ApplicantForms_Text","Applicant Gender",String.class),
    APPLICANT_KNOWN_LANGUAGES("applicantKnownLanguages_ApplicantForms_Text","Applicant Known Languages",String.class),
    APPLICANT_MARITAL_STATUS("applicantMaritalStatus_ApplicantForms_Text","Applicant Marital Status", String.class),
    APPLICANT_MOTHER_TONGUE("applicantMotherTongue_ApplicantForms_Text","Applicant Mother Tongue",String.class),
    APPLICANT_SUB_CASTE_CATEGORY("applicantSubCasteCategory_ApplicantForms_Text","Applicant Sub Caste Category",String.class),
//    APPLICANT_PERMANENT_NATIONALITY("applicantPermanentNationality_ApplicantForms_Text","Applicant Permanent Nationality",String.class),
    APPLICANT_PHYSICALLY_CHALLENGED("applicantPhysicallyChallenged_ApplicantForms_Bool","Physically Challenged",Boolean.class),
    APPLICANT_PHYSICALLY_CHALLENGED_DESCRIPTION("applicantPhysicallyChallengedDescription_ApplicantForms_File","Physically Challenged Description",String.class),
    APPLICANT_PHOTO("applicantPhoto_ApplicantForms_File", "Applicant Photo", String.class),
    APPLICANT_ADHAAR_NUMBER("applicantAdhaarNumber_ApplicantForms_Long", "Applicant Adhaar Number", Long.class),
    APPLICANT_PASSPORT_NUMBER("applicantPassportNumber_ApplicantForms_Text", "Applicant Passport Number", String.class),
    APPLICANT_BIRTH_COUNTRY("applicantBirthCountry_ApplicantForms_Text", "Applicant Birth Country", String.class),
    APPLICANT_BIRTH_STATE("applicantBirthState_ApplicantForms_Text", "Applicant Birth State", String.class),
    APPLICANT_BIRTH_DISTRICT("applicantBirthDistrict_ApplicantForms_Text", "Applicant Birth District", String.class),




    APPLICANT_PERMANENT_ADDRESS("applicantPermanentAddress_ApplicantForms_Text","Applicant Permanent Address", String.class),
    APPLICANT_PERMANENT_DISTRICT("applicantPermanentDistrict_ApplicantForms_Text","Applicant Permanent District",String.class),
    APPLICANT_PERMANENT_PIN_CODE("applicantPermanentPinCode_ApplicantForms_Int","Applicant Permanent Pin code",Integer.class),
    APPLICANT_PERMANENT_STATE("applicantPermanentState_ApplicantForms_Text","Applicant Permanent State",String.class),
    APPLICANT_PRESENT_ADDRESS("applicantPresentAddress_ApplicantForms_Text","Applicant Present Address",String.class),
    APPLICANT_PRESENT_COUNTRY("applicantPresentCountry_ApplicantForms_Text","Applicant Present Country",String.class),
    APPLICANT_PERMANENT_COUNTRY("applicantPermanentCountry_ApplicantForms_Text","Applicant Permanent Country",String.class),
    APPLICANT_PRESENT_DISTRICT("applicantPresentDistrict_ApplicantForms_Text","Applicant Present District",String.class),
    APPLICANT_PRESENT_PIN_CODE("applicantPresentPinCode_ApplicantForms_Int","Applicant Present Pin code",Integer.class),
    APPLICANT_PRESENT_STATE("applicantPresentState_ApplicantForms_Text","Applicant Present State",String.class),
    APPLICANT_ADDRESS_FLAG("applicantAddressFlag_ApplicantForms_Bool","Address Flag",Boolean.class),


    FATHER_NAME("fatherName_ApplicantForms_Text","Father Name",String.class),
    FATHER_OCCUPATION("fatherOccupation_ApplicantForms_Text","Father Occupation",String.class),
    FATHER_EDUCATION("fatherEducation_ApplicantForms_Text","Education",String.class),
    FATHER_ANNUAL_INCOME("fatherAnnualIncome_ApplicantForms_Text","Father Annual Income",String.class),
    FATHER_EMPLOYER_DETAIL("fatherEmployerDetail_ApplicantForms_Text","Father's Employer", String.class),
    FATHER_AGE("fatherAge_ApplicantForms_Int","Father Age",Integer.class),
    FATHER_CASTE("fatherCaste_ApplicantForms_Text","Caste Category",String.class),
    FATHER_EMAIL("fatherEmail_ApplicantForms_Text","Email",String.class),
    FATHER_MOBILE_NUMBER("fatherMobileNumber_ApplicantForms_Text","Mobile No",String.class),
    FATHER_PAN_NUMBER("fatherPanNumber_ApplicantForms_Text","Father PAN Number",String.class),


    GUARDIAN_AGE("guardianAge_ApplicantForms_Int","Age",Integer.class),
    GUARDIAN_CASTE("guardianCaste_ApplicantForms_Text","Guardian Caste Category",String.class),
    GUARDIAN_EDUCATION_DETAILS("guardianEducationDetails_ApplicantForms_Text","Guardian Education Detail",String.class),
    GUARDIAN_EMPLOYER_DETAIL("guardianEmployerDetail_ApplicantForms_Text","Guardian's Employer", String.class),
    GUARDIAN_MOBILE_NUMBER("guardianMobileNumber_ApplicantForms_Text","Gaurdian Mobile Number",String.class),
    GUARDIAN_NAME("guardianName_ApplicantForms_Text","Guardian Name",String.class),
    GUARDIAN_OCCUPATION("guardianOccupation_ApplicantForms_Text","Guardian Occupation",String.class),
    GUARDIAN_PAN_NUMBER("guardianPanNumber_ApplicantForms_Text","Guardian PAN Number",String.class),
    GUARDIAN_ANNUAL_INCOME("guardianAnnualIncome_ApplicantForms_Text","Guardian Annual Income", String.class),
    GUARDIAN_EMAIL("guardianEmail_ApplicantForms_Text","Guardian Email",String.class),
    GUARDIAN_RELATIONSHIP_DETAIL("guardianRelationshipDetail_ApplicantForms_Text","Guardian Relationship Detail",String.class),


    MOTHER_NAME("motherName_ApplicantForms_Text","Mother Name",String.class),
    MOTHER_AGE("motherAge_ApplicantForms_Int","Mother Age", Integer.class),
    MOTHER_ANNUAL_INCOME("motherAnnualIncome_ApplicantForms_Text","Mother Annual Income", String.class),
    MOTHER_CASTE("motherCaste_ApplicantForms_Text","Mother Caste", String.class),
    MOTHER_EDUCATION_DETAILS("motherEducationDetails_ApplicantForms_Text","Mother Education Details", String.class),
    MOTHER_EMAIL("motherEmail_ApplicantForms_Text","Mother Email",String.class),
    MOTHER_EMPLOYER_DETAIL("motherEmployerDetail_ApplicantForms_Text","Mother Employer", String.class),
    MOTHER_MOBILE_NUMBER("motherMobileNumber_ApplicantForms_Text","Mother Mobile No", String.class),
    MOTHER_OCCUPATION("motherOccupation_ApplicantForms_Text","Mother Occupation", String.class),
    MOTHER_PAN_NUMBER("motherPanNumber_ApplicantForms_Text","Mother PAN No", String.class),


    APPLICANT_MOBILE_NUMBER_COUNTRY_CODE("applicantMobileNumberCountryCode_ApplicantForms_Text","Applicant Country Code", String.class),
    APPLICANT_ALT_MOBILE_NUMBER_COUNTRY_CODE("applicantAltMobileNumberCountryCode_ApplicantForms_Text","Applicant Alternate Country Code", String.class),
    FATHER_MOBILE_NUMBER_COUNTRY_CODE("fatherMobileNumberCountryCode_ApplicantForms_Text","Father's Country Code",String.class),
    MOTHER_MOBILE_NUMBER_COUNTRY_CODE("motherMobileNumberCountryCode_ApplicantForms_Text","Mother's Country Code", String.class),
    GUARDIAN_MOBILE_NUMBER_COUNTRY_CODE("guardianMobileNumberCountryCode_ApplicantForms_Text","Guardian's Country Code", String.class),

    FATHER_SALUTATION("fatherSalutation_ApplicantForms_Text","Father Salutation", String.class),
    GUARDIAN_SALUTATION("guardianSalutation_ApplicantForms_Text","Guardian Salutation", String.class),
    MOTHER_SALUTATION("motherSalutation_ApplicantForms_Text","Mother Salutation", String.class),

    TENTH_VALUATION_SCHEME("tenthValuationScheme_ApplicantForms_Bool","Tenth Valuation Scheme", Boolean.class),
    TENTH_MARKS_SCORED("tenthMarksScored_ApplicantForms_Double","10th Marks Scored",Double.class),
    TENTH_MAXIMUM_MARKS("tenthMaximumMarks_ApplicantForms_Double","10th Max Marks Scored",Double.class),
    TENTH_MARKS_PERCENTAGE("tenthMarksPercentage_ApplicantForms_Percentage","10th Marks Percentage",Double.class),
    TENTH_BOARD("tenthBoard_ApplicantForms_Text","Tenth Board",String.class),
    TENTH_INSTITUTION("tenthInstitution_ApplicantForms_Text","10th Institution", String.class),
    TENTH_YEAR_OF_PASSING("tenthYearOfPassing_ApplicantForms_Int","10 th Year of Passing",Integer.class),
    TENTH_COUNTRY_OF_STUDYING("tenthCountryOfStudying_ApplicantForms_Text","10th Country of Studying", String.class),
    TENTH_STATE_OF_STUDYING("tenthStateOfStudying_ApplicantForms_Text","10th State of Studying", String.class),
    TENTH_DISTRICT_OF_STUDYING("tenthDistrictOfStudying_ApplicantForms_Text","10th District of Studying", String.class),
    TENTH_REGISTRATION_NUMBER("tenthRegistrationNumber_ApplicantForms_Text","10th RegistrationNumber", String.class),
    TENTH_GRADE_SCORED("tenthGradeScored_ApplicantForms_Text","10th Grade Scored", String.class),
    TENTH_GRADE_PERCENTAGE("tenthGradePercentage_ApplicantForms_Percentage","10th Grade Percentage", Double.class),
    TENTH_PDF("tenthPdf_ApplicantForms_File","10th PDF", String.class),
    TENTH_BOARD_SPECIFY("tenthBoardSpecify_ApplicantForms_Text","10th Board Specify", String.class),


    // 11 th details
    ELEVENTH_VALUATION_SCHEME("eleventhValuationScheme_ApplicantForms_Bool","Eleventh Valuation Scheme", Boolean.class),
    ELEVENTH_MARKS_ARRAY("eleventhMarksArray_ApplicantForms_DocumentArray","11th Marks Array",Document.class),
    ELEVENTH_GRADE_ARRAY("eleventhGradeArray_ApplicantForms_DocumentArray","11th Grade Array",Document.class),
    //    ELEVENTH_MARKS_SCORED("eleventhMarksScored_ApplicantForms_Int","11th Marks Scored",Integer.class),
//    ELEVENTH_MAXIMUM_MARKS("eleventhMaximumMarks_ApplicantForms_Int", "11th Maximum Marks", Integer.class),
//    ELEVENTH_MARKS_PERCENTAGE("eleventhMarksPercentage_Percentage", "11th Marks Percentage",Double.class),
//  ELEVENTH_BOARD("eleventhBoard_ApplicantForms_Text", "Eleventh Board", String.class),
    ELEVENTH_INSTITUTION("eleventhInstitution_ApplicantForms_Text", "11th Institution", String.class),

    ELEVENTH_YEAR_OF_PASSING("eleventhYearOfPassing_ApplicantForms_Int", "11th Year of Passing", Integer.class),
    ELEVENTH_COUNTRY_OF_STUDYING("eleventhCountryOfStudying_ApplicantForms_Text", "11th Country of Studying", String.class),
    ELEVENTH_STATE_OF_STUDYING("eleventhStateOfStudying_ApplicantForms_Text", "11th State of Studying", String.class),
    ELEVENTH_DISTRICT_OF_STUDYING("eleventhDistrictOfStudying_ApplicantForms_Text", "11th District of Studying", String.class),
    //    ELEVENTH_GRADE_SCORED("eleventhGradeScored_ApplicantForms_Text", "11th Grade Scored", String.class),
//    ELEVENTH_GRADE_PERCENTAGE("eleventhGradePercentage_ApplicantForms_Percentage", "11th Grade Percentage", Double.class),
    ELEVENTH_PDF("eleventhPdf_ApplicantForms_File", "11th PDF", String.class),

    ELEVENTH_QUALIFYING_EXAM("eleventhQualifyingExam_ApplicantForms_Text", "11th Qualifying Exam", String.class),
    ELEVENTH_BOARD_UNIVERSITY("eleventhBoardUniversity_ApplicantForms_Text", "11th Board University", String.class),
    ELEVENTH_REGISTER_NUMBER("eleventhRegisterNumber_ApplicantForms_Text", "11th Register Number", String.class),
    ELEVENTH_SPECIFY_QUALIFYING_EXAM("eleventhSpecifyQualifyingExam_ApplicantForms_Text", "11th Specify Qualifying Exam", String.class),
    ELEVENTH_SPECIFY_BOARD_UNIVERSITY("eleventhSpecifyBoardUniversity_ApplicantForms_Text", "11th Specify Board University", String.class),
    // awaiting status 11 th marks


    ELEVENTH_SUBJECT_1("eleventhSubject1_ApplicantForms_Text", "11th Subject 1", String.class),
    ELEVENTH_MARKS_SCORED_1("eleventhMarksScored1_ApplicantForms_Double", "11th Marks Scored 1", Double.class),
    ELEVENTH_MAX_MARKS_1("eleventhMaxMarks1_ApplicantForms_Double", "11th Max Marks 1", Double.class),
    ELEVENTH_GRADE_1("eleventhGrade1_ApplicantForms_Text", "11th Grade 1", String.class),
    ELEVENTH_GRADE_1_EQUIVALENT_PERCENTAGE("eleventhGrade1EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 1 percentage", Double.class),


    //    11th total including languages
    ELEVENTH_TOTAL_OBTAINED_MARKS_INCLUDING_LANGUAGE("eleventhTotalObtainedMarksIncludingLanguage_ApplicantForms_Double","11th Total Obtained Marks Including Language", Double.class),
    ELEVENTH_TOTAL_MAX_MARKS_INCLUDING_LANGUAGE("eleventhTotalMaxMarksIncludingLanguage_ApplicantForms_Double","11th Total  Max Marks Including Language", Double.class),
    ELEVENTH_AGGREGATE_PERCENTAGE_INCLUDING_LANGUAGES("eleventhAggregatePercentageIncludingLanguages_ApplicantForms_Percentage","11th Aggregate Percentage Including Language", Double.class),

    ELEVENTH_TOTAL_OBTAINED_MARKS_EXCLUDING_LANGUAGE("eleventhTotalObtainedMarksExcludingLanguage_ApplicantForms_Double", "11th Total Obtained Marks Excluding Language", Double.class),
    ELEVENTH_TOTAL_MAX_MARKS_EXCLUDING_LANGUAGE("eleventhTotalMaxMarksExcludingLanguage_ApplicantForms_Double", "11th Total Max Marks Excluding Language", Double.class),
    ELEVENTH_AGGREGATE_PERCENTAGE_EXCLUDING_LANGUAGE("eleventhAggregatePercentageExcludingLanguage_ApplicantForms_Percentage", "11th Aggregate Percentage Excluding Language", Double.class),

    ELEVENTH_SUBJECT_2("eleventhSubject2_ApplicantForms_Text", "11th Subject 2", String.class),
    ELEVENTH_MARKS_SCORED_2("eleventhMarksScored2_ApplicantForms_Double", "11th Marks Scored 2", Double.class),
    ELEVENTH_MAX_MARKS_2("eleventhMaxMarks2_ApplicantForms_Double", "11th Max Marks 2", Double.class),
    ELEVENTH_GRADE_2("eleventhGrade2_ApplicantForms_Text", "11th Grade 2", String.class),
    ELEVENTH_GRADE_2_EQUIVALENT_PERCENTAGE("eleventhGrade2EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 2 percentage", Double.class),


    ELEVENTH_SUBJECT_3("eleventhSubject3_ApplicantForms_Text", "11th Subject 3", String.class),
    ELEVENTH_MARKS_SCORED_3("eleventhMarksScored3_ApplicantForms_Double", "11th Marks Scored 3", Double.class),
    ELEVENTH_MAX_MARKS_3("eleventhMaxMarks3_ApplicantForms_Double", "11th Max Marks 3", Double.class),
    ELEVENTH_GRADE_3("eleventhGrade3_ApplicantForms_Text", "11th Grade 3", String.class),
    ELEVENTH_GRADE_3_EQUIVALENT_PERCENTAGE("eleventhGrade3EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 3 percentage", Double.class),


    ELEVENTH_SUBJECT_4("eleventhSubject4_ApplicantForms_Text", "11th Subject 4", String.class),
    ELEVENTH_MARKS_SCORED_4("eleventhMarksScored4_ApplicantForms_Double", "11th Marks Scored 4", Double.class),
    ELEVENTH_MAX_MARKS_4("eleventhMaxMarks4_ApplicantForms_Double", "11th Max Marks 4", Double.class),
    ELEVENTH_GRADE_4("eleventhGrade4_ApplicantForms_Text", "11th Grade 4", String.class),
    ELEVENTH_GRADE_4_EQUIVALENT_PERCENTAGE("eleventhGrade4EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 4 percentage", Double.class),


    ELEVENTH_SUBJECT_5("eleventhSubject5_ApplicantForms_Text", "11th Subject 5", String.class),
    ELEVENTH_MARKS_SCORED_5("eleventhMarksScored5_ApplicantForms_Double", "11th Marks Scored 5", Double.class),
    ELEVENTH_MAX_MARKS_5("eleventhMaxMarks5_ApplicantForms_Double", "11th Max Marks 5", Double.class),
    ELEVENTH_GRADE_5("eleventhGrade5_ApplicantForms_Text", "11th Grade 5", String.class),
    ELEVENTH_GRADE_5_EQUIVALENT_PERCENTAGE("eleventhGrade5EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 5 percentage", Double.class),

    ELEVENTH_SUBJECT_6("eleventhSubject6_ApplicantForms_Text", "11th Subject 6", String.class),
    ELEVENTH_MARKS_SCORED_6("eleventhMarksScored6_ApplicantForms_Double", "11th Marks Scored 6", Double.class),
    ELEVENTH_MAX_MARKS_6("eleventhMaxMarks6_ApplicantForms_Double", "11th Max Marks 6", Double.class),
    ELEVENTH_GRADE_6("eleventhGrade6_ApplicantForms_Text", "11th Grade 6", String.class),
    ELEVENTH_GRADE_6_EQUIVALENT_PERCENTAGE("eleventhGrade6EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 6 percentage", Double.class),

    ELEVENTH_SUBJECT_7("eleventhSubject7_ApplicantForms_Text", "11th Subject 7", String.class),
    ELEVENTH_MARKS_SCORED_7("eleventhMarksScored7_ApplicantForms_Double", "11th Marks Scored 7", Double.class),
    ELEVENTH_MAX_MARKS_7("eleventhMaxMarks7_ApplicantForms_Double", "11th Max Marks 7", Double.class),
    ELEVENTH_GRADE_7("eleventhGrade7_ApplicantForms_Text", "11th Grade 7", String.class),
    ELEVENTH_GRADE_7_EQUIVALENT_PERCENTAGE("eleventhGrade7EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 7 percentage", Double.class),

    ELEVENTH_SUBJECT_8("eleventhSubject8_ApplicantForms_Text", "11th Subject 8", String.class),
    ELEVENTH_MARKS_SCORED_8("eleventhMarksScored8_ApplicantForms_Double", "11th Marks Scored 8", Double.class),
    ELEVENTH_MAX_MARKS_8("eleventhMaxMarks8_ApplicantForms_Double", "11th Max Marks 8", Double.class),
    ELEVENTH_GRADE_8("eleventhGrade8_ApplicantForms_Text", "11th Grade 8", String.class),
    ELEVENTH_GRADE_8_EQUIVALENT_PERCENTAGE("eleventhGrade8EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 8 percentage", Double.class),

    ELEVENTH_SUBJECT_9("eleventhSubject9_ApplicantForms_Text", "11th Subject 9", String.class),
    ELEVENTH_MARKS_SCORED_9("eleventhMarksScored9_ApplicantForms_Double", "11th Marks Scored 9", Double.class),
    ELEVENTH_MAX_MARKS_9("eleventhMaxMarks9_ApplicantForms_Double", "11th Max Marks 9", Double.class),
    ELEVENTH_GRADE_9("eleventhGrade9_ApplicantForms_Text", "11th Grade 9", String.class),
    ELEVENTH_GRADE_9_EQUIVALENT_PERCENTAGE("eleventhGrade9EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 9 percentage", Double.class),

    ELEVENTH_SUBJECT_10("eleventhSubject10_ApplicantForms_Text", "11th Subject 10", String.class),
    ELEVENTH_MARKS_SCORED_10("eleventhMarksScored10_ApplicantForms_Double", "11th Marks Scored 10", Double.class),
    ELEVENTH_MAX_MARKS_10("eleventhMaxMarks10_ApplicantForms_Double", "11th Max Marks 10", Double.class),
    ELEVENTH_GRADE_10("eleventhGrade10_ApplicantForms_Text", "11th Grade 10", String.class),
    ELEVENTH_GRADE_10_EQUIVALENT_PERCENTAGE("eleventhGrade10EquivalentPercentage_ApplicantForms_Percentage", "11th Grade 10 percentage", Double.class),





    ELEVENTH_AGGREGATE_GRADE_INCLUDING_LANGUAGES("eleventhAggregateGradeIncludingLanguages_ApplicantForms_Text", "11th Aggregate Grade Including Languages", String.class),
 ELEVENTH_AGGREGATE_GRADE_EXCLUDING_LANGUAGES("eleventhAggregateGradeExcludingLanguages_ApplicantForms_Text", "11th Aggregate Grade Excluding Languages", String.class),
   ELEVENTH_AGGREGATE_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGES("eleventhAggregateGradeEquivalentPercentageIncludingLanguages_ApplicantForms_Percentage", "11th Aggregate Grade Equivalent Percentage Including Languages", Double.class),
    ELEVENTH_AGGREGATE_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGES("eleventhAggregateGradeEquivalentPercentageExcludingLanguages_ApplicantForms_Percentage", "11th Aggregate Grade Equivalent Percentage Excluding Languages", Double.class),
    ELEVENTH_AGGREGATE_MARKS_INCLUDING_LANGUAGES("eleventhAggregateMarksIncludingLanguages_ApplicantForms_Double","11th Aggregate marks including languages ",Double.class),
    ELEVENTH_AGGREGATE_MARKS_EXCLUDING_LANGUAGES("eleventhAggregateMarksExcludingLanguages_ApplicantForms_Double","11th Aggregate marks Excluding languages ",Double.class),
    ELEVENTH_AGGREGATE_MARKS_PERCENTAGE_INCLUDING_LANGUAGES("eleventhAggregateMarksPercentageIncludingLanguages_ApplicantForms_Percentage", "11th Aggregate marks Percentage Including Languages", Double.class),
    ELEVENTH_AGGREGATE_MARKS_PERCENTAGE_EXCLUDING_LANGUAGES("eleventhAggregateMarksPercentageExcludingLanguages_ApplicantForms_Percentage", "11th Aggregate marks Percentage Excluding Languages", Double.class),






    // 12 th Details
    TWELFTH_VALUATION_SCHEME("twelfthValuationScheme_ApplicantForms_Bool","Twelfth Valuation Scheme", Boolean.class),
    TWELFTH_MARKS_ARRAY("twelfthMarksArray_ApplicantForms_DocumentArray","12th Marks Array",Document.class),
    TWELFTH_GRADE_ARRAY("twelfthGradeArray_ApplicantForms_DocumentArray","12th Grade Array",Document.class),
    TWELFTH_MAXIMUM_MARKS("twelfthMaximumMarks_ApplicantForms_Double", "12th Maximum Marks", Double.class),

    TWELFTH_MARKS_PERCENTAGE("twelfthMarksPercentage_ApplicantForms_Percentage", "12th Marks Percentage", Double.class),
    TWELFTH_QUALIFYING_EXAM("twelfthQualifyingExam_ApplicantForms_Text", "12th Qualifying Exam", String.class),
    TWELFTH_BOARD_UNIVERSITY("twelfthBoardUniversity_ApplicantForms_Text", "12th Board University", String.class),
    TWELFTH_REGISTER_NUMBER("twelfthRegisterNumber_ApplicantForms_Text", "12th Register Number", String.class),
    TWELFTH_SPECIFY_QUALIFYING_EXAM("twelfthSpecifyQualifyingExam_ApplicantForms_Text", "12th Specify Qualifying Exam", String.class),
    TWELFTH_SPECIFY_BOARD_UNIVERSITY("twelfthSpecifyBoardUniversity_ApplicantForms_Text", "12th Specify Board University", String.class),
    TWELFTH_MARKS_SCORED("twelfthMarksScored_ApplicantForms_Double", "12th Marks Scored", Double.class),


    TWELFTH_INSTITUTION("twelfthInstitution_ApplicantForms_Text", "12th Institution", String.class),
    TWELFTH_YEAR_OF_PASSING("twelfthYearOfPassing_ApplicantForms_Int", "12th Year of Passing", Integer.class),
    TWELFTH_COUNTRY_OF_STUDYING("twelfthCountryOfStudying_ApplicantForms_Text", "12th Country of Studying", String.class),
    TWELFTH_STATE_OF_STUDYING("twelfthStateOfStudying_ApplicantForms_Text", "12th State of Studying", String.class),
    TWELFTH_DISTRICT_OF_STUDYING("twelfthDistrictOfStudying_ApplicantForms_Text", "12th District of Studying", String.class),
    TWELFTH_GRADE_SCORED("twelfthGradeScored_ApplicantForms_Text", "12th Grade Scored", String.class),
    TWELFTH_GRADE_PERCENTAGE("twelfthGradePercentage_ApplicantForms_Percentage", "12th Grade Percentage", Double.class),
    TWELFTH_PDF("twelfthPdf_ApplicantForms_File", "12th PDF", String.class),






//    Result Declared 12th marks Details

    TWELFTH_RESULTS_DECLARED("twelfthResultsDeclared_ApplicantForms_Text", "12th Results Declared/Awaiting", String.class),

    TWELFTH_SUBJECT_1("twelfthSubject1_ApplicantForms_Text", "12th Subject 1", String.class),
    TWELFTH_MARKS_SCORED_1("twelfthMarksScored1_ApplicantForms_Double", "12th Marks Scored 1", Double.class),
    TWELFTH_MAX_MARKS_1("twelfthMaxMarks1_ApplicantForms_Double", "12th Max Marks 1", Double.class),
    TWELFTH_GRADE_1("twelfthGrade1_ApplicantForms_Text", "12th Grade 1", String.class),
    TWELFTH_GRADE_1_EQUIVALENT_PERCENTAGE("twelfthGrade1EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 1 percentage", Double.class),

    TWELFTH_SUBJECT_2("twelfthSubject2_ApplicantForms_Text", "12th Subject 2", String.class),
    TWELFTH_MARKS_SCORED_2("twelfthMarksScored2_ApplicantForms_Double", "12th Marks Scored 2", Double.class),
    TWELFTH_MAX_MARKS_2("twelfthMaxMarks2_ApplicantForms_Double", "12th Max Marks 2", Double.class),
    TWELFTH_GRADE_2("twelfthGrade2_ApplicantForms_Text", "12th Grade 2", String.class),
    TWELFTH_GRADE_2_EQUIVALENT_PERCENTAGE("twelfthGrade2EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 2 percentage", Double.class),

    TWELFTH_SUBJECT_3("twelfthSubject3_ApplicantForms_Text", "12th Subject 3", String.class),
    TWELFTH_MARKS_SCORED_3("twelfthMarksScored3_ApplicantForms_Double", "12th Marks Scored 3", Double.class),
    TWELFTH_MAX_MARKS_3("twelfthMaxMarks3_ApplicantForms_Double", "12th Max Marks 3", Double.class),
    TWELFTH_GRADE_3("twelfthGrade3_ApplicantForms_Text", "12th Grade 3", String.class),
    TWELFTH_GRADE_3_EQUIVALENT_PERCENTAGE("twelfthGrade3EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 3 percentage", Double.class),

    TWELFTH_SUBJECT_4("twelfthSubject4_ApplicantForms_Text", "12th Subject 4", String.class),
    TWELFTH_MARKS_SCORED_4("twelfthMarksScored4_ApplicantForms_Double", "12th Marks Scored 4", Double.class),
    TWELFTH_MAX_MARKS_4("twelfthMaxMarks4_ApplicantForms_Double", "12th Max Marks 4", Double.class),
    TWELFTH_GRADE_4("twelfthGrade4_ApplicantForms_Text", "12th Grade 4", String.class),
    TWELFTH_GRADE_4_EQUIVALENT_PERCENTAGE("twelfthGrade4EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 4 percentage", Double.class),

    TWELFTH_SUBJECT_5("twelfthSubject5_ApplicantForms_Text", "12th Subject 5", String.class),
    TWELFTH_MARKS_SCORED_5("twelfthMarksScored5_ApplicantForms_Double", "12th Marks Scored 5", Double.class),
    TWELFTH_MAX_MARKS_5("twelfthMaxMarks5_ApplicantForms_Double", "12th Max Marks 5", Double.class),
    TWELFTH_GRADE_5("twelfthGrade5_ApplicantForms_Text", "12th Grade 5", String.class),
    TWELFTH_GRADE_5_EQUIVALENT_PERCENTAGE("twelfthGrade5EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 5 percentage", Double.class),

    TWELFTH_SUBJECT_6("twelfthSubject6_ApplicantForms_Text", "12th Subject 6", String.class),
    TWELFTH_MARKS_SCORED_6("twelfthMarksScored6_ApplicantForms_Double", "12th Marks Scored 6", Double.class),
    TWELFTH_MAX_MARKS_6("twelfthMaxMarks6_ApplicantForms_Double", "12th Max Marks 6", Double.class),
    TWELFTH_GRADE_6("twelfthGrade6_ApplicantForms_Text", "12th Grade 6", String.class),
    TWELFTH_GRADE_6_EQUIVALENT_PERCENTAGE("twelfthGrade6EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 6 percentage", Double.class),

    TWELFTH_SUBJECT_7("twelfthSubject7_ApplicantForms_Text", "12th Subject 7", String.class),
    TWELFTH_MARKS_SCORED_7("twelfthMarksScored7_ApplicantForms_Double", "12th Marks Scored 7", Double.class),
    TWELFTH_MAX_MARKS_7("twelfthMaxMarks7_ApplicantForms_Double", "12th Max Marks 7", Double.class),
    TWELFTH_GRADE_7("twelfthGrade7_ApplicantForms_Text", "12th Grade 7", String.class),
    TWELFTH_GRADE_7_EQUIVALENT_PERCENTAGE("twelfthGrade7EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 7 percentage", Double.class),

    TWELFTH_SUBJECT_8("twelfthSubject8_ApplicantForms_Text", "12th Subject 8", String.class),
    TWELFTH_MARKS_SCORED_8("twelfthMarksScored8_ApplicantForms_Double", "12th Marks Scored 8", Double.class),
    TWELFTH_MAX_MARKS_8("twelfthMaxMarks8_ApplicantForms_Double", "12th Max Marks 8", Double.class),
    TWELFTH_GRADE_8("twelfthGrade8_ApplicantForms_Text", "12th Grade 8", String.class),
    TWELFTH_GRADE_8_EQUIVALENT_PERCENTAGE("twelfthGrade8EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 8 percentage", Double.class),

    TWELFTH_SUBJECT_9("twelfthSubject9_ApplicantForms_Text", "12th Subject 9", String.class),
    TWELFTH_MARKS_SCORED_9("twelfthMarksScored9_ApplicantForms_Double", "12th Marks Scored 9", Double.class),
    TWELFTH_MAX_MARKS_9("twelfthMaxMarks9_ApplicantForms_Double", "12th Max Marks 9", Double.class),
    TWELFTH_GRADE_9("twelfthGrade9_ApplicantForms_Text", "12th Grade 9", String.class),
    TWELFTH_GRADE_9_EQUIVALENT_PERCENTAGE("twelfthGrade9EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 9 percentage", Double.class),

    TWELFTH_SUBJECT_10("twelfthSubject10_ApplicantForms_Text", "12th Subject 10", String.class),
    TWELFTH_MARKS_SCORED_10("twelfthMarksScored10_ApplicantForms_Double", "12th Marks Scored 10", Double.class),
    TWELFTH_MAX_MARKS_10("twelfthMaxMarks10_ApplicantForms_Double", "12th Max Marks 10", Double.class),
    TWELFTH_GRADE_10("twelfthGrade10_ApplicantForms_Text", "12th Grade 10", String.class),
    TWELFTH_GRADE_10_EQUIVALENT_PERCENTAGE("twelfthGrade10EquivalentPercentage_ApplicantForms_Percentage", "12th Grade 10 percentage", Double.class),
    //    12th totals
    TWELFTH_TOTAL_OBTAINED_MARKS_INCLUDING_LANGUAGE("twelfthTotalObtainedMarksIncludingLanguage_ApplicantForms_Double", "12th Total Obtained Marks Including Language", Double.class),
    TWELFTH_TOTAL_MAX_MARKS_INCLUDING_LANGUAGE("twelfthTotalMaxMarksIncludingLanguage_ApplicantForms_Double", "12th Total Max Marks Including Language", Double.class),
    TWELFTH_AGGREGATE_PERCENTAGE_INCLUDING_LANGUAGE("twelfthAggregatePercentageIncludingLanguage_ApplicantForms_Percentage", "12th Aggregate Percentage Including Language", Double.class),

    TWELFTH_TOTAL_OBTAINED_MARKS_EXCLUDING_LANGUAGE("twelfthTotalObtainedMarksExcludingLanguage_ApplicantForms_Double", "12th Total Obtained Marks Excluding Language", Double.class),
    TWELFTH_TOTAL_MAX_MARKS_EXCLUDING_LANGUAGE("twelfthTotalMaxMarksExcludingLanguage_ApplicantForms_Double", "12th Total Max Marks Excluding Language", Double.class),
    TWELFTH_AGGREGATE_PERCENTAGE_EXCLUDING_LANGUAGE("twelfthAggregatePercentageExcludingLanguage_ApplicantForms_Percentage", "12th Aggregate Percentage Excluding Language", Double.class),

    TWELFTH_AGGREGATE_GRADE_INCLUDING_LANGUAGES("twelfthAggregateGradeIncludingLanguages_ApplicantForms_Text", "12th Aggregate Grade Including Languages", String.class),
    TWELFTH_AGGREGATE_GRADE_EXCLUDING_LANGUAGES("twelfthAggregateGradeExcludingLanguages_ApplicantForms_Text", "12th Aggregate Grade Excluding Languages", String.class),
//    TWELFTH_AGGREGATE_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGES("twelfthAggregateGradeEquivalentPercentageIncludingLanguages_ApplicantForms_Percentage", "12th Aggregate Grade Equivalent Percentage Including Languages", Double.class),
//    TWELFTH_AGGREGATE_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGES("twelfthAggregateGradeEquivalentPercentageExcludingLanguages_ApplicantForms_Percentage", "12th Aggregate Grade Equivalent Percentage Excluding Languages", Double.class),





//UG details


    UG_QUALIFYING_EXAM("ugQualifyingExam_ApplicantForms_Text", "UG Qualifying Exam", String.class),
    UG_BOARD_UNIVERSITY("ugBoardUniversity_ApplicantForms_Text", "UG Board University", String.class),
    UG_REGISTER_NUMBER("ugRegisterNumber_ApplicantForms_Text", "UG Register Number", String.class),
    UG_NAME_OF_INSTITUTION("ugNameOfInstitution_ApplicantForms_Text", "UG Name of Institution", String.class),
    UG_YEAR_OF_PASSING("ugYearOfPassing_ApplicantForms_Text", "UG Year of Passing", String.class),
    UG_SPECIFY_QUALIFYING_EXAM("ugSpecifyQualifyingExam_ApplicantForms_Text", "UG Specify Qualifying Exam", String.class),
    UG_SPECIFY_BOARD_UNIVERSITY("ugSpecifyBoardUniversity_ApplicantForms_Text", "UG Specify Board University", String.class),
    MARKS_UG_ARRAY("marksUgArray_ApplicantForms_DocumentArray","Marks/Grade UG Array", Document.class),
    GRADE_UG_ARRAY("gradeUgArray_ApplicantForms_DocumentArray","Grade UG Array ", Double.class),
    UG_RESULTS_DECLARED("ugResultsDeclared_ApplicantForms_Text", "UG Results Declared or Awaiting", String.class),
    UG_VALUATION_SCHEME("ugValuationScheme_ApplicantForms_Bool", "Ug Valuation Scheme", Boolean.class),
    UG_COUNTRY_OF_STUDYING("ugCountryOfStudying_ApplicantForms_Text", "UG Country of Studying", String.class),
    UG_STATE_OF_STUDYING("ugStateOfStudying_ApplicantForms_Text", "UG State of Studying", String.class),
    UG_DISTRICT_OF_STUDYING("ugDistrictOfStudying_ApplicantForms_Text", "UG District of Studying", String.class),

//    MARKS_UG_ARRAY_DECLARED("marksUgArrayDeclared_ApplicantForms_DocumentArray","Marks Declared UG Array", Document.class),
//    MARKS_UG_ARRAY_AWAITING("gradeUgArrayAwaiting_ApplicantForms_DocumentArray","Marks Awaiting UG Array", Document.class),

    //    session 1 marks detail
    S_1_MARKS_OBTAINED_INCLUDING_LANGUAGE("s1MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 1 Marks Obtained Including Language", Double.class),
    S_1_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s1MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 1 Maximum Marks Including Language", Double.class),
    S_1_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s1MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 1 Marks Obtained Excluding Language", Double.class),
    S_1_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s1MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 1 Maximum Marks Excluding Language", Double.class),
    S_1_RESULT_STATUS("s1ResultStatus_ApplicantForms_Text", "Session 1 Result Status", String.class),
    S_1_MARKS_DOCUMENT_UPLOAD("s1MarksDocumentUpload_ApplicantForms_File","Session 1 marks Card", String.class),

    //    sem2 mark detail
    S_2_MARKS_OBTAINED_INCLUDING_LANGUAGE("s2MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 2 Marks Obtained Including Language", Double.class),
    S_2_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s2MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 2 Maximum Marks Including Language", Double.class),
    S_2_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s2MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 2 Marks Obtained Excluding Language", Double.class),
    S_2_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s2MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 2 Maximum Marks Excluding Language", Double.class),
    S_2_RESULT_STATUS("s2ResultStatus_ApplicantForms_Text", "Session 2 Result Status", String.class),

    S_2_MARKS_DOCUMENT_UPLOAD("s2MarksDocumentUpload_ApplicantForms_File", "Session 2 Marks Card", String.class),

    //sem3 mark
    S_3_MARKS_OBTAINED_INCLUDING_LANGUAGE("s3MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 3 Marks Obtained Including Language", Double.class),
    S_3_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s3MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 3 Maximum Marks Including Language", Double.class),
    S_3_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s3MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 3 Marks Obtained Excluding Language", Double.class),
    S_3_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s3MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 3 Maximum Marks Excluding Language", Double.class),
    S_3_RESULT_STATUS("s3ResultStatus_ApplicantForms_Text", "Session 3 Result Status", String.class),

    S_3_MARKS_DOCUMENT_UPLOAD("s3MarksDocumentUpload_ApplicantForms_File", "Session 3 Marks Card", String.class),

    S_4_MARKS_OBTAINED_INCLUDING_LANGUAGE("s4MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 4 Marks Obtained Including Language", Double.class),
    S_4_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s4MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 4 Maximum Marks Including Language", Double.class),
    S_4_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s4MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 4 Marks Obtained Excluding Language", Double.class),
    S_4_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s4MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 4 Maximum Marks Excluding Language", Double.class),
    S_4_RESULT_STATUS("s4ResultStatus_ApplicantForms_Text", "Session 4 Result Status", String.class),

    S_4_MARKS_DOCUMENT_UPLOAD("s4MarksDocumentUpload_ApplicantForms_File", "Session 4 Marks Card", String.class),

    S_5_MARKS_OBTAINED_INCLUDING_LANGUAGE("s5MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 5 Marks Obtained Including Language", Double.class),
    S_5_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s5MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 5 Maximum Marks Including Language", Double.class),
    S_5_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s5MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 5 Marks Obtained Excluding Language", Double.class),
    S_5_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s5MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 5 Maximum Marks Excluding Language", Double.class),
    S_5_RESULT_STATUS("s5ResultStatus_ApplicantForms_Text", "Session 5 Result Status", String.class),

    S_5_MARKS_DOCUMENT_UPLOAD("s5MarksDocumentUpload_ApplicantForms_File", "Session 5 Marks Card", String.class),

    S_6_MARKS_OBTAINED_INCLUDING_LANGUAGE("s6MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 6 Marks Obtained Including Language", Double.class),
    S_6_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s6MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 6 Maximum Marks Including Language", Double.class),
    S_6_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s6MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 6 Marks Obtained Excluding Language", Double.class),
    S_6_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s6MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 6 Maximum Marks Excluding Language", Double.class),
    S_6_RESULT_STATUS("s6ResultStatus_ApplicantForms_Text", "Session 6 Result Status", String.class),

    S_6_MARKS_DOCUMENT_UPLOAD("s6MarksDocumentUpload_ApplicantForms_File", "Session 6 Marks Card", String.class),

    S_7_MARKS_OBTAINED_INCLUDING_LANGUAGE("s7MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 7 Marks Obtained Including Language", Double.class),
    S_7_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s7MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 7 Maximum Marks Including Language", Double.class),
    S_7_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s7MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 7 Marks Obtained Excluding Language", Double.class),
    S_7_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s7MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 7 Maximum Marks Excluding Language", Double.class),
    S_7_RESULT_STATUS("s7ResultStatus_ApplicantForms_Text", "Session 7 Result Status", String.class),

    S_7_MARKS_DOCUMENT_UPLOAD("s7MarksDocumentUpload_ApplicantForms_File", "Session 7 Marks Card", String.class),

    S_8_MARKS_OBTAINED_INCLUDING_LANGUAGE("s8MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 8 Marks Obtained Including Language", Double.class),
    S_8_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s8MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 8 Maximum Marks Including Language", Double.class),
    S_8_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s8MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 8 Marks Obtained Excluding Language", Double.class),
    S_8_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s8MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 8 Maximum Marks Excluding Language", Double.class),
    S_8_RESULT_STATUS("s8ResultStatus_ApplicantForms_Text", "Session 8 Result Status", String.class),

    S_8_MARKS_DOCUMENT_UPLOAD("s8MarksDocumentUpload_ApplicantForms_File", "Session 8 Marks Card", String.class),

    S_9_MARKS_OBTAINED_INCLUDING_LANGUAGE("s9MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 9 Marks Obtained Including Language", Double.class),
    S_9_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s9MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 9 Maximum Marks Including Language", Double.class),
    S_9_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s9MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 9 Marks Obtained Excluding Language", Double.class),
    S_9_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s9MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 9 Maximum Marks Excluding Language", Double.class),
    S_9_RESULT_STATUS("s9ResultStatus_ApplicantForms_Text", "Session 9 Result Status", String.class),

    S_9_MARKS_DOCUMENT_UPLOAD("s9MarksDocumentUpload_ApplicantForms_File", "Session 9 Marks Card", String.class),

    S_10_MARKS_OBTAINED_INCLUDING_LANGUAGE("s10MarksObtainedIncludingLanguage_ApplicantForms_Double", "Session 10 Marks Obtained Including Language", Double.class),
    S_10_MAXIMUM_MARKS_INCLUDING_LANGUAGE("s10MaximumMarksIncludingLanguage_ApplicantForms_Double", "Session 10 Maximum Marks Including Language", Double.class),
    S_10_MARKS_OBTAINED_EXCLUDING_LANGUAGE("s10MarksObtainedExcludingLanguage_ApplicantForms_Double", "Session 10 Marks Obtained Excluding Language", Double.class),
    S_10_MAXIMUM_MARKS_EXCLUDING_LANGUAGE("s10MaximumMarksExcludingLanguage_ApplicantForms_Double", "Session 10 Maximum Marks Excluding Language", Double.class),
    S_10_RESULT_STATUS("s10ResultStatus_ApplicantForms_Text", "Session 10 Result Status", String.class),

    S_10_MARKS_DOCUMENT_UPLOAD("s10MarksDocumentUpload_ApplicantForms_File", "Session 10 Marks Card", String.class),
    UG_TOTAL_OBTAINED_MARKS_INCLUDING_LANGUAGE("ugTotalObtainedMarksIncludingLanguage_ApplicantForms_Double","UG Total Obtained Marks Including Language", Double.class),
    UG_TOTAL_MAX_MARKS_INCLUDING_LANGUAGE("ugTotalMaxMarksIncludingLanguage_ApplicantForms_Double","UG Total max Marks Including Language", Double.class),
    UG_AGGREGATE_RESULT_STATUS("ugAggregateResultStatus_ApplicantForms_Text","Ug Aggregate Result Status", String.class),
    UG_AGGREGATE_MARKS_PERCENTAGE_INCLUDING_LANGUAGE("ugAggregateMarksPercentageIncludingLanguage_ApplicantForms_Percentage","UG Aggregate Marks Percentage Including Language",Double.class),
    UG_TOTAL_OBTAINED_MARKS_EXCLUDING_LANGUAGE("ugTotalObtainedMarksExcludingLanguage_ApplicantForms_Double","UG Total Obtained Marks excluding Language", Double.class),
    UG_TOTAL_MAX_MARKS_EXCLUDING_LANGUAGE("ugTotalMaxMarksExcludingLanguage_ApplicantForms_Double","UG Total max Marks Excluding Language", Double.class),
    UG_AGGREGATE_MARKS_PERCENTAGE_EXCLUDING_LANGUAGE("ugAggregateMarksPercentageExcludingLanguage_ApplicantForms_Percentage","UG Aggregate Marks Percentage Excluding Language",Double.class),

    //    ug grade
    S_1_GRADE_SCORED_INCLUDING_LANGUAGE("s1GradeScoredIncludingLanguage_ApplicantForms_Text","S1 grade Scored Including Language", String.class),
    S_1_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s1GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage","s1 Grade Percentage Including Language",Double.class),
    S_1_GRADE_SCORED_EXCLUDING_LANGUAGE("s1GradeScoredExcludingLanguage_ApplicantForms_Text", "S1 grade Scored Excluding Language", String.class),
    S_1_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s1GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S1 Grade Percentage Excluding Language", Double.class),

    S_2_GRADE_SCORED_INCLUDING_LANGUAGE("s2GradeScoredIncludingLanguage_ApplicantForms_Text", "S2 grade Scored Including Language", String.class),
    S_2_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s2GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage", "S2 Grade Percentage Including Language", Double.class),
    S_2_GRADE_SCORED_EXCLUDING_LANGUAGE("s2GradeScoredExcludingLanguage_ApplicantForms_Text", "S2 grade Scored Excluding Language", String.class),
    S_2_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s2GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S2 Grade Percentage Excluding Language", Double.class),

    S_3_GRADE_SCORED_INCLUDING_LANGUAGE("s3GradeScoredIncludingLanguage_ApplicantForms_Text", "S3 grade Scored Including Language", String.class),
    S_3_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s3GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage", "S3 Grade Percentage Including Language", Double.class),
    S_3_GRADE_SCORED_EXCLUDING_LANGUAGE("s3GradeScoredExcludingLanguage_ApplicantForms_Text", "S3 grade Scored Excluding Language", String.class),
    S_3_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s3GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S3 Grade Percentage Excluding Language", Double.class),

    S_4_GRADE_SCORED_INCLUDING_LANGUAGE("s4GradeScoredIncludingLanguage_ApplicantForms_Text", "S4 grade Scored Including Language", String.class),
    S_4_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s4GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage", "S4 Grade Percentage Including Language", Double.class),
    S_4_GRADE_SCORED_EXCLUDING_LANGUAGE("s4GradeScoredExcludingLanguage_ApplicantForms_Text", "S4 grade Scored Excluding Language", String.class),
    S_4_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s4GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S4 Grade Percentage Excluding Language", Double.class),

    S_5_GRADE_SCORED_INCLUDING_LANGUAGE("s5GradeScoredIncludingLanguage_ApplicantForms_Text", "S5 grade Scored Including Language", String.class),
    S_5_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s5GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage", "S5 Grade Percentage Including Language", Double.class),
    S_5_GRADE_SCORED_EXCLUDING_LANGUAGE("s5GradeScoredExcludingLanguage_ApplicantForms_Text", "S5 grade Scored Excluding Language", String.class),
    S_5_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s5GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S5 Grade Percentage Excluding Language", Double.class),

    S_6_GRADE_SCORED_INCLUDING_LANGUAGE("s6GradeScoredIncludingLanguage_ApplicantForms_Text", "S6 grade Scored Including Language", String.class),
    S_6_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s6GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage", "S6 Grade Percentage Including Language", Double.class),
    S_6_GRADE_SCORED_EXCLUDING_LANGUAGE("s6GradeScoredExcludingLanguage_ApplicantForms_Text", "S6 grade Scored Excluding Language", String.class),
    S_6_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s6GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S6 Grade Percentage Excluding Language", Double.class),

    S_7_GRADE_SCORED_INCLUDING_LANGUAGE("s7GradeScoredIncludingLanguage_ApplicantForms_Text", "S7 grade Scored Including Language", String.class),
    S_7_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s7GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage", "S7 Grade Percentage Including Language", Double.class),
    S_7_GRADE_SCORED_EXCLUDING_LANGUAGE("s7GradeScoredExcludingLanguage_ApplicantForms_Text", "S7 grade Scored Excluding Language", String.class),
    S_7_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s7GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S7 Grade Percentage Excluding Language", Double.class),

    S_8_GRADE_SCORED_INCLUDING_LANGUAGE("s8GradeScoredIncludingLanguage_ApplicantForms_Text", "S8 grade Scored Including Language", String.class),
    S_8_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s8GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage", "S8 Grade Percentage Including Language", Double.class),
    S_8_GRADE_SCORED_EXCLUDING_LANGUAGE("s8GradeScoredExcludingLanguage_ApplicantForms_Text", "S8 grade Scored Excluding Language", String.class),
    S_8_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s8GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S8 Grade Percentage Excluding Language", Double.class),

    S_9_GRADE_SCORED_INCLUDING_LANGUAGE("s9GradeScoredIncludingLanguage_ApplicantForms_Text", "S9 grade Scored Including Language", String.class),
    S_9_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s9GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage", "S9 Grade Percentage Including Language", Double.class),
    S_9_GRADE_SCORED_EXCLUDING_LANGUAGE("s9GradeScoredExcludingLanguage_ApplicantForms_Text", "S9 grade Scored Excluding Language", String.class),
    S_9_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s9GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S9 Grade Percentage Excluding Language", Double.class),

    S_10_GRADE_SCORED_INCLUDING_LANGUAGE("s10GradeScoredIncludingLanguage_ApplicantForms_Text", "S10 grade Scored Including Language", String.class),
    S_10_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("s10GradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage", "S10 Grade Percentage Including Language", Double.class),
    S_10_GRADE_SCORED_EXCLUDING_LANGUAGE("s10GradeScoredExcludingLanguage_ApplicantForms_Text", "S10 grade Scored Excluding Language", String.class),
    S_10_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("s10GradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage", "S10 Grade Percentage Excluding Language", Double.class),

    UG_AGGREGATE_GRADE_INCLUDING_LANGUAGE("ugAggregateGradeIncludingLanguage_ApplicantForms_Text","UG Aggregate Grade Including Language ",String.class),
    UG_AGGREGATE_GRADE_EXCLUDING_LANGUAGE("ugAggregateGradeExcludingLanguage_ApplicantForms_Text","UG Aggregate Grade Excluding Language ",String.class),
    UG_AGGREGATE_GRADE_EQUIVALENT_PERCENTAGE_INCLUDING_LANGUAGE("ugAggregateGradeEquivalentPercentageIncludingLanguage_ApplicantForms_Percentage","UG Aggregate Grade Percentage Including Language ",Double.class),
    UG_AGGREGATE_GRADE_EQUIVALENT_PERCENTAGE_EXCLUDING_LANGUAGE("ugAggregateGradeEquivalentPercentageExcludingLanguage_ApplicantForms_Percentage","UG Aggregate Grade Percentage Excluding Language ",Double.class),


    //    eligibility subjects

    PREFERENCES_ARRAY("preferencesArray_ApplicantForms_DocumentArray", "Preferences Array", Document.class),
//  PROGRAMME_PREFERENCE_1("programmePreference1_ApplicantForms_Text","Programme Preference 1", String.class),
    PREFERENCE_1_ELIGIBILITY_SUBJECT_1("preference1EligibilitySubject1_ApplicantForms_Text","Preference1 Eligibility Subject1", String.class),
    PREFERENCE_1_ELIGIBILITY_SUBJECT_1_PERCENTAGE("preference1EligibilitySubject1Percentage_ApplicantForms_Percentage","Preference1 Eligibility Subject1 Percentage", Double.class),
    PREFERENCE_1_ELIGIBILITY_SUBJECT_2("preference1EligibilitySubject2_ApplicantForms_Text","Preference1 Eligibility Subject2", String.class),
    PREFERENCE_1_ELIGIBILITY_SUBJECT_2_PERCENTAGE("preference1EligibilitySubject2Percentage_ApplicantForms_Percentage","Preference1 Eligibility Subject2 Percentage", Double.class),

    // Fields for Preference 2
    PROGRAMME_PREFERENCE_2("programmePreference2_ApplicantForms_Text", "Programme Preference 2", String.class),
    PROGRAMME_PREFERENCE_2_BATCH_CODE("programmePreference2BatchCode_ApplicantForms_Text", "Programme Preference 2 Batch Code", String.class),
    PREFERENCE_2_ELIGIBILITY_SUBJECT_1("preference2EligibilitySubject1_ApplicantForms_Text", "Preference 2 Eligibility Subject 1", String.class),
    PREFERENCE_2_ELIGIBILITY_SUBJECT_1_PERCENTAGE("preference2EligibilitySubject1Percentage_ApplicantForms_Percentage", "Preference 2 Eligibility Subject 1 Percentage", Double.class),
    PREFERENCE_2_ELIGIBILITY_SUBJECT_2("preference2EligibilitySubject2_ApplicantForms_Text", "Preference 2 Eligibility Subject 2", String.class),
    PREFERENCE_2_ELIGIBILITY_SUBJECT_2_PERCENTAGE("preference2EligibilitySubject2Percentage_ApplicantForms_Percentage", "Preference 2 Eligibility Subject 2 Percentage", Double.class),

    // Fields for Preference 3
    PROGRAMME_PREFERENCE_3("programmePreference3_ApplicantForms_Text", "Programme Preference 3", String.class),
    PROGRAMME_PREFERENCE_3_BATCH_CODE("programmePreference3BatchCode_ApplicantForms_Text", "Programme Preference 3 Batch Code", String.class),
    PREFERENCE_3_ELIGIBILITY_SUBJECT_1("preference3EligibilitySubject1_ApplicantForms_Text", "Preference 3 Eligibility Subject 1", String.class),
    PREFERENCE_3_ELIGIBILITY_SUBJECT_1_PERCENTAGE("preference3EligibilitySubject1Percentage_ApplicantForms_Percentage", "Preference 3 Eligibility Subject 1 Percentage", Double.class),
    PREFERENCE_3_ELIGIBILITY_SUBJECT_2("preference3EligibilitySubject2_ApplicantForms_Text", "Preference 3 Eligibility Subject 2", String.class),
    PREFERENCE_3_ELIGIBILITY_SUBJECT_2_PERCENTAGE("preference3EligibilitySubject2Percentage_ApplicantForms_Percentage", "Preference 3 Eligibility Subject 2 Percentage", Double.class),


//    MARKS_SCORED_FOR_ELIGIBILITY_SUBJECT_1("marksScoredForEligibilitySubject1_ApplicantForms_Double","Marks for Eligibility Subject 1",Double.class),
//    MAX_MARKS_SCORED_FOR_ELIGIBILITY_SUBJECT_1("maxMarksScoredForEligibilitySubject1_ApplicantForms_Double"," Max Marks for Eligibility Subject 1",Double.class),
//    GRADE_SCORED_FOR_ELIGIBILITY_SUBJECT_1("gradeScoredForEligibilitySubject1_ApplicantForms_Text","Grade for Eligibility Subject 1",String.class),
//
//    ELIGIBILITY_SUBJECT_2("eligibilitySubject2_ApplicantForms_Text","Eligibility Subject 2", String.class),
//    MARKS_SCORED_FOR_ELIGIBILITY_SUBJECT_2("marksScoredForEligibilitySubject2_ApplicantForms_Double","Marks for Eligibility Subject 2",Double.class),
//    MAX_MARKS_SCORED_FOR_ELIGIBILITY_SUBJECT_2("maxMarksScoredForEligibilitySubject2_ApplicantForms_Double"," Max Marks for Eligibility Subject 2",Double.class),
//    GRADE_SCORED_FOR_ELIGIBILITY_SUBJECT_2("gradeScoredForEligibilitySubject2_ApplicantForms_Text","Grade for Eligibility Subject 2",String.class),
//
//    ELIGIBILITY_SUBJECT_3("eligibilitySubject3_ApplicantForms_Text","Eligibility Subject 3", String.class),
//    MARKS_SCORED_FOR_ELIGIBILITY_SUBJECT_3("marksScoredForEligibilitySubject3_ApplicantForms_Double","Marks for Eligibility Subject 3",Double.class),
//    MAX_MARKS_SCORED_FOR_ELIGIBILITY_SUBJECT_3("maxMarksScoredForEligibilitySubject3_ApplicantForms_Double"," Max Marks for Eligibility Subject 3",Double.class),
//    GRADE_SCORED_FOR_ELIGIBILITY_SUBJECT_3("gradeScoredForEligibilitySubject3_ApplicantForms_Text","Grade for Eligibility Subject 3",String.class),




    //AdditionalDetails
    TRAINING_OR_SHORT_COURSE_ATTENDED_VALIDATION("trainingOrShortCourseAttendedValidation_ApplicantForms_Bool","Training/Short Course Attended", Boolean.class),
    NAME_OF_SHORT_COURSE_OR_TRAINING("nameOfShortCourseOrTraining_ApplicantForms_Text","Name of Program/ShortCourse", String.class),
    DURATION_OF_SHORT_COURSE_OR_TRAINING("durationOfShortCourseOrTraining_ApplicantForms_Text","Duration of short", String.class),
    ADDRESS_OF_SHORT_COURSE_OR_TRAINING_INSTITUTION("addressOfShortCourseOrTrainingInstitution_ApplicantForms_Text","Address of Shortcourse/training Institution",String.class),

    PURPOSE_OF_SHORT_COURSE_OR_TRAINING("purposeOfShortCourseOrTraining_ApplicantForms_Text","Purpose of Training", String.class),
    WORK_EXPERIENCE_VALIDATION("workExperienceValidation_ApplicantForms_Bool","Work Experience",Boolean.class),
    JOB_1_DESIGNATION("job1Designation_ApplicantForms_Text", "Job 1 Designation", String.class),
    JOB_1_ORGANIZATION("job1Organization_ApplicantForms_Text", "Job 1 Organization", String.class),
    JOB_1_DESCRIPTION("job1Description_ApplicantForms_Text", "Job 1 Description", String.class),
    JOB_1_DURATION("job1Duration_ApplicantForms_Text", "Job 1 Duration", String.class),

    JOB_2_DESIGNATION("job2Designation_ApplicantForms_Text", "Job 2 Designation", String.class),
    JOB_2_ORGANIZATION("job2Organization_ApplicantForms_Text", "Job 2 Organization", String.class),
    JOB_2_DESCRIPTION("job2Description_ApplicantForms_Text", "Job 2 Description", String.class),
    JOB_2_DURATION("job2Duration_ApplicantForms_Text", "Job 2 Duration", String.class),

    JOB_3_DESIGNATION("job3Designation_ApplicantForms_Text", "Job 3 Designation", String.class),
    JOB_3_ORGANIZATION("job3Organization_ApplicantForms_Text", "Job 3 Organization", String.class),
    JOB_3_DESCRIPTION("job3Description_ApplicantForms_Text", "Job 3 Description", String.class),
    JOB_3_DURATION("job3Duration_ApplicantForms_Text", "Job 3 Duration", String.class),

    JOB_4_DESIGNATION("job4Designation_ApplicantForms_Text", "Job 4 Designation", String.class),
    JOB_4_ORGANIZATION("job4Organization_ApplicantForms_Text", "Job 4 Organization", String.class),
    JOB_4_DESCRIPTION("job4Description_ApplicantForms_Text", "Job 4 Description", String.class),
    JOB_4_DURATION("job4Duration_ApplicantForms_Text", "Job 4 Duration", String.class),

    ENTRANCE_EXAMINATION_NAME("entranceExaminationName_ApplicantForms_Text", "Entrance Examination Name", String.class),
    ENTRANCE_EXAMINATION_DATE("entranceExaminationDate_ApplicantForms_Date", "Entrance Examination Date", Long.class),
    ENTRANCE_EXAMINATION_ROLLNO("entranceExaminationRollno_ApplicantForms_Text", "Entrance Examination RollNo", String.class),
    ENTRANCE_EXAMINATION_RANK("entranceExaminationRank_ApplicantForms_Text", "Entrance Examination Rank", String.class),
    ENTRANCE_EXAMINATION_DETAILS_SECTION_IS_LOCKED("entranceExaminationDetailsSectionIsLocked_ApplicantForms_Bool", "Entrance Examination Section Is Locked", Boolean.class),
    ENTRANCE_EXAMINATION_UNIVERSITY_FEE_RECEIPT_NUMBER("entranceExaminationUniversityFeeReceiptNumber_ApplicantForms_Text","Applicant University Fee Receipt Number",String.class),
    ENTRANCE_EXAMINATION_UNIVERSITY_FEE_RECEIPT_FILE("entranceExaminationUniversityFeeReceiptFile_ApplicantForms_Text","Applicant University Fee Receipt Number",String.class),

    /// ###  //
    ADDRESS_DETAILS_SECTION_IS_LOCKED("addressDetailsSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    PERSONAL_DETAILS_SECTION_IS_LOCKED("personalDetailsSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    MOTHER_DETAILS_SECTION_IS_LOCKED("motherDetailsSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    FATHER_DETAILS_SECTION_IS_LOCKED("fatherDetailsSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    GUARDIAN_DETAILS_SECTION_IS_LOCKED("guardianDetailsSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    ADDITIONAL_FIELDS_SECTION_IS_LOCKED("additionalFieldsSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    PG_EDUCATION_DETAILS_10_TH_SECTION_IS_LOCKED("pgEducationDetails10ThSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    PG_EDUCATION_DETAILS_12_TH_SECTION_IS_LOCKED("pgEducationDetails12ThSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    UG_EDUCATION_DETAILS_10_TH_SECTION_IS_LOCKED("ugEducationDetails10ThSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    UG_EDUCATION_DETAILS_11_TH_SECTION_IS_LOCKED("ugEducationDetails11ThSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    UG_EDUCATION_DETAILS_12_TH_SECTION_IS_LOCKED("ugEducationDetails12ThSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),
    PG_QUALIFICATION_DETAILS_UG_SECTION_IS_LOCKED("pgQualificationDetailsUgSectionIsLocked_ApplicantForms_Bool", "Address Details Section Lock ", Boolean.class),


    ADMISSION_OFFICE_REMARKS("admissionOfficeRemarks_ApplicantForms_Text","Admission Office Remarks",String.class),
    IS_APPLICANT_RECOMMENDED("isApplicantRecommended_ApplicantForms_Text","Is Applicant Recommended",String.class),
    FORM_STEP_NUMBER("formStepNumber_ApplicantForms_Int","Step Number",Integer.class),
    REQUEST_EDIT_FLAG("requegit stEditFlag_ApplicantForms_Bool","Request Edit Flag", Boolean.class),
    REQUEST_EDIT_STATUS("requestEditStatus_ApplicantForms_Text","Request Edit Status", String.class),
    FORM_SECTION_NAME("formSectionName_ApplicantForms_Text","Section Name", String.class),
//    APPLICANT_ALLOTTED_PROGRAMME("applicantAllottedProgramme_ApplicantForms_Text", "Applicant Alloted Programme", String.class),
    APPLICANT_INTERVIEW_STATUS("applicantInterviewStatus_ApplicantForms_Text", "Applicant Interview Status", String.class),
    APPLICANT_INTERVIEW_REVIEWER_ID("applicantInterviewReviewerId_ApplicantForms_ObjectId", "Applicant Interview Reviewer Id", ObjectId.class),
    APPLICANT_PROGRAM_CHANGE_STATUS("applicantProgramChangeStatus_ApplicantForms_Bool", "Applicant Program Change Status", Boolean.class),
    APPLICANT_INTERVIEW_REVIEWER_NAME("applicantInterviewReviewerName_ApplicantForms_Text", "Applicant Interview Reviewer Name", String.class),
    APPLICANT_INTERVIEW_REVIEWER_EMAIL("applicantInterviewReviewerEmail_ApplicantForms_Text", "Applicant Interview Reviewer Email", String.class),
    APPLICANT_INTERVIEW_DATE("applicantInterviewDate_ApplicantForms_Date", "Applicant Interview Date", Long.class),
    APPLICANT_INTERVIEW_ID("applicantInterviewId_ApplicantForms_ObjectId", "Applicant Interview ID", ObjectId.class),

    SELECTED_REQUEST_ID("selectedRequestId_ApplicantForms_TextArray","Selected request Id",String.class),
    EDIT_REQUEST_APPROVE("editRequestApprove_ApplicantForms_Bool","Edit Request Approve", Boolean.class),
    REQUEST_START_DATE("requestStartDate_ApplicantForms_Text","Request Start Date", String.class),
    REQUEST_END_DATE("requestEndDate_ApplicantForms_Text","Request End Date", String.class),

    DECLARATION_FLAG("declarationFlag_ApplicantForms_Bool", "Declaration Flag", Boolean.class),
    APPLICANT_COUNTRY_CODE("applicantCountryCode_ApplicantForms_Text","Applicant Country Code", String.class),
    APPLICANT_ALT_COUNTRY_CODE("applicantAltCountryCode_ApplicantForms_Text","Applicant Alternate Country Code", String.class),
    FATHER_COUNTRY_CODE("fatherCountryCode_ApplicantForms_Text","Father's Country Code",String.class),
    MOTHER_COUNTRY_CODE("motherCountryCode_ApplicantForms_Text","Mother's Country Code", String.class),
    GUARDIAN_COUNTRY_CODE("guardianCountryCode_ApplicantForms_Text","Guardian's Country Code", String.class),

    //applicant Entrance detail
    APPLICANT_UNIVERSITY_FEE_RECEIPT_NUMBER("applicantUniversityFeeReceiptNumber_ApplicantForms_Text","Applicant University Fee Receipt Number",String.class),
    APPLICANT_UNIVERSITY_FEE_RECEIPT_FILE("applicantUniversityFeeReceiptFile_ApplicantForms_Text","Applicant University Fee Receipt File",String.class);















    private final String property;
    private final String label;
    private final Class dataType;
    private final int minLength;
    private final int maxLength;

    private ApplicantFormsKeysPBinder(String property, String label, Class dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private ApplicantFormsKeysPBinder(String property, String label, Class dataType) {
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
    private static ApplicantFormsKeysPBinder[] $values() {
        return ApplicantFormsKeysPBinder.class.getEnumConstants();
    }
}


