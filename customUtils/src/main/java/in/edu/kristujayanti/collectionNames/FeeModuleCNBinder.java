package in.edu.kristujayanti.collectionNames;

public enum FeeModuleCNBinder implements KJUSYSCNBinder {
    FEE_HEADER_COLLECTION("feeHeadCollection"),
    FEE_GROUP_COLLECTION("feeGroupCollection"),
    BANK_ACCOUNT_COLLECTION("bankAccountCollection"),
    FEE_STRUCTURE_COLLECTION("feeStructureCollection"),
    STUDENT_COLLECTION("studentCollection"),
    LATE_FEE_RULE_COLLECTION("lateFeeRuleCollection"),
    TRUST_COLLECTION("trustCollection"),
    FEE_CATEGORY_COLLECTION("feeCategoryCollection"),
    FEE_EDIT_LOG_DETAILS("feeEditLogDetails"),
    FEE_COLLECTION_DETAILS("feeCollectionDetails")

    ;

    private final String collectionName;

    private FeeModuleCNBinder(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    // $FF: synthetic method
    private static FeeModuleCNBinder[] $values() {
        return FeeModuleCNBinder.class.getEnumConstants();
    }




}
