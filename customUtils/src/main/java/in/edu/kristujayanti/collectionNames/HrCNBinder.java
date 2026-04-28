package in.edu.kristujayanti.collectionNames;

public enum HrCNBinder implements KJUSYSCNBinder{
    ATTENDANCE_BIOMETRIC_ENTRY_COLLECTION("attendance_biometric_entry"),
    ATTENDANCE_LEAVE_ENTRY_COLLECTION("attendance_leave_entry"),
    WORK_SHIFTS_COLLECTION("work_shifts"),
    TIMETABLES_COLLECTION("timetables"),
    HOLIDAYS_COLLECTION("holidays"),
    ATTENDANCE_MONTHLY_SUMMARY_COLLECTION("attendance_monthly_summary"),
    PRINCIPAL_NOTES_COLLECTION("principal_notes"),
    STAFF_ROLE_COLLECTION("staff_role_collection"),
    STAFF_AWARDS_COLLECTION("staffAwardsCollection")
    ;


    private final String collectionName;

    private HrCNBinder(String collectionName){
        this.collectionName = collectionName;
    }

    private static HrCNBinder[] $values() {
        return HrCNBinder.class.getEnumConstants();
    }

    @Override
    public String getCollectionName() {
        return this.collectionName;
    }
}