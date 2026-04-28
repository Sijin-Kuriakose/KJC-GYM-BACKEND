package in.edu.kristujayanti.util;

import in.edu.kristujayanti.enums.UIPropertyKey;

import java.util.HashMap;
import java.util.Map;

public final class UIPropertyKeyMap {
    static Map<String, String> uiPropertyKeyMap = new HashMap();

    private UIPropertyKeyMap() {
    }

    public static String getFormValidationPropertyKey(String key) {
        return UIPropertyKey.FORM_VALIDATION.getProperty().concat((String)uiPropertyKeyMap.getOrDefault(key, "NO_KEY"));
    }

    public static String getHttpResponsePropertyKey(String key) {
        return UIPropertyKey.HTTP_CODE.getProperty().concat((String)uiPropertyKeyMap.getOrDefault(key, "NO_KEY"));
    }

    static {
        uiPropertyKeyMap.put("REQUIRED", "REQUIRED");
        uiPropertyKeyMap.put("0-20", "0-20");
        uiPropertyKeyMap.put("3-20", "3-20");
        uiPropertyKeyMap.put("6-10", "6-10");
        uiPropertyKeyMap.put("6-6", "6-6");
        uiPropertyKeyMap.put("10-10", "10-10");
        uiPropertyKeyMap.put("1-20", "1-20");
        uiPropertyKeyMap.put(UIPropertyKey.ENUM.getProperty(), UIPropertyKey.ENUM.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.INVALID.getProperty(), UIPropertyKey.INVALID.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.BOOLEANFIELD.getProperty(), UIPropertyKey.BOOLEANFIELD.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.NUMERICFIELD.getProperty(), UIPropertyKey.NUMERICFIELD.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.DATEFORMAT.getProperty(), UIPropertyKey.DATEFORMAT.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.STRING.getProperty(), UIPropertyKey.STRING.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.DOCUMENT.getProperty(), UIPropertyKey.DOCUMENT.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.DATETIME.getProperty(), UIPropertyKey.DATETIME.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.TEXTARRAY.getProperty(), UIPropertyKey.TEXTARRAY.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.NOT_LIST.getProperty(), UIPropertyKey.NOT_LIST.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.INTARRAY.getProperty(), UIPropertyKey.INTARRAY.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.FAILED.getProperty(), UIPropertyKey.FAILED.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.CONTACT_SYSTEM_ADMIN.getProperty(), UIPropertyKey.CONTACT_SYSTEM_ADMIN.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.F_AND_B_PROJECT_IS_DOWN.getProperty(), UIPropertyKey.F_AND_B_PROJECT_IS_DOWN.getProperty());
        uiPropertyKeyMap.put(UIPropertyKey.CODE_SETTING_DOES_NOT_EXIST.getProperty(), UIPropertyKey.CODE_SETTING_DOES_NOT_EXIST.getProperty());

    }
}