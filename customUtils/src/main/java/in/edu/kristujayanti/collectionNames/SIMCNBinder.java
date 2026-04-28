package in.edu.kristujayanti.collectionNames;

public enum SIMCNBinder implements KJUSYSCNBinder{
    INTERVIEW_PANELS_COLLECTION("sim_interview_panels"),
    PROGRAMME_GROUPS_COLLECTION("sim_programme_groups"),
    ;

    private final String collectionName;

    private SIMCNBinder(String collectionName){
        this.collectionName = collectionName;
    }

    private static SIMCNBinder[] $values() {
        return SIMCNBinder.class.getEnumConstants();
    }

    @Override
    public String getCollectionName() {
        return this.collectionName;
    }
}