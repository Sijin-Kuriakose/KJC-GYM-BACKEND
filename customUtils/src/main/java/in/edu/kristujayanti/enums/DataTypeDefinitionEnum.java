package in.edu.kristujayanti.enums;

import org.bson.types.ObjectId;

import javax.swing.text.Document;
import java.util.List;

public enum DataTypeDefinitionEnum {
    BOOL("_Bool", Boolean.class),
    DATE("_Date", Long.class),
    ENDDATE("_EndDate", Long.class),
    STARTDATE("_StartDate", Long.class),
    TEXT("_Text", String.class),
    EMAIL("_Email", String.class),
    INT("_Int", Integer.class),
    LONG("_Long", Long.class),
    PERCENTAGE("_Percentage", Double.class),
    DOUBLE("_Double", Double.class),
    DATETIME("_DateTime", Long.class),
    TIME("_Time", Long.class),
    ARRAY("_Array", List.class),
    FILE("_File", String.class),
    IMAGE("_Image", String.class),
    CURRENCY("_Currency", Double.class),
    DOCUMENT("_Document", Document.class),
    TEXTARRAY("_TextArray", String.class),

    INTARRAY("_IntArray", Integer.class),
    DOCUMENTARRAY("_DocumentArray", Document.class),

    OBJECTID("_ObjectId", ObjectId.class),
    OBJECTIDARRAY("_ObjectIdArray", ObjectId.class);



    private final String appendedString;
    private final Class dataTypeDefinitionClass;

    DataTypeDefinitionEnum(String appendedString, Class dataTypeDefinitionClass) {
        this.appendedString = appendedString;
        this.dataTypeDefinitionClass = dataTypeDefinitionClass;
    }

    public String getAppendedString() {
        return appendedString;
    }

    public Class getDataTypeDefinitionClass() {
        return dataTypeDefinitionClass;
    }

    public static DataTypeDefinitionEnum getEnumByProperty(String propertyName, String enumValue) {
        try {
            return valueOf(enumValue.toUpperCase());
        } catch (Exception var3) {
            throw new IllegalArgumentException("Property " + propertyName + " is invalid");
        }
    }

    // $FF: synthetic method
    private static DataTypeDefinitionEnum[] $values() {
        return DataTypeDefinitionEnum.class.getEnumConstants();
    }
}