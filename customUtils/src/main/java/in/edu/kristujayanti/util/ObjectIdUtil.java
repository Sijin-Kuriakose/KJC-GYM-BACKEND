package in.edu.kristujayanti.util;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;

public class ObjectIdUtil {
    /**
     * Convert the ObjectId to a string representation.
     *
     * @param doc the document containing the _id field
     */
    public static void convertObjectIdToString(Document doc) {
        doc.computeIfPresent("_id", (key, val) -> {
            ObjectId objectId = doc.getObjectId("_id");
            return objectId.toHexString();
        });
    }

    /**
     * Convert the ObjectId to a string representation.
     *
     * @param objectId the ObjectId to convert
     */
    public static String convertObjectIdToString(ObjectId objectId) {
        return objectId.toHexString();
    }

    /**
     * Converts {@link List<String>} of ObjectId Hex String to {@link List<ObjectId>}.
     *
     * @param stringObjectIdList The list of ObjectId Hex Strings.
     * @return A list of ObjectId if valid, null if any objectId hex String is invalid.
     */
    public static List<ObjectId> convertStringListToObjectIdList(List<String> stringObjectIdList) {
        try {
            return stringObjectIdList.stream()
                    .map(ObjectId::new) // Convert each Hex String to ObjectId
                    .toList(); // Collect into a List<ObjectId>
        } catch (IllegalArgumentException e) {
            // Return null if any ObjectId Hex String is invalid
            return null;
        }
    }

    /**
     * Converts a {@link List<ObjectId>} to a {@link List<String>} of their hex string representations.
     *
     * @param objectIdList The list of ObjectIds.
     * @return A list of ObjectId hex strings, or an empty list if the input list is null or contains any invalid ObjectId.
     */
    public static List<String> convertObjectIdListToStringList(List<ObjectId> objectIdList) {
        if (objectIdList == null) {
            return null; // Return null if input is null
        }

        return objectIdList.stream()
                .filter(Objects::nonNull) // Filter out any null ObjectIds
                .map(ObjectId::toHexString) // Convert each ObjectId to Hex String
                .toList(); // Collect into a List<String>
    }

    /**
     * Convert the ObjectId Hex String to ObjectId.
     *
     * @param objectIdString the ObjectId hex String to convert
     */
    public static ObjectId convertStringToObjectId(String objectIdString) {
        return new ObjectId(objectIdString);
    }
}