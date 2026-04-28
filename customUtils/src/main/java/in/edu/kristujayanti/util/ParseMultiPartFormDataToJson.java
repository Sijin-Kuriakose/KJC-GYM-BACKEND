package in.edu.kristujayanti.util;

import in.edu.kristujayanti.enums.DataTypeDefinitionEnum;
import in.edu.kristujayanti.util.DocumentParser;
import io.vertx.core.MultiMap;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Iterator;
import java.util.Map;

public class ParseMultiPartFormDataToJson {
    public ParseMultiPartFormDataToJson() {
    }

    public static JsonObject headersToJsonObject(MultiMap headers) {
        JsonObject jsonObject = new JsonObject();
        Iterator<Map.Entry<String, String>> var2 = headers.entries().iterator();

        while (var2.hasNext()) {
            Map.Entry<String, String> entry = var2.next();
            String key = entry.getKey();
            String value = entry.getValue();

            if (value != null) {
                String[] propertySplitWithUnderScore = DocumentParser.splitPropertyName(key);
                int dataTypeIndex = 2;
                DataTypeDefinitionEnum dataTypeEnum = DataTypeDefinitionEnum.getEnumByProperty(key, propertySplitWithUnderScore[dataTypeIndex]);
                try {
                    if (dataTypeEnum == DataTypeDefinitionEnum.DOCUMENTARRAY) {
                        // Attempt to parse as JSON array
                        jsonObject.put(key, new JsonArray(value));
                    } else {
                        // Treat as a regular string
                        jsonObject.put(key, value);
                    }
                } catch (DecodeException e) {
                    // Log error and add the value as a string instead
                    System.err.println("Failed to parse header value as JSON for key: " + key + ", value: " + value);
                    jsonObject.put(key, value);
                }
            } else {
                jsonObject.putNull(key);
            }
        }

        return jsonObject;
    }
}
