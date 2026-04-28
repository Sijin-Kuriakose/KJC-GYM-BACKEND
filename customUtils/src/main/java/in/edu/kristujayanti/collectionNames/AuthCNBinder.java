package in.edu.kristujayanti.collectionNames;

public enum AuthCNBinder implements KJUSYSCNBinder {

    AUTH_USERS("auth_users"),
    AUTH_ROLES("auth_roles"),
    AUTH_PERMISSIONS("auth_permissions");


    private final String collectionName;

    private AuthCNBinder(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    // $FF: synthetic method
    private static AuthCNBinder[] $values() {
        return AuthCNBinder.class.getEnumConstants();
    }


}
