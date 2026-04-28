//package in.edu.kristujayanti.util;
//
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.model.Filters;
//import com.mongodb.client.model.Projections;
//import org.bson.Document;
//import org.bson.types.ObjectId;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.StreamSupport;
//
//public class DocumentFieldMapperUtil {
//    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFieldMapperUtil.class);
//
//    /**
//     * Converts a comma-separated string of field values to their corresponding ObjectIds from MongoDB
//     * @param mongoDatabase The MongoDB database instance
//     * @param collection The collection to query
//     * @param fieldName The field name to match against in the collection
//     * @param fieldValues Comma-separated string of field values
//     * @param matchFieldName The field name containing the values to match against
//     * @return List of ObjectIds corresponding to the matched documents
//     */
//    public static List<ObjectId> getObjectIdsForFieldValues(
//            MongoDatabase mongoDatabase,
//            String collection,
//            String fieldName,
//            String fieldValues,
//            String matchFieldName) {
//        try {
//            if (fieldValues == null || fieldValues.trim().isEmpty()) {
//                LOGGER.warn("Empty field values provided for field: {}", fieldName);
//                return Collections.emptyList();
//            }
//
//            List<String> valuesList = Arrays.stream(fieldValues.split(","))
//                    .map(String::trim)
//                    .toList();
//
//            FindIterable<Document> documents = findDocuments(
//                    mongoDatabase,
//                    collection,
//                    Filters.in(matchFieldName, valuesList),
//                    Projections.include("_id", matchFieldName)
//            );
//
//            List<ObjectId> objectIds = StreamSupport.stream(documents.spliterator(), false)
//                    .map(doc -> doc.get("_id", ObjectId.class))
//                    .toList();
//
//            if (objectIds.isEmpty()) {
//                LOGGER.warn("No matching documents found for field: {} with values: {}", fieldName, fieldValues);
//            }
//
//            return objectIds;
//        } catch (Exception e) {
//            LOGGER.error("Error converting field values to ObjectIds for field: {} with values: {}", fieldName, fieldValues, e);
//            return Collections.emptyList();
//        }
//    }
//
//    /**
//     * Converts a single field value to its corresponding ObjectId from MongoDB
//     * @param mongoDatabase The MongoDB database instance
//     * @param collection The collection to query
//     * @param fieldName The field name to match against in the collection
//     * @param fieldValue Single field value to look up
//     * @param matchFieldName The field name containing the value to match against
//     * @return ObjectId of the matched document, or null if not found
//     */
//    public static ObjectId getObjectIdForFieldValue(
//            MongoDatabase mongoDatabase,
//            String collection,
//            String fieldName,
//            String fieldValue,
//            String matchFieldName) {
//        try {
//            if (fieldValue == null || fieldValue.trim().isEmpty()) {
//                LOGGER.warn("Empty field value provided for field: {}", fieldName);
//                return null;
//            }
//
//            FindIterable<Document> documents = findDocuments(
//                    mongoDatabase,
//                    collection,
//                    Filters.eq(matchFieldName, fieldValue.trim()),
//                    Projections.include("_id", matchFieldName)
//            );
//
//            Document document = documents.first();
//            if (document == null) {
//                LOGGER.warn("No matching document found for field: {} with value: {}", fieldName, fieldValue);
//                return null;
//            }
//
//            return document.get("_id", ObjectId.class);
//        } catch (Exception e) {
//            LOGGER.error("Error converting field value to ObjectId for field: {} with value: {}", fieldName, fieldValue, e);
//            return null;
//        }
//    }
//
//    /**
//     * Helper method to find documents in MongoDB
//     */
//    private static FindIterable<Document> findDocuments(
//            MongoDatabase mongoDatabase,
//            String collection,
//            org.bson.conversions.Bson filter,
//            org.bson.conversions.Bson projection) {
//        return mongoDatabase.getCollection(collection).find(filter).projection(projection);
//    }
//}





/// the real one working
package in.edu.kristujayanti.util;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import in.edu.kristujayanti.exception.InvalidMappingException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//public class DocumentFieldMapperUtil {
//    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFieldMapperUtil.class);
//
//    /**
//     * Configuration class for field mapping
//     */
//    public static class FieldMapping {
//        private final String sourceField;
//        private final String collection;
//        private final String lookupField;
//
//        public FieldMapping(String sourceField, String collection, String lookupField) {
//            this.sourceField = sourceField;
//            this.collection = collection;
//            this.lookupField = lookupField;
//        }
//
//        public String getSourceField() { return sourceField; }
//        public String getCollection() { return collection; }
//        public String getLookupField() { return lookupField; }
//    }
//
//    /**
//     * Result class for document processing
//     */
//    public static class ProcessingResult {
//        private final List<Document> processedDocuments;
//        private final List<String> errors;
//
//        public ProcessingResult(List<Document> processedDocuments, List<String> errors) {
//            this.processedDocuments = processedDocuments;
//            this.errors = errors;
//        }
//
//        public List<Document> getProcessedDocuments() { return processedDocuments; }
//        public List<String> getErrors() { return errors; }
//        public boolean hasErrors() { return !errors.isEmpty(); }
//    }
//
//    /**
//     * Converts specified fields in multiple documents to their corresponding ObjectIds
//     */
//    public static ProcessingResult convertFieldsToObjectIds(
//            List<Document> documents,
//            List<FieldMapping> fieldMappings,
//            MongoDatabase mongoDatabase) {
//
//        List<Document> processedDocuments = new ArrayList<>();
//        List<String> errors = new ArrayList<>();
//
//        for (Document doc : documents) {
//            try {
//                Document processedDoc = processDocumentFields(doc, fieldMappings, mongoDatabase);
//                if (processedDoc != null) {
//                    processedDocuments.add(processedDoc);
//                }
//            } catch (Exception e) {
//                String errorMsg = String.format("Error processing document: %s. Error: %s",
//                        doc.toString(), e.getMessage());
//                LOGGER.error(errorMsg, e);
//                errors.add(errorMsg);
//            }
//        }
//
//        return new ProcessingResult(processedDocuments, errors);
//    }
//
//    /**
//     * Process a single document's fields
//     */
//    private static Document processDocumentFields(
//            Document doc,
//            List<FieldMapping> fieldMappings,
//            MongoDatabase mongoDatabase) {
//
//        Document processedDoc = new Document(doc);
//        Map<String, String> conversionErrors = new HashMap<>();
//
//        for (FieldMapping mapping : fieldMappings) {
//            String fieldValue = processedDoc.getString(mapping.getSourceField());
//
//            if (fieldValue != null && !fieldValue.trim().isEmpty()) {
//                if (fieldValue.contains(",")) {
//                    // Handle multiple values
//                    List<ObjectId> objectIds = getObjectIdsForFieldValues(
//                            mongoDatabase,
//                            mapping.getCollection(),
//                            mapping.getSourceField(),
//                            fieldValue,
//                            mapping.getLookupField()
//                    );
//                    if (!objectIds.isEmpty()) {
//                        processedDoc.put(mapping.getSourceField(), objectIds);
//                    } else {
//                        conversionErrors.put(mapping.getSourceField(), fieldValue);
//                    }
//                } else {
//                    // Handle single value
//                    ObjectId objectId = getObjectIdForFieldValue(
//                            mongoDatabase,
//                            mapping.getCollection(),
//                            mapping.getSourceField(),
//                            fieldValue,
//                            mapping.getLookupField()
//                    );
//                    if (objectId != null) {
//                        processedDoc.put(mapping.getSourceField(), objectId);
//                    } else {
//                        conversionErrors.put(mapping.getSourceField(), fieldValue);
//                    }
//                }
//            }
//        }
//
//        if (!conversionErrors.isEmpty()) {
//            LOGGER.error("Failed to convert fields: {}", conversionErrors);
//            return null;
//        }
//
//        return processedDoc;
//    }
//
//    // Existing methods remain unchanged for backward compatibility
//    public static List<ObjectId> getObjectIdsForFieldValues(
//            MongoDatabase mongoDatabase,
//            String collection,
//            String fieldName,
//            String fieldValues,
//            String matchFieldName) {
//        try {
//            if (fieldValues == null || fieldValues.trim().isEmpty()) {
//                LOGGER.warn("Empty field values provided for field: {}", fieldName);
//                return Collections.emptyList();
//            }
//
//            List<String> valuesList = Arrays.stream(fieldValues.split(","))
//                    .map(String::trim)
//                    .toList();
//
//            FindIterable<Document> documents = findDocuments(
//                    mongoDatabase,
//                    collection,
//                    Filters.in(matchFieldName, valuesList),
//                    Projections.include("_id", matchFieldName)
//            );
//
//            List<ObjectId> objectIds = StreamSupport.stream(documents.spliterator(), false)
//                    .map(doc -> doc.get("_id", ObjectId.class))
//                    .toList();
//
//            if (objectIds.isEmpty()) {
//                LOGGER.warn("No matching documents found for field: {} with values: {}", fieldName, fieldValues);
//            }
//
//            return objectIds;
//        } catch (Exception e) {
//            LOGGER.error("Error converting field values to ObjectIds for field: {} with values: {}", fieldName, fieldValues, e);
//            return Collections.emptyList();
//        }
//    }
//
//    public static ObjectId getObjectIdForFieldValue(
//            MongoDatabase mongoDatabase,
//            String collection,
//            String fieldName,
//            String fieldValue,
//            String matchFieldName) {
//        try {
//            if (fieldValue == null || fieldValue.trim().isEmpty()) {
//                LOGGER.warn("Empty field value provided for field: {}", fieldName);
//                return null;
//            }
//
//            FindIterable<Document> documents = findDocuments(
//                    mongoDatabase,
//                    collection,
//                    Filters.eq(matchFieldName, fieldValue.trim()),
//                    Projections.include("_id", matchFieldName)
//            );
//
//            Document document = documents.first();
//            if (document == null) {
//                LOGGER.warn("No matching document found for field: {} with value: {}", fieldName, fieldValue);
//                return null;
//            }
//
//            return document.get("_id", ObjectId.class);
//        } catch (Exception e) {
//            LOGGER.error("Error converting field value to ObjectId for field: {} with value: {}", fieldName, fieldValue, e);
//            return null;
//        }
//    }
//
//    private static FindIterable<Document> findDocuments(
//            MongoDatabase mongoDatabase,
//            String collection,
//            org.bson.conversions.Bson filter,
//            org.bson.conversions.Bson projection) {
//        return mongoDatabase.getCollection(collection).find(filter).projection(projection);
//    }
//}





public class DocumentFieldMapperUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFieldMapperUtil.class);

    public DocumentFieldMapperUtil() {
    }

    // Convert fields in documents to ObjectIds using the field mappings
    public static ProcessingResult convertFieldsToObjectIds(List<Document> documents, List<FieldMapping> fieldMappings, MongoDatabase mongoDatabase) {
        List<Document> processedDocuments = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Document doc : documents) {
            try {
                Document processedDoc = processDocumentFields(doc, fieldMappings, mongoDatabase);
                if (processedDoc != null) {
                    processedDocuments.add(processedDoc);
                }
            } catch (Exception e) {
                String errorMsg = String.format("Error processing document: %s. Error: %s", doc.toString(), e.getMessage());
                LOGGER.error(errorMsg, e);
                errors.add(errorMsg);
            }
        }

        return new ProcessingResult(processedDocuments, errors);
    }

    // Process fields based on the mapping provided and update the document
    private static Document processDocumentFields(Document doc, List<FieldMapping> fieldMappings, MongoDatabase mongoDatabase) {
        Document processedDoc = new Document(doc);
        Map<String, String> conversionErrors = new HashMap<>();

        for (FieldMapping mapping : fieldMappings) {
            String fieldValue = processedDoc.getString(mapping.getSourceField());

            if (fieldValue != null && !fieldValue.trim().isEmpty()) {
                // Handle multiple values (comma separated)
                if (fieldValue.contains(",")) {
                    List<ObjectId> objectIds = getObjectIdsForFieldValues(mongoDatabase, mapping.getCollection(), mapping.getSourceField(), fieldValue, mapping.getLookupField());
                    if (!objectIds.isEmpty()) {
                        processedDoc.put(mapping.getTargetField(), objectIds);  // Store in the target field
                    } else {
                        conversionErrors.put(mapping.getSourceField(), fieldValue);
                    }
                } else {
                    // Handle single value
                    ObjectId objectId = getObjectIdForFieldValue(mongoDatabase, mapping.getCollection(), mapping.getSourceField(), fieldValue, mapping.getLookupField());
                    if (objectId != null) {
                        processedDoc.put(mapping.getTargetField(), objectId);  // Store in the target field
                    } else {
                        conversionErrors.put(mapping.getSourceField(), fieldValue);
                    }
                }
            }
        }

        // Return null if there were errors
        if (!conversionErrors.isEmpty()) {
            LOGGER.error("Failed to convert fields: {}", conversionErrors);
            return null;
        } else {
            return processedDoc;
        }
    }

    // Fetch ObjectIds for multiple field values (comma separated)
    public static List<ObjectId> getObjectIdsForFieldValues(MongoDatabase mongoDatabase, String collection, String fieldName, String fieldValues, String matchFieldName) {
        try {
            if (fieldValues != null && !fieldValues.trim().isEmpty()) {
                List<String> valuesList = Arrays.stream(fieldValues.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                FindIterable<Document> documents = findDocuments(mongoDatabase, collection, Filters.in(matchFieldName, valuesList), Projections.include("_id", matchFieldName));
                List<ObjectId> objectIds = StreamSupport.stream(documents.spliterator(), false)
                        .map(doc -> doc.getObjectId("_id"))
                        .collect(Collectors.toList());

                if (objectIds.isEmpty()) {
                    LOGGER.warn("No matching documents found for field: {} with values: {}", fieldName, fieldValues);
                }

                return objectIds;
            } else {
                LOGGER.warn("Empty field values provided for field: {}", fieldName);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            LOGGER.error("Error converting field values to ObjectIds for field: {} with values: {}", fieldName, fieldValues, e);
            return Collections.emptyList();
        }
    }

    // Fetch ObjectId for a single field value
    public static ObjectId getObjectIdForFieldValue(MongoDatabase mongoDatabase, String collection, String fieldName, String fieldValue, String matchFieldName) {

        try {
            if (fieldValue != null && !fieldValue.trim().isEmpty()) {
                // Ensure fieldValue is trimmed and sanitized
                fieldValue = fieldValue.trim();

                // Log to confirm the value we're looking for
                LOGGER.info("Looking for document in collection '{}' where field '{}' matches value '{}'", collection, matchFieldName, fieldValue);

                // Find the matching document in MongoDB based on the matchFieldName (which should be 'bankAccountNumber_FeeModule_Text')
                FindIterable<Document> documents = mongoDatabase.getCollection(collection)
                        .find(Filters.eq(matchFieldName, fieldValue))  // match the bankAccountNumber
                        .projection(Projections.include("_id", matchFieldName));  // include _id to return the ObjectId

                // Fetch the first matching document
                Document document = documents.first();

                // If no matching document is found, log and return null
                if (document == null) {
                    LOGGER.warn("No matching document found in collection '{}' for field '{}' with value '{}'", collection, matchFieldName, fieldValue);
//                    return null;
                    String errorMessage = String.format(
                            "Mapping error: No document found for value '%s' in field '%s'.",
                            fieldValue, matchFieldName
                    );
                    LOGGER.error(errorMessage);
                    throw new InvalidMappingException(errorMessage);

                } else {
                    // Return the correct ObjectId
                    ObjectId objectId = document.getObjectId("_id");
                    LOGGER.info("Found matching document for field '{}' with value '{}', ObjectId: {}", matchFieldName, fieldValue, objectId);
                    return objectId;
                }
            } else {
                LOGGER.warn("Empty or null field value provided for field '{}'", fieldName);
//                return null;
//                throw new InvalidMappingException("Mapping error: Field value is null or empty for field '"
//                        + fieldName + "'.");
                String errorMessage = String.format(
                        "Mapping error: Field value is null or empty for field '%s'.",
                        fieldName
                );
                LOGGER.error(errorMessage);
                throw new InvalidMappingException(errorMessage);
            }
        } catch (InvalidMappingException e) {
            throw e; // custom exception propagate
        } catch (Exception e) {
            LOGGER.error("Error converting field value to ObjectId for field '{}' with value '{}'", fieldName, fieldValue, e);
//            return null;
            String errorMessage = String.format(
                    "Error converting field value to ObjectId for field '%s' with value '%s", fieldName, fieldValue
            );
            throw new InvalidMappingException(errorMessage, e);
        }
    }



    // Helper method to find documents in MongoDB based on filter and projection
    private static FindIterable<Document> findDocuments(MongoDatabase mongoDatabase, String collection, Bson filter, Bson projection) {
        return mongoDatabase.getCollection(collection).find(Document.class).projection(projection);
    }

    // Nested class to hold the result of the processing
    public static class ProcessingResult {
        private final List<Document> processedDocuments;
        private final List<String> errors;

        public ProcessingResult(List<Document> processedDocuments, List<String> errors) {
            this.processedDocuments = processedDocuments;
            this.errors = errors;
        }

        public List<Document> getProcessedDocuments() {
            return processedDocuments;
        }

        public List<String> getErrors() {
            return errors;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }

    // Nested class to define the mapping for field conversions
    public static class FieldMapping {
        private final String sourceField;   // Field in the uploaded document
        private final String collection;    // Collection to query in MongoDB
        private final String lookupField;   // MongoDB field to match the source field
        private final String targetField;   // Field in the document to store the ObjectId

        public FieldMapping(String sourceField, String collection, String lookupField, String targetField) {
            this.sourceField = sourceField;
            this.collection = collection;
            this.lookupField = lookupField;
            this.targetField = targetField;
        }

        public String getSourceField() {
            return sourceField;
        }

        public String getCollection() {
            return collection;
        }

        public String getLookupField() {
            return lookupField;
        }

        public String getTargetField() {
            return targetField;
        }
    }
}
