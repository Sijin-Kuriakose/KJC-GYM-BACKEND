package in.edu.kristujayanti.util;

import in.edu.kristujayanti.enums.DataTypeDefinitionEnum;
import in.edu.kristujayanti.enums.UIPropertyKey;
import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import in.edu.kristujayanti.propertyBinder.PropertyEnumRegistry;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

public class DocumentParser {
    private static final Logger LOGGER = LogManager.getLogger(DocumentParser.class);

    public DocumentParser() {
    }

    public static Document parseToDocument(JsonObject jsonObject) {
        return Document.parse(jsonObject.toString());
    }

    public static JsonArray validateAndCleanDocument(Document document, List<String> nonEmptyProperties) {
        Set<String> nonEmptyPropertySet = nonEmptyProperties != null ? new HashSet<>(nonEmptyProperties) : new HashSet<>();
        return validateAndCleanDocumentRecursion(document, nonEmptyPropertySet, -1, nonEmptyProperties != null);
    }

    private static JsonArray validateAndCleanDocumentRecursion(Document document, Set<String> nonEmptyPropertySet, int index, boolean checkMandatoryFields) {
        Map<String, Set<String>> nonEmptyPropertyMap = new LinkedHashMap<>();
        Set<String> nonEmptyPropertyStringSet = new HashSet<>();
        initializeNonEmptyPropertyCollections(nonEmptyPropertySet, nonEmptyPropertyMap, nonEmptyPropertyStringSet);
        JsonArray validationResponseArray = new JsonArray();
        Iterator<Map.Entry<String, Object>> keyIterator = document.entrySet().iterator();

        while(keyIterator.hasNext()) {
            Map.Entry<String, Object> iteratorObject = keyIterator.next();
            String key = iteratorObject.getKey();
            Object value = iteratorObject.getValue();
            boolean isRemovedProperty = false;
            JsonArray validationMessageArray = new JsonArray();

            if (value == null) {
                keyIterator.remove();
                isRemovedProperty = true;
            } else if (value instanceof String) {
                String stringValue = (String)value;
                if (!stringValue.trim().isEmpty() && !stringValue.trim().isBlank()) {
                    document.replace(key, stringValue.trim());
                } else {
                    keyIterator.remove();
                    isRemovedProperty = true;
                }
            } // In validateAndCleanDocumentRecursion method
            else if (value instanceof List) {
                List<Object> objectList = document.getList(key, Object.class);
                if (!objectList.isEmpty()) {
                    isRemovedProperty = validateInstanceOfList(objectList, keyIterator, nonEmptyPropertyMap.getOrDefault(key, Set.of()), validationMessageArray, checkMandatoryFields);
                } else {
                    keyIterator.remove();
                    isRemovedProperty = true;
                }
            }// In validateAndCleanDocumentRecursion method
            else if (value instanceof Document) {
                Document innerDocument = (Document)value;
                isRemovedProperty = validateInstanceOfDocument(innerDocument, keyIterator, nonEmptyPropertyMap.getOrDefault(key, Set.of()), validationMessageArray, index, checkMandatoryFields);
            }

            if (checkMandatoryFields) {
                validationMessageArray.addAll(checkNonEmptyPropertyInDocument(document, nonEmptyPropertyStringSet, key));
                nonEmptyPropertyStringSet.remove(key);
            }

            if (!isRemovedProperty) {
                String[] propertySplitWithUnderScore = splitPropertyName(key);
                int dataTypeIndex = 2;
                DataTypeDefinitionEnum dataTypeEnum = DataTypeDefinitionEnum.getEnumByProperty(key, propertySplitWithUnderScore[dataTypeIndex]);

                String validationMessage = dataLengthAndEnumTypeIsValid(value, propertySplitWithUnderScore, key);
                if (validationMessage != null) {
                    validationMessageArray.add(validationMessage);
                }

                validationMessage = validateDataTypeAndUpdateValue(key, value, dataTypeEnum, document);
                if (validationMessage != null) {
                    validationMessageArray.add(validationMessage);
                }
            }

            if (!validationMessageArray.isEmpty()) {
                addValuesToValidationResponseArray(validationMessageArray, validationResponseArray, key, index);
            }
        }

        if (checkMandatoryFields && !nonEmptyPropertyStringSet.isEmpty()) {
            for (String key : nonEmptyPropertyStringSet) {
                JsonArray validationMessageArray = new JsonArray();
                String validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.REQUIRED.getProperty());
                validationMessageArray.add(validationMessage);
                addValuesToValidationResponseArray(validationMessageArray, validationResponseArray, key, index);
            }
        }

        return validationResponseArray;
    }

    private static void addValuesToValidationResponseArray(JsonArray validationMessageArray, JsonArray validationResponseArray, String nonEmptyPropertyKey, int index) {
        if (index != -1) {
            OptionalInt foundIndex = IntStream.range(0, validationResponseArray.size()).filter((i) -> {
                return validationResponseArray.getJsonObject(i).getInteger("index") == index;
            }).findFirst();
            if (foundIndex.isPresent()) {
                int pos = foundIndex.getAsInt();
                JsonObject validationJsonObject = validationResponseArray.getJsonObject(pos);
                validationJsonObject.put(nonEmptyPropertyKey, validationMessageArray);
            } else {
                JsonObject validationResponseObject = new JsonObject();
                validationResponseObject.put("index", index);
                validationResponseObject.put(nonEmptyPropertyKey, validationMessageArray);
                validationResponseArray.add(validationResponseObject);
            }
        } else {
            JsonObject validationResponseObject = new JsonObject();
            validationResponseObject.put(nonEmptyPropertyKey, validationMessageArray);
            validationResponseArray.add(validationResponseObject);
        }

    }

    private static JsonArray checkNonEmptyPropertyInDocument(Document document, Set<String> nonEmptyPropertyStringList, String key) {
        JsonArray validationMessageArray = new JsonArray();
        if (nonEmptyPropertyStringList.contains(key) && !document.containsKey(key)) {
            String validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.REQUIRED.getProperty());
            validationMessageArray.add(validationMessage);
        }

        return validationMessageArray;
    }

    private static boolean validateInstanceOfDocument(Document document, Iterator<Map.Entry<String, Object>> keyIterator, Set<String> nonEmptyPropertyList, JsonArray validationMessageArray, int index, boolean checkMandatoryFields) {
        JsonArray innerValidationResponseArray = validateAndCleanDocumentRecursion(document, nonEmptyPropertyList, index, checkMandatoryFields);
        if (!innerValidationResponseArray.isEmpty()) {
            validationMessageArray.addAll(innerValidationResponseArray);
        }

        // Collect keys to remove instead of removing them directly during iteration
        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            if (entry.getValue() == null || entry.getValue().toString().trim().isEmpty()) {
                keysToRemove.add(entry.getKey());
            }
        }

        // Remove entries after iteration
        keysToRemove.forEach(document::remove);

        // Check if document is empty after removals
        return document.isEmpty();
    }

    private static boolean validateInstanceOfList(List<Object> objectList, Iterator<Map.Entry<String, Object>> keyIterator, Set<String> nonEmptyPropertyList, JsonArray validationMessageArray, boolean checkMandatoryFields) {
        boolean isRemovedProperty = false;
        List<Integer> indicesToRemove = new ArrayList<>();

        for (int i = 0; i < objectList.size(); i++) {
            Object element = objectList.get(i);
            if (element == null) {
                indicesToRemove.add(i);
                isRemovedProperty = true;
            } else if (element instanceof String) {
                String stringValue = (String) element;
                if (stringValue.trim().isEmpty()) {
                    indicesToRemove.add(i);
                    isRemovedProperty = true;
                } else {
                    objectList.set(i, stringValue.trim()); // Trim and update string values
                }
            } else if (element instanceof Document) {
                Document innerDocument = (Document) element;
                boolean innerRemoved = validateInstanceOfDocument(innerDocument, keyIterator, nonEmptyPropertyList, validationMessageArray, i, checkMandatoryFields);
                if (innerRemoved) {
                    indicesToRemove.add(i);
                    isRemovedProperty = true;
                }
            }
        }

        // Remove elements from list starting from the end to avoid index shifting issues
        Collections.reverse(indicesToRemove); // Reverse to ensure correct order of removal
        indicesToRemove.forEach(index -> objectList.remove((int)index));

        return isRemovedProperty || objectList.isEmpty();
    }


    private static void initializeNonEmptyPropertyCollections(Set<String> nonEmptyPropertySet, Map<String, Set<String>> nonEmptyPropertyMap, Set<String> nonEmptyPropertyStringSet) {
        nonEmptyPropertySet.forEach((nonEmptyProperty) -> {
            int firstIndexOfDot = nonEmptyProperty.indexOf(".");
            if (firstIndexOfDot == -1) {
                nonEmptyPropertyStringSet.add(nonEmptyProperty);
            } else {
                String keyToAddTo = nonEmptyProperty.substring(0, firstIndexOfDot);
                String valueToAddTo = nonEmptyProperty.substring(firstIndexOfDot + 1);
                ((Set)nonEmptyPropertyMap.computeIfAbsent(keyToAddTo, (k) -> {
                    return new HashSet();
                })).add(valueToAddTo);
            }

        });
    }

    public static String[] splitPropertyName(String propertyName) {
        String[] segments = propertyName.split("_");
        int length = segments.length;
        if (length != 3 && length != 4) {
            throw new IllegalArgumentException("Property name " + propertyName + " is invalid");
        } else {
            String[] var3 = segments;
            int var4 = segments.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String propertySegment = var3[var5];
                if (propertySegment.trim().isEmpty()) {
                    throw new IllegalArgumentException("One of the split part is empty in property name " + propertyName);
                }
            }

            return segments;
        }
    }

    private static String validateDataTypeAndUpdateValue(String propertyName, Object value, DataTypeDefinitionEnum dataTypeEnum, Document document) {
        String validationMessage = null;
        List listValues;
        switch (dataTypeEnum) {
            case BOOL:
                if (!DataTypeValidatorUtils.isBoolean(String.valueOf(value))) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.BOOLEANFIELD.getProperty());
                } else {
                    document.put(propertyName, DataTypeValidatorUtils.toBoolean(value.toString()));
                }
                break;
            case CURRENCY:
                try {
                    double numericValue;

                    // Convert the value to a double if it's not already
                    if (!(value instanceof Double)) {
                        numericValue = DataTypeValidatorUtils.getDoubleValue(value.toString());
                    } else {
                        numericValue = (Double) value;
                    }

                    // Format the value for consistent two-decimal-place representation
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    String formattedValue = decimalFormat.format(numericValue);

                    // Log the formatted value for debugging or display purposes
                    LOGGER.info("Formatted currency value: {}", formattedValue);

                    // Store the numeric value back in the document as a Double
                    document.put(propertyName, formattedValue);
                } catch (Exception var15) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.NUMERICFIELD.getProperty());
                }
                break;



            case DOUBLE:
            case PERCENTAGE:
                try {
                    if (!(value instanceof Double)) {
                        document.put(propertyName, DataTypeValidatorUtils.getDoubleValue(value.toString()));
                    }
                } catch (Exception var15) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.NUMERICFIELD.getProperty());
                }
                break;

            case DATE:
                try {
                    Long dtInMillis = DateUtils.getStartOfDayMillis(value.toString());
                    if(dtInMillis == null){
                        throw new IllegalArgumentException("Date format is invalid");
                    }
                    document.put(propertyName, dtInMillis);
                } catch (Exception var13) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.DATEFORMAT.getProperty());
                }
                break;
            case ENDDATE:
                try {
                    Long dtInMillis = DateUtils.getEndOfDayMillis(value.toString());
                    if(dtInMillis == null){
                        throw new IllegalArgumentException("Date format is invalid");
                    }
                    document.put(propertyName, dtInMillis);
                } catch (Exception var13) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.DATEFORMAT.getProperty());
                }
                break;
            case STARTDATE:
                try {
                    Long dtInMillis = DateUtils.getStartOfDayMillis(value.toString());
                    if(dtInMillis == null){
                        throw new IllegalArgumentException("Date format is invalid");
                    }
                    document.put(propertyName, dtInMillis);
                } catch (Exception var13) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.DATEFORMAT.getProperty());
                }
                break;
            case TIME:
                try {
                    Long timeInMillis = DateUtils.convertTimeToMillis(value.toString());
                    document.put(propertyName, timeInMillis);
                } catch (Exception var13) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.TIME.getProperty());
                }
                break;
            case INT:
                try {
                    document.put(propertyName, DataTypeValidatorUtils.getIntegerValue(value.toString()));
                } catch (Exception var11) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.NUMERICFIELD.getProperty());
                }
                break;
            case LONG:
                try {
                    document.put(propertyName, DataTypeValidatorUtils.getLongValue(value.toString()));
                } catch (Exception var9) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.NUMERICFIELD.getProperty());
                }
                break;
            case TEXT, FILE, IMAGE:
                if (!(value instanceof String)) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.STRING.getProperty());
                } else {
                    String stringValue = (String) value;
                    String[] propertySplitWithUnderScore = splitPropertyName(propertyName);
                    String fieldName = propertySplitWithUnderScore[0];

                    UppercasePolicyManager.UppercasePolicy policy = UppercasePolicyManager.getPolicy(fieldName);
                    if (policy == UppercasePolicyManager.UppercasePolicy.UPPERCASE) {
                        stringValue = stringValue.toUpperCase();
                    }
                    document.put(propertyName, stringValue);
                }
                break;

            case DATETIME:
                try {
                    document.put(propertyName, DateUtils.convertDateTimeStringToMillis(value.toString()));
                } catch (Exception var7) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.DATETIME.getProperty());
                }
                break;
            case TEXTARRAY:
                if (!(value instanceof List)) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.NOT_LIST.getProperty());
                } else {
                    Stream var10000 = ((List)value).stream();
                    Objects.requireNonNull(String.class);
                    if (!var10000.allMatch(String.class::isInstance)) {
                        validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.TEXTARRAY.getProperty());
                    }
                }
                break;
            case DOCUMENT:
                if (!(value instanceof Document)) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.DOCUMENT.getProperty());
                }
                break;

            case DOCUMENTARRAY:
                if (!(value instanceof List)) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.NOT_LIST.getProperty());
                } else {
                    listValues = (List)value;
                    document.put(propertyName, listValues);
                }
                break;


            case INTARRAY:
                if (!(value instanceof List)) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.NOT_LIST.getProperty());
                } else {
                    listValues = (List)value;
                    if (!listValues.stream().allMatch(item -> item instanceof Integer)) {
                        validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.INTARRAY.getProperty());
                    } else {
                        document.put(propertyName, listValues);
                    }
                }
                break;

            case OBJECTID:
                if (!(value instanceof String stringValue)) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.STRING.getProperty());
                } else {
                    document.put(propertyName, new ObjectId(stringValue));
                }
                break;

            case OBJECTIDARRAY:
                if (!(value instanceof List)) {
                    validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.NOT_LIST.getProperty());
                } else {
                    Stream var10000 = ((List)value).stream();
                    Objects.requireNonNull(String.class);
                    if (!var10000.allMatch(String.class::isInstance)) {
                        validationMessage = UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.TEXTARRAY.getProperty());
                    } else {
                        document.put(propertyName, ObjectIdUtil.convertStringListToObjectIdList((List) value));
                    }
                }
                break;

            default:
                String var10002 = dataTypeEnum.getDataTypeDefinitionClass().getSimpleName();
                throw new IllegalArgumentException("The class " + var10002 + " is invalid for data type enum " + dataTypeEnum.name());
        }

        return validationMessage;
    }

    private static String dataLengthAndEnumTypeIsValid(Object value, String[] propertySplitWithUnderScore, String propertyName) {
        String enumValue = getEnumValue(propertySplitWithUnderScore[0]);
        int enumIndex =  1;

        Class enumClass = PropertyEnumRegistry.getEnumClass(propertySplitWithUnderScore[enumIndex]);


        try {
            KJUSYSPropertyBinder kjusysPropertyBinder = (KJUSYSPropertyBinder) Enum.valueOf(enumClass, enumValue);
            if (kjusysPropertyBinder.getMinLength() != -1 && kjusysPropertyBinder.getMaxLength() != -1) {
                if (DataTypeDefinitionEnum.TEXTARRAY.name().equals(enumValue)) {
                    List<String> listValues = (List)value;
                    if (listValues.stream().anyMatch((stringValue) -> {
                        return stringValue.length() < kjusysPropertyBinder.getMinLength() || stringValue.length() > kjusysPropertyBinder.getMaxLength();
                    })) {
                        Integer var10000 = kjusysPropertyBinder.getMinLength();
                        return UIPropertyKeyMap.getFormValidationPropertyKey("" + var10000 + "-" + kjusysPropertyBinder.getMaxLength());
                    }
                } else {
                    int length = value.toString().length();
                    if (length < kjusysPropertyBinder.getMinLength() || length > kjusysPropertyBinder.getMaxLength()) {
                        return UIPropertyKeyMap.getFormValidationPropertyKey(kjusysPropertyBinder.getMinLength().toString().concat("-").concat(kjusysPropertyBinder.getMaxLength().toString()));
                    }
                }
            }

            if (kjusysPropertyBinder.getDataType().isEnum()) {
                try {
                    if (!kjusysPropertyBinder.getDataType().getField(value.toString()).isEnumConstant()) {
                        return UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.ENUM.getProperty());
                    }
                } catch (NoSuchFieldException var8) {
                    return UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.ENUM.getProperty());
                }
            }

            return null;
        } catch (IllegalArgumentException var9) {
            return UIPropertyKeyMap.getFormValidationPropertyKey(UIPropertyKey.INVALID.getProperty());
        }
    }
    private static String getEnumValue(String propertyPrefix) {
        StringBuilder result = new StringBuilder();
        boolean wasPrevCharUpper = false; // Flag to track if the previous character was uppercase

        for (int i = 0; i < propertyPrefix.length(); i++) {
            char c = propertyPrefix.charAt(i);

            // Check if the current character is uppercase
            boolean isCurrentCharUpper = Character.isUpperCase(c);
            boolean isCurrentCharDigit = Character.isDigit(c);

            if (i > 0) {
                // If the current character is uppercase and the previous character was lowercase
                // or if the current character is a digit and the previous character was not a digit,
                // add an underscore to separate words
                if ((isCurrentCharUpper && !wasPrevCharUpper) ||
                        (isCurrentCharDigit && !Character.isDigit(propertyPrefix.charAt(i - 1)))) {
                    result.append('_');
                }
            }

            // Append the current character, converting it to uppercase
            result.append(Character.toUpperCase(c));

            // Update the flag for the next iteration
            wasPrevCharUpper = isCurrentCharUpper;
        }

        return result.toString();
    }







}