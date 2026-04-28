
package in.edu.kristujayanti.propertyBinder.FeeModule;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

import javax.swing.text.Document;

public enum

FeeModuleKeysPBinder implements KJUSYSPropertyBinder {


    ///Trust

    TRUST_NAME("trustName_FeeModule_Text","Trust Name", String.class),
    TRUST_DESCRIPTION("trustDescription_FeeModule_Text","Trust Description", String.class),
    TRUST_OTHER_INFO("trustOtherInfo_FeeModule_Text","Trust Other Info", String.class),
    TRUST_CREATED_BY("trustCreatedBy_FeeModule_Text","Trust Created By", String.class),
    TRUST_CREATED_AT("trustCreatedAt_FeeModule_Date","Trust Created Time",Long.class),
    TRUST_IS_ACTIVE("trustIsActive_FeeModule_Bool","Trust is active", Boolean.class),
    TRUST_UPDATED_BY("trustUpdatedBy_FeeModule_Text","Trust Updated By", String.class),
    TRUST_UPDATED_AT("trustUpdatedAt_FeeModule_Date","Trust Updated At", Long.class),
    TRUST_DETAILS("trustDetails_FeeModule_Document", "Trust Details", Document.class),



    ///Bank Account
    BANK_ACCOUNT_CODE("bankAccountCode_FeeModule_Text","Bank Account Details", String.class),
    BANK_ACCOUNT_NUMBER("bankAccountNumber_FeeModule_Text","Bank Account Number", String.class),
    BANK_ACCOUNT_BENEFICIARY_NAME("bankAccountBeneficiaryName_FeeModule_Text","Bank Account Beneficiary Name", String.class),
    BANK_NAME("bankName_FeeModule_Text","Bank Name", String.class),
    BANK_ACCOUNT_PG_NAME("bankAccountPgName_FeeModule_Text","Bank Account Payment Gateway Name", String.class),
    BANK_IFSC_CODE("bankIfscCode_FeeModule_Text","Bank IFSC Code", String.class),
    BANK_ACCOUNT_OTHER_INFO("bankAccountOtherInfo_FeeModule_Text","Bank Account Info",String.class),
    BANK_ACCOUNT_CREATED_BY("bankAccountCreatedBy_FeeModule_Text","Bank Account Created By", String.class),
    BANK_ACCOUNT_CREATED_AT("bankAccountCreatedAt_FeeModule_Date","Bank Account Created At",Long.class),
    BANK_ACCOUNT_LAST_MODIFIED_BY("bankAccountLastModifiedBy_FeeModule_Text","Bank Account Last Modified By", String.class),
    BANK_ACCOUNT_LAST_MODIFIED_AT("bankAccountLastModifiedAt_FeeModule_Date","Bank Account Last Modified At", Long.class),
    BANK_ACCOUNT_IS_ACTIVE_FLAG("bankAccountIsActiveFlag_FeeModule_Bool","Bank Account is Active", Boolean.class),
    BANK_ACCOUNT_LAST_MODIFIED_EMAIL("bankAccountLastModifiedEmail_FeeModule_Text","Bank Account Last Modified Email", String.class),
    BANK_ACCOUNT_RAZORPAY_ID("bankAccountRazorpayId_FeeModule_Text","Bank Account Razorpay Id",String.class),
    BANK_ACCOUNT_DETAILS("bankAccountDetails_FeeModule_Document","Bank Account Details",Document.class),

    ///Fee group
    FEE_GROUP_CODE("feeGroupCode_FeeModule_Text","Fee Group Code", String.class),
    FEE_GROUP_NAME("feeGroupName_FeeModule_Text","Fee Group Name",String.class),
    FEE_GROUP_DESCRIPTION("feeGroupDescription_FeeModule_Text","Fee Group Description",String.class),
    FEE_GROUP_OTHER_INFO("feeGroupOtherInfo_FeeModule_Text","Fee Group Other Info", String.class),
    FEE_GROUP_FILE_UPLOAD("feeGroupFileUpload_FeeModule_File","Fee Group File Upload", String.class),
    FEE_GROUP_PERMIT_INDEPENDENT_PAYMENT_FLAG("feeGroupPermitIndependentPaymentFlag_FeeModule_Bool","Fee Group Permit Independent Payment Flag", Boolean.class),
    FEE_GROUP_CREATED_BY("feeGroupCreatedBy_FeeModule_Text","Fee Group Created By", String.class),
    FEE_GROUP_CREATED_AT("feeGroupCreatedAt_FeeModule_Date","Fee Group Created At", Long.class),
    FEE_GROUP_LAST_MODIFIED_AT("feeGroupLastModifiedAt_FeeModule_Date","Fee Group last Modified At", Long.class),
    FEE_GROUP_LAST_MODIFIED_BY("feeGroupLastModifiedBy_FeeModule_Text","Fee Group Last Modified By", String.class),
    FEE_GROUP_IS_ACTIVE_FLAG("feeGroupIsActiveFlag_FeeModule_Bool","Fee Group Is Active", Boolean.class),
    FEE_GROUP_LAST_MODIFIED_EMAIL("feeGroupLastModifiedEmail_FeeModule_Text","Fee Group Last Modified Email", String.class),
    FEE_GROUP_RECEIPT_SEQUENTIAL_NUMBER("feeGroupReceiptSequentialNumber_FeeModule_Long","Fee Group Sequential Number", Long.class),


    ///fee head
    FEE_HEAD_CODE("feeHeadCode_FeeModule_Text","Fee Head Code ", String.class),
    FEE_HEAD_NAME("feeHeadName_FeeModule_Text","Fee Head Name", String.class),
    FEE_HEAD_DESCRIPTION("feeHeadDescription_FeeModule_Text","Fee Head Description", String.class),
    FEE_HEAD_TYPE("feeHeadType_FeeModule_Text","Fee Head Type",String.class),
    FEE_HEAD_MODULE("feeHeadModule_FeeModule_Text","Fee Head Modules",String.class),
    FEE_HEAD_OTHER_INFO("feeHeadOtherInfo_FeeModule_Text","Fee Head Other Info", String.class),
    FEE_HEAD_REGISTRATION_FEE_FLAG("feeHeadRegistrationFeeFlag_FeeModule_Bool","FeeHead Registration Fee", Boolean.class),
    FEE_HEAD_FILE_UPLOAD("feeHeadFileUpload_FeeModule_File","Fee Head File Upload", String.class),
    FEE_HEAD_IS_LATE_FEE_FLAG("feeHeadIsLateFeeFlag_FeeModule_Bool","Fee Head Is Late Fee", Boolean.class),
    FEE_HEAD_LATE_FEE_FOR("feeHeadLateFeeFor_FeeModule_Text","Fee Head Late Fee For",String.class),
    FEE_HEAD_IS_ACTIVE_FLAG("feeHeadIsActiveFlag_FeeModule_Bool","Fee Head Is Active", Boolean.class),

    FEE_HEAD_CREATED_BY("feeHeadCreatedBy_FeeModule_Text", "Fee Head Created By", String.class),
    FEE_HEAD_CREATED_AT("feeHeadCreatedAt_FeeModule_Date", "Fee Head Created At", Long.class),
    FEE_HEAD_LAST_MODIFIED_AT("feeHeadLastModifiedAt_FeeModule_Date", "Fee Head Last Modified At", Long.class),
    FEE_HEAD_LAST_MODIFIED_BY("feeHeadLastModifiedBy_FeeModule_Text", "Fee Head Last Modified By", String.class),
    FEE_HEAD_LAST_MODIFIED_EMAIL("feeHeadLastModifiedEmail_FeeModule_Text", "Fee Head Last Modified Email", String.class),
    IS_FEE_HEAD_SELECTED("isFeeHeadSelected_FeeModule_Bool","Is Fee Head Selected",Boolean.class),
    IS_FEE_HEAD_PAID("isFeeHeadPaid_FeeModule_Bool","Is Fee Head Paid",Boolean.class),
    IS_FEE_HEAD_CONCESSION("isFeeHeadConcession_FeeModule_Bool","Is Fee Head Concession",Boolean.class),

    ///fee structure
    FEE_STRUCTURE_CODE("feeStructureCode_FeeModule_Text","Fee Structure Code", String.class),
    FEE_STRUCTURE_NAME("feeStructureName_FeeModule_Text","Fee Structure Name",String.class),
    FEE_STRUCTURE_DESCRIPTION("feeStructureDescription_FeeModule_Text","Fee Structure Description",String.class),
    FEE_STRUCTURE_LATE_FEE_RULE("feeStructureLateFeeRule_FeeModule_Text","Late Fee Rule", String.class),
    FEE_STRUCTURE_FEE_REFUND_RULE("feeStructureFeeRefundRule_FeeModule_Text","Fee Refund Rule",String.class),
    FEE_STRUCTURE_APPLICABILITY("feeStructureApplicability_FeeModule_Document","Fee Structure Applicability", Document.class),
    FEE_STRUCTURE_CURRENCY("feeStructureCurrency_FeeModule_Text","Fee Structure Currency",String.class),
    FEE_STRUCTURE_IS_FOR_ENROLLMENT_FLAG("feeStructureIsForEnrollmentFlag_FeeModule_Bool","Is For Enrollment Flag", Boolean.class),
    FEE_STRUCTURE_FILE_UPLOAD("feeStructureFileUpload_FeeModule_File","Fee Structure File Upload", String.class),
    IS_FEE_STRUCTURE_APPLIED("isFeeStructureApplied_FeeModule_Bool","Is Fee Structure Applied", Boolean.class),


    LIST_OF_FEE_STRUCTURE("listOfFeeStructure_FeeModule_DocumentArray","List Of Fee Structure",Document.class),

    /// fee structure records
    FEE_STRUCTURE_RECORD("feeStructureRecord_FeeModule_DocumentArray","Fee Structure Record",Document.class),
    FEE_STRUCTURE_AMOUNT("feeStructureAmount_FeeModule_Long","Fee Structure",Long.class),


    //fee module object ids
    BANK_ACCOUNT_OID("bankAccountOid_FeeModule_ObjectId","Bank Account Object ID", Object.class),
    FEE_HEAD_OID("feeHeadOid_FeeModule_ObjectId","Fee Head Object ID", Object.class),
    FEE_GROUP_OID("feeGroupOid_FeeModule_ObjectId","Fee Group Object ID", Object.class),
    FEE_STRUCTURE_OID("feeStructureOid_FeeModule_ObjectId","Fee Structure Object ID", Object.class),
    LATE_FEE_RULE_OID("lateFeeRuleOid_FeeModule_ObjectId","Late Fee Rule Object ID", Object.class),
    TRUST_OID("trustOid_FeeModule_ObjectId","Trust Object Id", Object.class),
    FEE_HEAD_LATE_OID("feeHeadLateOid_FeeModule_ObjectId","Late Fee head Object id", Object.class),

    ///fee structure records
    FEE_STRUCTURE_RECORD_START_DATE("feeStructureRecordStartDate_FeeModule_Date","Fee Structure Record Start Date",Long.class),
    FEE_STRUCTURE_RECORD_END_DATE("feeStructureRecordEndDate_FeeModule_Date","Fee Structure Record End Date",Long.class),
    FEE_STRUCTURE_RECORD_POSITION("feeStructureRecordPosition_FeeModule_Int","Fee Structure Position",Integer.class),




    //payment keys
    TUITION_FEES("tuitionFees_FeeModule_Double", "Tuition Fees", Double.class),
    APPLICANT_TOTAL_FEE_AMOUNT_DUE("applicantTotalFeeAmountDue_FeeModule_Long","Applicant Total Fee Amount Due", Long.class),






    //fee structure applicability fields

    FEE_STRUCTURE_QUOTA("feeStructureQuota_FeeModule_TextArray","Fee Structure Quota", String.class),
    FEE_STRUCTURE_BATCH("feeStructureBatch_FeeModule_TextArray","Fee Structure Batch", String.class),
    FEE_STRUCTURE_SESSION("feeStructureSession_FeeModule_IntArray","Fee Structure Session", Integer.class),
    FEE_STRUCTURE_CATEGORY("feeStructureCategory_FeeModule_TextArray","Fee Structure Category", String.class),
    FEE_STRUCTURE_CASTE("feeStructureCaste_FeeModule_TextArray","Fee Structure Caste", String.class),





        //fee category
    FEE_CATEGORY_CODE("feeCategoryCode_FeeModule_Text","Fee Category Code", String.class),
    FEE_CATEGORY_NAME("feeCategoryName_FeeModule_Text","Fee Category Name",String.class),
    FEE_CATEGORY_DESCRIPTION("feeCategoryDescription_FeeModule_Text","Fee Category Description", String.class),
    FEE_CATEGORY_TYPE("feeCategoryType_FeeModule_Text","Fee Category Type", String.class),
    FEE_CATEGORY_SHOWN_FOR_ALL_BATCH_FLAG("feeCategoryShownForAllBatchFlag_FeeModule_Bool","Fee category Shown for all batch Flag", Boolean.class),
    FEE_CATEGORY_IS_INTERNATIONAL_FLAG("feeCategoryIsInternationalFlag_FeeModule_Bool","Fee Category Is International Flag", Boolean.class),
    FEE_CATEGORY_FILE_UPLOAD("feeCategoryFileUpload_FeeModule_File","Fee Category File Upload", String.class),
    FEE_CATEGORY_IS_ACTIVE("feeCategoryIsActive_FeeModule_Bool","Fee Category is Active", Boolean.class),

    // amounts
    ADVANCE_AMOUNT("advanceAmount_FeeModule_Long","Advance Amount",Long.class),
    PAYABLE_AMOUNT("payableAmount_FeeModule_Long","Payable Amount",Long.class),
    IS_PAYABLE_AMOUNT_APPLIED("isPayableAmountApplied_FeeModule_Bool","Is Payable Amount Applied",Boolean.class),




    //  late fee rule
    LATE_FEE_RULE_CODE("lateFeeRuleCode_FeeModule_Text","Late Fee Rule Code", String.class),
    LATE_FEE_RULE_NAME("lateFeeRuleName_FeeModule_Text","Late Fee Rule Name",String.class),
    LATE_FEE_RULE_DESCRIPTION("lateFeeRuleDescription_FeeModule_Text","Late Fee Rule Description",String.class),
    LATE_FEE_RULE_DURATION("lateFeeRuleDuration_FeeModule_Int","Late Fee Rule Duration",Integer.class),
    IS_LATE_FEE_RULE_DISABLE_FEE_VISIBILITY("isLateFeeRuleDisableFeeVisibility_FeeModule_Bool","Is Late Fee Rule Disable Fee Visibility",Boolean.class),

    // late fee rule schedules
    LATE_FEE_RULE_SCHEDULE_RECORD("lateFeeRuleScheduleRecord_FeeModule_DocumentArray","Late Fee Rule Schedule Record",Document.class),
    LATE_FEE_RULE_SCHEDULE_DAY("lateFeeRuleScheduleDay_FeeModule_Int","Late Fee Rule Schedule Day",Integer.class),
    LATE_FEE_RULE_SCHEDULE_TYPE("lateFeeRuleScheduleType_FeeModule_Text","Late Fee Rule Schedule Type",String.class),
    LATE_FEE_RULE_SCHEDULE_AMOUNT("lateFeeRuleScheduleAmount_FeeModule_Long","Late Fee Rule Schedule Amount",Long.class),
    IS_LATE_FEE_RULE_SCHEDULE_CUMULATIVE("isLateFeeRuleScheduleCumulative_FeeModule_Bool","Is Late Fee Rule Schedule Cumulative",Boolean.class),


    // common fee payment Mode
    FEE_PAYMENT_MODE("feePaymentMode_FeeModule_Text","Fee Payment Mode",String.class),
    // fee collection
    FEE_COLLECTION_AMOUNT_TO_BE_PAID("feeCollectionAmountToBePaid_FeeModule_Long","Fee Collection Amount Amount To Be Paid",Long.class),
    FEE_COLLECTION_PAY_AMOUNT("feeCollectionPayAmount_FeeModule_Long","Fee Collection Pay Amount",Long.class),
    FEE_COLLECTION_PAYMENT_MODE("feeCollectionPaymentMode_FeeModule_Text","Fee Collection Payment Mode",String.class),

    // fee collection cheque
//    FEE_COLLECTION_MICR_NUMBER("feeCollectionMicrNumber_FeeModule_Int","Fee Collection MICR Number",Integer.class),
    FEE_COLLECTION_MICR_NUMBER("feeCollectionMicrNumber_FeeModule_Text","Fee Collection MICR Number",String.class),
    FEE_COLLECTION_INSTRUMENT_NUMBER("feeCollectionInstrumentNumber_FeeModule_Text","Fee Collection Instrument Number",String.class),
    FEE_COLLECTION_INSTRUMENT_DATE("feeCollectionInstrumentDate_FeeModule_Date","Fee Collection Instrument Date",Long.class),
    FEE_COLLECTION_TYPE_OF_CHEQUE_OR_DD("feeCollectionTypeOfChequeOrDd_FeeModule_Text","Fee Collection Type Of Cheque Or Dd",String.class),
    FEE_COLLECTION_GUARDIAN_TYPE("feeCollectionGuardianType_FeeModule_Text","Fee Collection Guardian Type",String.class),
    FEE_COLLECTION_GUARDIAN_NAME("feeCollectionGuardianName_FeeModule_Text","Fee Collection Guardian Name",String.class),
    FEE_COLLECTION_META_DATA("feeCollectionMetaData_FeeModule_Document","Fee Collection Meta Data",Document.class),
    FEE_COLLECTED_STAFF_NAME("feeCollectedStaffName_FeeModule_Text","Fee Collected Staff Name", String.class),
    FEE_COLLECTED_USER_ID("feeCollectedUserId_FeeModule_Text","Fee_Collected User ID", String.class),
    FEE_COLLECTED_STAFF_EMAIL("feeCollectedStaffEmail_FeeModule_Text","Fee Collected Staff Email", String.class),
    FEE_COLLECTION_DATE("feeCollectionDate_FeeModule_Date","Fee Collection Date",Long.class),


    // fee collection other info
    FEE_COLLECTION_ACKNOWLEDGEMENT_NUMBER("feeCollectionAcknowledgementNumber_FeeModule_Int","Fee Collection Acknowledgement Number",Integer.class),
    FEE_COLLECTION_BALANCE_AMOUNT("feeCollectionBalanceAmount_FeeModule_Long","Fee Collection Balance Amount",Long.class),
    FEE_COLLECTION_REMARK("feeCollectionRemark_FeeModule_Text","Fee Collection Remark",String.class),
    IS_FEE_COLLECTION_RECEIPT("isFeeCollectionReceipt_FeeModule_Bool","Is Fee Collection Receipt",Boolean.class),

    // fee order details
    FEE_PAYMENT_ORDER_ID("feePaymentOrderId_FeeModule_ObjectId","Fee Payment Order Id",Object.class),

    FEE_COLLECTION_BANK_NAME("feeCollectionBankName_FeeModule_Text","Fee Collection Bank Name", String.class),
    FEE_COLLECTION_CARD_NUMBER("feeCollectionCardNumber_FeeModule_Text", "Fee Collection Card Number", String.class),


    //grouping receipts
    FEE_GROUP_SCHEDULED_AMOUNT("feeGroupScheduledAmount_FeeModule_Long","Fee Group Scheduled Amount", Long.class),
    FEE_GROUP_PAID_AMOUNT("feeGroupPaidAmount_FeeModule_Long","Fee Group Paid Amount", Long.class),
    FEE_GROUP_DUE_AMOUNT("feeGroupDueAmount_FeeModule_Long","Fee Group Due Amount", Long.class),
    FEE_HEAD_SCHEDULED_AMOUNT("feeHeadScheduledAmount_FeeModule_Long","Fee Head Scheduled Amount", Long.class),
    TOTAL_FEE_HEAD_PAID_AMOUNT("totalFeeHeadPaidAmount_FeeModule_Long","Total Fee Head Paid Amount", Long.class),
    TOTAL_FEE_HEAD_DUE_AMOUNT("totalFeeHeadDueAmount_FeeModule_Long","Total Fee Head Due Amount",Long .class),
    TOTAL_SELECTED_FEE_HEAD_AMOUNT_PAID("totalSelectedFeeHeadAmountPaid_FeeModule_Long","Total selected Fee Head Amount Paid",Long.class),
    FEE_HEAD_ARREAR_AMOUNT("feeHeadArrearAmount_FeeModule_Long","Fee Head Arrear Amount", Long.class),
    FEE_HEAD_ARREAR_AMOUNT_DATE("feeHeadArrearAmountDate_FeeModule_Date","Fee Head Arrear Amount Date",Long.class)












            ;

    private final String property;
    private final String label;
    private final Class dataType;
    private final int minLength;
    private final int maxLength;

    private FeeModuleKeysPBinder(String property, String label, Class dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private FeeModuleKeysPBinder(String property, String label, Class dataType) {
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
    private static in.edu.kristujayanti.propertyBinder.Eform.Applicant.ApplicantProgrammeKeysPBinder[] $values() {
        return in.edu.kristujayanti.propertyBinder.Eform.Applicant.ApplicantProgrammeKeysPBinder.class.getEnumConstants();
    }
}
