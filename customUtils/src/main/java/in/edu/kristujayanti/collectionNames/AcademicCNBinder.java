package in.edu.kristujayanti.collectionNames;

public enum AcademicCNBinder implements  KJUSYSCNBinder {
    ACADEMIC_BATCH_DETAILS_COLLECTION("academicBatchDetails"),
    SESSION_DETAILS_COLLECTION("sessionDetails"),;




    private final String collectionName;

    private AcademicCNBinder(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    // $FF: synthetic method
    private static AcademicCNBinder[] $values() {
        return AcademicCNBinder.class.getEnumConstants();
    }


}
