package in.edu.kristujayanti.util;

import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.propertyBinder.PropertyBinderUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

public class ExcelUploadUtil extends MongoDataAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUploadUtil.class);

    // Private constructor to prevent instantiation
    private ExcelUploadUtil() {
    }

    /**
     * Retrieves the indices of the header fields in the Excel sheet.
     *
     * @param sheet          The Excel sheet.
     * @param expectedFields The list of expected fields.
     * @return A map of field names to their column indices.
     */
    private static Map<String, Integer> getHeaderIndices(Sheet sheet, List<String> expectedFields) {
        Map<String, Integer> headerIndices = new HashMap<>(); // Map to store field names and their column indices
        Row headerRow = sheet.getRow(0); // Get the header row (first row)

        for (Cell cell : headerRow) { // Iterate through each cell in the header row
            String label = cell.getStringCellValue().trim(); // Get the cell value and trim whitespace
            String propertyName = PropertyBinderUtils.getPropertyNameByDisplayName(label); // Get the property name by display name

            // Check if the property name is expected and add it to the map
            if (propertyName != null && expectedFields.contains(propertyName)) {
                headerIndices.put(propertyName, cell.getColumnIndex());
            } else if (expectedFields.contains(label)) { // If the label itself is an expected field, add it to the map
                headerIndices.put(label, cell.getColumnIndex());
            }
        }
        return headerIndices; // Return the map of field names and their column indices
    }

    /**
     * Retrieves the value of a cell as an Object.
     *
     * @param cell The cell.
     * @return The value of the cell.
     */
    private static Object getCellValue(Cell cell) {
        String cellValue = getCellStringValue(cell); // Get the cell value as a string
        return (cellValue == null || cellValue.isEmpty()) ? null : cellValue; // Return null if the value is empty, otherwise return the value
    }

    /**
     * Retrieves the string value of a cell.
     *
     * @param cell The cell.
     * @return The string value of the cell.
     */
    private static String getCellStringValue(Cell cell) {
        if (cell == null) { // Check if the cell is null
            return null; // Return null if the cell is null
        }

        // Switch case to handle different cell types
        return switch (cell.getCellType()) {
            case NUMERIC->
                    DateUtil.isCellDateFormatted(cell) ? String.valueOf(cell.getDateCellValue().getTime()) : BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
            // If the cell is numeric, check if it is a date and return the appropriate value
            case BOOLEAN ->
                    String.valueOf(cell.getBooleanCellValue()); // If the cell is boolean, return the boolean value as a string
            case FORMULA -> cell.getCellFormula(); // If the cell is a formula, return the formula
            default -> cell.getStringCellValue().trim(); // Default case, return the cell value as a trimmed string
        };
    }

    /**
     * Parses an Excel file and returns a list of documents.
     *
     * @param fileData       The buffer containing the Excel file data.
     * @param requiredFields The list of required fields.
     * @param expectedFields The list of expected fields.
     * @return A list of documents parsed from the Excel file.
     * @throws IOException              If an I/O error occurs during reading the Excel file.
     * @throws IllegalArgumentException If the Excel file is missing required fields or contains invalid data.
     */
//    public static List<Document> parseExcelFile(Buffer fileData, List<String> requiredFields, List<String> expectedFields) throws IOException, IllegalArgumentException {
//        List<Document> documents = new ArrayList<>(); // List to store the parsed documents
//
//        try (InputStream inputStream = new ByteArrayInputStream(fileData.getBytes())) {
//
//            Workbook workbook = new XSSFWorkbook(inputStream);
//
//            LOGGER.info("Starting to parse the Excel file."); // Log the start of file parsing
//            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet from the workbook
//            Map<String, Integer> headerIndices = getHeaderIndices(sheet, expectedFields); // Get the header indices
//
//            // Validate that all required fields are present in the header
//            for (String requiredField : requiredFields) {
//                if (!headerIndices.containsKey(requiredField)) {
//                    String errorMessage = "Required field missing in file: " + requiredField;
//                    LOGGER.error(errorMessage); // Log the missing required field
//                    throw new IllegalArgumentException(errorMessage); // Throw an exception for the missing required field
//                }
//            }
//
//            // Iterate through each row starting from the second row (index 1)
//            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
//                Row row = sheet.getRow(rowIndex); // Get the current row
//                if (row == null) {
//                    continue; // Skip empty rows
//                }
//
//                Document document = new Document(); // Create a new document for each row
//                for (Map.Entry<String, Integer> entry : headerIndices.entrySet()) { // Iterate through the header indices
//                    String header = entry.getKey(); // Get the header field name
//                    int columnIndex = entry.getValue(); // Get the column index for the header
//                    Cell cell = row.getCell(columnIndex); // Get the cell at the column index
//                    Object cellValue = getCellValue(cell);
//                    if (cellValue != null) {
//                        document.append(header, getCellValue(cell)); // Append the cell value to the document
//                    }
//                }
//
//                if (document.isEmpty()) {
//                    continue;
//                }
//
//                // Validate and clean the document
//                JsonArray validationResponse = DocumentParser.validateAndCleanDocument(document, requiredFields);
//                if (!validationResponse.isEmpty()) {
//                    String errorMessage = String.format("Error in fields: <%s> for record: <%s>.", validationResponse, document);
//                    LOGGER.error(errorMessage); // Log the validation error
//                    throw new IllegalArgumentException(errorMessage); // Throw an exception for the validation error
//                }
//
//                documents.add(document); // Add the document to the list
//                LOGGER.info("Parsed document: {}", document); // Log the parsed document
//            }
//
//            LOGGER.info("Completed parsing the Excel file."); // Log the completion of file parsing
//        }
//
//        return documents; // Return the list of parsed documents
//    }

    public static List<Document> parseExcelFile(Buffer fileData, List<String> requiredFields, List<String> expectedFields, List<String> uniqueFields) throws IOException, IllegalArgumentException {
        List<Document> documents = new ArrayList<>(); // List to store the parsed documents
        Set<String> uniqueValues = new HashSet<>(); // Set to track unique values for specified fields

        try (InputStream inputStream = new ByteArrayInputStream(fileData.getBytes())) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            LOGGER.info("Starting to parse the Excel file."); // Log the start of file parsing
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet from the workbook
            Map<String, Integer> headerIndices = getHeaderIndices(sheet, expectedFields); // Get the header indices

            // Validate that all required fields are present in the header
            for (String requiredField : requiredFields) {
                if (!headerIndices.containsKey(requiredField)) {
                    String errorMessage = "Required field missing in file: " + requiredField;
                    LOGGER.error(errorMessage); // Log the missing required field
                    throw new IllegalArgumentException(errorMessage); // Throw an exception for the missing required field
                }
            }

            // Iterate through each row starting from the second row (index 1)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex); // Get the current row
                if (row == null) {
                    continue; // Skip empty rows
                }

                Document document = new Document(); // Create a new document for each row
                for (Map.Entry<String, Integer> entry : headerIndices.entrySet()) { // Iterate through the header indices
                    String header = entry.getKey(); // Get the header field name
                    int columnIndex = entry.getValue(); // Get the column index for the header
                    Cell cell = row.getCell(columnIndex); // Get the cell at the column index
                    Object cellValue = getCellValue(cell);
                    if (cellValue != null) {
                        document.append(header, getCellValue(cell)); // Append the cell value to the document
                    }
                }

                if (document.isEmpty()) {
                    continue;
                }

                // Validate and clean the document
             LOGGER.info("document {}", document);
                JsonArray validationResponse = DocumentParser.validateAndCleanDocument(document, requiredFields);
                if (!validationResponse.isEmpty()) {
                    String errorMessage = String.format("Error in fields: <%s> for record: <%s>.", validationResponse, document);
                    LOGGER.error(errorMessage); // Log the validation error
                    throw new IllegalArgumentException(errorMessage); // Throw an exception for the validation error
                }

                // Check for duplicate values in specified unique fields
                if (uniqueFields != null && !uniqueFields.isEmpty()) {
                    for (String uniqueField : uniqueFields) {
                        if (document.containsKey(uniqueField)) {
                            String uniqueValue = document.getString(uniqueField);
                            if (uniqueValues.contains(uniqueValue)) {
                                String errorMessage = String.format("Duplicate value found in field %s: %s", PropertyBinderUtils.getDisplayNameByPropertyName(uniqueField) , uniqueValue);
                                LOGGER.error(errorMessage);
                                throw new IllegalArgumentException(errorMessage);
                            }
                            uniqueValues.add(uniqueValue);
                        }
                    }
                }

                documents.add(document); // Add the document to the list
                LOGGER.info("Parsed document: {}", document); // Log the parsed document
            }

            LOGGER.info("Completed parsing the Excel file."); // Log the completion of file parsing
        }

        return documents; // Return the list of parsed documents
    }

    public static List<Document> parseExcelFile(Buffer fileData, List<String> requiredFields, List<String> expectedFields) throws IOException, IllegalArgumentException {
        return parseExcelFile(fileData, requiredFields, expectedFields, null);
    }

    public static List<Document> parseExcelFileForUpload(Buffer fileData, List<String> requiredFields,  List<String> uniqueFields) throws IOException, IllegalArgumentException {
        List<Document> documents = new ArrayList<>(); // List to store the parsed documents
        Set<String> uniqueValues = new HashSet<>(); // Set to track unique values for specified fields

        try (InputStream inputStream = new ByteArrayInputStream(fileData.getBytes())) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            LOGGER.info("Starting to parse the Excel file for upload."); // Log the start of file parsing
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet from the workbook
            Map<String, Document> headerTemplate = getHeaderIndicesWithSubHeaders(sheet); // Get the header indices with sub-headers


            // Iterate through each row starting from the second row (index 2) to skip headers
            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex); // Get the current row
                if (row == null) {
                    continue; // Skip empty rows
                }

                Document document = new Document(); // Create a new document for each row
                for (Map.Entry<String, Document> mainHeaderEntry : headerTemplate.entrySet()) {
                    String mainHeader = mainHeaderEntry.getKey();
                    Document subHeaders = mainHeaderEntry.getValue();

                    if (subHeaders == null || subHeaders.isEmpty()) {
                        // Independent field (no sub-headers, so just store it as a main field)
                        Integer columnIndex = subHeaders.getInteger(mainHeader);
                        if (columnIndex != null) {
                            Cell cell = row.getCell(columnIndex);
                            Object cellValue = getCellValue(cell);
                            if (cellValue != null) {
                                document.append(mainHeader, cellValue); // Append the cell value to the document
                            }
                        }
                    } else {
                        // Nested field (handle sub-headers)
                        Document nestedDocument = new Document();
                        for (String subHeader : subHeaders.keySet()) {
                            Integer colIndex = subHeaders.getInteger(subHeader);
                            if (colIndex != null) {
                                Cell subHeaderCell = row.getCell(colIndex);
                                Object subHeaderValue = getCellValue(subHeaderCell);
                                if (subHeaderValue != null) {
                                    nestedDocument.append(subHeader, subHeaderValue); // Append the subheader value to the nested document
                                }
                            }
                        }
                        if (!nestedDocument.isEmpty()) {
                            document.append(mainHeader, nestedDocument); // Add the nested document under the main header
                        }
                    }
                }

                if (document.isEmpty()) {
                    continue; // Skip empty documents
                }

                // Retrieve the dynamic main headers (e.g., from getHeaderIndicesWithSubHeaders or similar logic

// Prepare a new document containing only sub-headers (excluding the dynamically determined main headers)
                Document validationDocument = new Document(); // New document for validation

// Iterate over the keys of the original document to filter out the dynamically determined main headers
                for (String key : document.keySet()) {
                    // Skip the keys that match any dynamically identified main headers
                    if (headerTemplate.containsKey(key)) {
                        Document subHeaders = headerTemplate.get(key); // Get the sub-headers for this main header

                        if (subHeaders != null && !subHeaders.isEmpty()) {
                            // If the value for the key in the document is a nested document (sub-headers)
                            Object value = document.get(key);
                            if (value instanceof Document) {
                                Document nestedDocument = (Document) value;

                                // Iterate through the nested document's keys (sub-headers) and their values
                                for (String subHeaderKey : nestedDocument.keySet()) {
                                    // Append the sub-header key-value pair to the validation document
                                    validationDocument.append(subHeaderKey, nestedDocument.get(subHeaderKey));
                                }
                            }
                        }
                    }
                }

// Now validationDocument contains only the sub-header fields and their values, excluding the main header keys
                JsonArray validationResponse = DocumentParser.validateAndCleanDocument(validationDocument, requiredFields);

                if (validationResponse != null && !validationResponse.isEmpty()) {
                    String errorMessage = String.format("Error in fields: <%s> for record: <%s>.", validationResponse, validationDocument);
                    LOGGER.error(errorMessage); // Log the validation error
                    throw new IllegalArgumentException(errorMessage); // Throw an exception for the validation error
                }


                // Check for duplicate values in specified unique fields
                if (uniqueFields != null && !uniqueFields.isEmpty()) {
                    for (String uniqueField : uniqueFields) {
                        if (document.containsKey(uniqueField)) {
                            String uniqueValue = document.getString(uniqueField);
                            if (uniqueValues.contains(uniqueValue)) {
                                String errorMessage = String.format("Duplicate value found in field %s: %s", PropertyBinderUtils.getDisplayNameByPropertyName(uniqueField), uniqueValue);
                                LOGGER.error(errorMessage); // Log the error
                                throw new IllegalArgumentException(errorMessage); // Throw an exception for the duplicate value
                            }
                            uniqueValues.add(uniqueValue); // Add to the set of unique values
                        }
                    }
                }

                documents.add(document); // Add the document to the list
                LOGGER.info("Parsed document: {}", document); // Log the parsed document
            }

            LOGGER.info("Completed parsing the Excel file for upload."); // Log the completion of file parsing
        }

        return documents; // Return the list of parsed documents
    }

    public static Map<String, Document> getHeaderIndicesWithSubHeaders(Sheet sheet) {

        Map<String, Document> headerIndices = new HashMap<>();

        // First row for main headers
        Row mainHeaderRow = sheet.getRow(0);
        // Second row for sub-headers and independent headers
        Row subHeaderRow = sheet.getRow(1);

        if (mainHeaderRow == null || subHeaderRow == null) {
            throw new IllegalArgumentException("The provided sheet does not have enough rows to determine main and sub-headers.");
        }

        int lastColumn = Math.max(mainHeaderRow.getLastCellNum(), subHeaderRow.getLastCellNum());
        for (int colIndex = 0; colIndex < lastColumn; colIndex++) {
            Cell mainHeaderCell = mainHeaderRow.getCell(colIndex);
            Cell subHeaderCell = subHeaderRow.getCell(colIndex);

            String mainHeader = (mainHeaderCell != null) ? mainHeaderCell.getStringCellValue().trim() : "";
            String subHeader = (subHeaderCell != null) ? subHeaderCell.getStringCellValue().trim() : "";

            // Convert headers using PropertyBinderUtils
            String subHeaderProperty = !subHeader.isEmpty() ? PropertyBinderUtils.getPropertyNameByDisplayName(subHeader) : "";

            if (!mainHeader.isEmpty()) {
                // Main header exists, handle subheaders
                headerIndices.putIfAbsent(mainHeader, new Document());

                if (!subHeaderProperty.isEmpty()) {
                    // Add sub-header under the main header
                    headerIndices.get(mainHeader).append(subHeaderProperty, colIndex);

                    // Log the subheader
                    LOGGER.info("Mapped Subheader: '{}' (column {}) under Main Header: '{}'", subHeader, colIndex, mainHeader);
                }
            } else if (!subHeaderProperty.isEmpty()) {
                // Independent header (no main header above)
                headerIndices.putIfAbsent("independentHeaders", new Document());
                headerIndices.get("independentHeaders").append(subHeaderProperty, colIndex);

                // Log the independent header
                LOGGER.info("Mapped Independent Header: '{}' (column {})", subHeader, colIndex);
            }
        }

//        // Validate that all expected fields are present in the parsed sub-headers or independent headers
//        for (String expectedField : expectedFields) {
//            boolean found = headerIndices.values().stream()
//                    .anyMatch(subHeaders -> subHeaders.containsKey(expectedField));
//
//            if (!found) {
//                throw new IllegalArgumentException("Missing expected field: " + expectedField);
//            }
//        }

        return headerIndices;
    }


}
