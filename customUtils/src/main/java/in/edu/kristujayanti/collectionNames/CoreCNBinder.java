package in.edu.kristujayanti.collectionNames;

public enum CoreCNBinder implements KJUSYSCNBinder {

    ERP_STAFF_USER_PROFILE("erp_staff_user_profile"),
    UI_MENUS("ui_menus"),
    UI_MENU_GROUPS("ui_menu_groups")
    ;

    private final String collectionName;

    private CoreCNBinder(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    // $FF: synthetic method
    private static CoreCNBinder[] $values() {
        return CoreCNBinder.class.getEnumConstants();
    }
}
