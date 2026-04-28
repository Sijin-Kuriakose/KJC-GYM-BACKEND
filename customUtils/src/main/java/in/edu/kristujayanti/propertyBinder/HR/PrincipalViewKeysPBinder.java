package in.edu.kristujayanti.propertyBinder.HR;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

public enum PrincipalViewKeysPBinder  implements KJUSYSPropertyBinder {
    // notes
    NOTE_TITLE("noteTitle_PrincipalViewKeys_Text", "Note Title", String.class),
    NOTE_TAG("noteTag_PrincipalViewKeys_Text", "Note Tag", String.class),
    NOTE_DESCRIPTION("noteDescription_PrincipalViewKeys_Text", "Note Description", String.class),
    NOTE_CREATED_AT("noteCreatedAt_PrincipalViewKeys_Date","Note Created At",Long.class),
    PRINCIPAL_NOTES_OID("principalNotesOid_PrincipalViewKeys_ObjectId","Principal Notes Object ID",Object.class),
    NOTE_CREATED_BY("noteCreatedBy_PrincipalViewKeys_Text","Note Created By",String.class),
    NOTE_CREATED_BY_EMAIL("noteCreatedByEmail_PrincipalViewKeys_Text","Note Created By Email",String.class);
    // changes;

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;

    private final int maxLength;

    private PrincipalViewKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private PrincipalViewKeysPBinder(String property, String label, Class<?> dataType) {
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

    private static PrincipalViewKeysPBinder[] $values() {
        return PrincipalViewKeysPBinder.class.getEnumConstants();
    }
}
