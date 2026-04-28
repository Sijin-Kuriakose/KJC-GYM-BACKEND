package in.edu.kristujayanti.propertyBinder.core;

import in.edu.kristujayanti.propertyBinder.KJUSYSCommonKeysPBinder;
import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import org.bson.Document;
import org.bson.types.ObjectId;

public enum MenuPBinder implements KJUSYSPropertyBinder {
    // MENU MAPPED
    MENU_MAPPED("menuMapped_Menu_DocumentArray", "Menu Mapped for role",Document .class),
    MENU_DISPLAY_NAME("menuDisplayName_Menu_Text", "Menu Display Name", String.class),
    MENU_DESCRIPTION("menuDescription_Menu_Text", "Menu Description", String.class),
    MENU_ROUTE("menuRoute_Menu_Text", "Menu Route", String.class),
    MENU_NG_MODULE_NAME("menuNgModuleName_Menu_Text", "Menu ng module name", String.class),
    MENU_EXPOSED_MODULE("menuExposedModule_Menu_Text", "Menu exposed module", String.class),
    MENU_PAGE_ELEMENTS("menuPageElements_Menu_TextArray", "Menu page elements", String.class),
    MENU_ALLOWED_PAGE_ELEMENTS("menuAllowedPageElements_Menu_TextArray", "Menu allowed page elements", String.class),
    MENU_OPERATIONS("menuOperations_Menu_Document", "Menu Operations", Document.class),
    MENU_ALLOWED_OPERATIONS("menuAllowedOperations_Menu_Document", "Menu Allowed Operations", Document.class),

    // OPERATIONS
    CREATE_OPERATION("createOperation_Menu_ObjectIdArray", "Create Operation", ObjectId.class),
    READ_OPERATION("readOperation_Menu_ObjectIdArray", "Read Operation", ObjectId.class),
    UPDATE_OPERATION("updateOperation_Menu_ObjectIdArray", "Update Operation", ObjectId.class),

    // OPERATION ALLOWED
    CREATE_OPERATION_ALLOWED("createOperationAllowed_Menu_Bool", "Create Operation Allowed", Boolean.class),
    READ_OPERATION_ALLOWED("readOperationAllowed_Menu_Bool", "Read Operation Allowed", Boolean.class),
    UPDATE_OPERATION_ALLOWED("updateOperationAllowed_Menu_Bool", "Update Operation Allowed", Boolean.class),

    // MENU GROUP
    MENU_GROUP("menuGroup_Menu_ObjectId", "Menu Group", ObjectId.class),
    MENU_GROUP_NAME("menuGroupName_Menu_Text", "Menu Group Name", String.class),
    MENU_GROUP_ICON("menuGroupIcon_Menu_Text", "Menu Group Icon", String.class),
    ;

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private MenuPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private MenuPBinder(String property, String label, Class<?> dataType) {
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

    public Class getDataType() {
        return this.dataType;
    }

    public Integer getMinLength() {
        return this.minLength;
    }

    public Integer getMaxLength() {
        return this.maxLength;
    }

    // New method to get label by property name
    public static String getLabelByProperty(String propertyName) {
        for (MenuPBinder binder : values()) {
            if (binder.property.equals(propertyName)) {
                return binder.label;
            }
        }
        return null;
    }

    // New method to get property name by label
    public static String getPropertyByLabel(String label) {
        for (MenuPBinder binder : values()) {
            if (binder.label.equals(label)) {
                return binder.property;
            }
        }
        return null;
    }

    // $FF: synthetic method
    private static KJUSYSCommonKeysPBinder[] $values() {
        return KJUSYSCommonKeysPBinder.class.getEnumConstants();
    }
}