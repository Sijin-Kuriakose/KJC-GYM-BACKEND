package in.edu.kristujayanti.collectionNames;

public enum LogCNBinder implements KJUSYSCNBinder {

    ADMIN_USER_UPDATES_LOG("log_admin_user_updates")
    ;
    private final String collectionName;

    private LogCNBinder(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    // $FF: synthetic method
    private static LogCNBinder[] $values() {
        return LogCNBinder.class.getEnumConstants();
    }

}
