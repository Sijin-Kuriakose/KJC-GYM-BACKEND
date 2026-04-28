package in.edu.kristujayanti.propertyBinder.HR;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

public enum StaffRoleKeysPBinder implements KJUSYSPropertyBinder {

    //STAFF ROLES :

    STAFF_ROLE_CREATED_AT("staffRoleCreatedAt_StaffRoleKeysPBinder_Date","Staff Role Created At",Long.class),
    //STAFF_ROLE_CREATED_BY("staffRoleCreatedBy_StaffRoleKeysPBinder_Text","Staff Role Created By",String.class),
    //STAFF_ROLE_CREATED_BY_EMAIL("staffRoleCreatedByEmail_StaffRoleKeysPBinder_Text","Staff Role Created By Email",String.class),
    ROLE_YEAR("roleYear_StaffRoleKeysPBinder_Text", "Role Year", String.class),
    ROLE_NAME("roleName_StaffRoleKeysPBinder_Text", "Role Name", String.class),
    STAFF_ROLE_OID("staffRoleOid_StaffRoleKeysPBinder_ObjectId","Staff Role Object ID",Object.class);

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;

    private final int maxLength;

    private StaffRoleKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private StaffRoleKeysPBinder(String property, String label, Class<?> dataType) {
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

    private static StaffRoleKeysPBinder[] $values() {
        return StaffRoleKeysPBinder.class.getEnumConstants();
    }
}
