package in.edu.kristujayanti.propertyBinder;

import org.bson.types.ObjectId;

public enum AuthCommonKeysPBinder implements KJUSYSPropertyBinder {

    USER_EMAIL("userEmail_AuthCommon_Text" , "User Email" , String.class),
    USER_PASSWORD("userPassword_AuthCommon_Text", "User Password" , String.class),
    ACCESS_TOKEN("accessToken_AuthCommon_Text", "Access Token" , String.class),
    REFRESH_TOKEN("refreshToken_AuthCommon_Text", "Refresh Token", String.class),

    // AUTH ROLES
    AUTH_ROLES("authRoles_AuthCommon_ObjectIdArray", "Auth Roles", ObjectId.class),
    AUTH_ROLE_NAME("authRoleName_AuthCommon_Text", "Auth Role Name", String.class),
    AUTH_ROLE_DESCRIPTION("authRoleDescription_AuthCommon_Text", "Auth Role Description", String.class),

    // AUTH PERMISSIONS
    AUTH_PERMISSION_NAME("authPermissionName_AuthCommon_Text", "Auth Permission Name", String.class),
    AUTH_PERMISSION_DESCRIPTION("authPermissionDescription_AuthCommon_Text", "Auth Permission Description", String.class),
    AUTH_PERMISSION_ENDPOINT("authPermissionEndpoint_AuthCommon_Text", "Auth Permission Endpoint", String.class),
    AUTH_PERMISSION_ENDPOINT_METHOD("authPermissionEndpointMethod_AuthCommon_Text", "Auth Permission Endpoint Method", String.class),

    // SUPERUSER / DEV
    IS_SUPERUSER("isSuperuser_AuthCommon_Bool", "Is Superuser", Boolean.class),
    IS_DEVELOPER("isDeveloper_AuthCommon_Bool", "Is Developer", Boolean.class);

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private AuthCommonKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private AuthCommonKeysPBinder(String property, String label, Class<?> dataType) {
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

    public static String getLabelByPropertyName(String propertyName) {
        for (AuthCommonKeysPBinder binder : AuthCommonKeysPBinder.values()) {
            if (binder.getPropertyName().equals(propertyName)) {
                return binder.getDisplayName();
            }
        }
        return null; // Or throw an exception if needed
    }

    // $FF: synthetic method
    private static AuthCommonKeysPBinder[] $values() {
        return AuthCommonKeysPBinder.class.getEnumConstants();
    }
}