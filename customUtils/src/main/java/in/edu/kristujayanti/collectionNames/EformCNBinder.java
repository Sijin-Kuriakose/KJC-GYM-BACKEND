package in.edu.kristujayanti.collectionNames;

public enum EformCNBinder implements KJUSYSCNBinder{

    APPLICANT_PROFILE_COLLECTION("applicant"),
    APPLICATION_NUMBER_COLLECTION("applicationNumber"),
    FORM_BUILDER_COLLECTION("formBuilder"),
    BATCH_DETAILS_COLLECTION("batch_details"),
    INSTITUTIONAL_DETAILS("institutional_details"),
    PROGRAMME_CATEGORIES("programme_categories"),
    SCRUTINY_STAFF_LOG("scrutinyStaffLog"),
    QUOTA_DETAILS_COLLECTION("quotaDetails"),
    APPLICATION_SEQUENCE_COLLECTION("applicationSequenceNumberCollection"),
    CATEGORY_DETAILS_COLLECTION("categoryDetails"),
    CASTE_DETAILS_COLLECTION("casteDetails");

    ;



    private final String collectionName;

    private EformCNBinder(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    // $FF: synthetic method
    private static EformCNBinder[] $values() {
        return EformCNBinder.class.getEnumConstants();
    }




}
