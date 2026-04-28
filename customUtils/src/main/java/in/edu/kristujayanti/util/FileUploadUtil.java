package in.edu.kristujayanti.util;

import in.edu.kristujayanti.propertyBinder.KJUSYSCommonKeysPBinder.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.nio.file.*;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;


public final class FileUploadUtil {
    private static final Integer pdfUploadMaximumSize = 500;
    private static final Integer imgUploadMaximumSize = 250;
    private static final Integer maxUploadSizeKB = 5120;
    private static final String PDF_EXTENSION = ".pdf";
    private static final List<String> IMG_EXTENSIONS = Arrays.asList(".jpg", ".jpeg");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg",".jpeg",".png",".pdf");

    public static void deleteFile(String filePath) throws Exception {
        try {
            Path target = Paths.get(filePath);
            Files.delete(target);
        } catch (Exception e) {
            throw e;
        }
    }

    public static JsonObject uploadFile(List<String> propertyNames, String uploadPath, RoutingContext routingContext, String fileName) throws Exception {
        JsonObject uploadType = new JsonObject();
        Path directory = Path.of(uploadPath);
        if (!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectories(directory);
        }

        for (FileUpload fileUpload : routingContext.fileUploads()) {
            for (String uploadProperty : propertyNames) {
                if (fileUpload.name().equals(uploadProperty)) {
                    String uploadedFile = fileUpload.uploadedFileName();
                    Path source = Paths.get(uploadedFile);

                    // Get the file extension from the original filename
                    String originalFileName = fileUpload.fileName();
                    String fileExtension = "";
                    int lastDotIndex = originalFileName.lastIndexOf('.');
                    if (lastDotIndex > 0) {
                        fileExtension = originalFileName.substring(lastDotIndex);
                    }

                    // Construct the new filename using the provided fileName parameter and the original extension
                    String newFileName = fileName + fileExtension;
                    String uploadedFileName = Paths.get(uploadPath, newFileName).toString();
                    Path target = Paths.get(uploadedFileName);

                    try {
                        if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                            Files.delete(target);
                        }
                        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                        uploadType.put(uploadProperty, uploadedFileName);
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }
        }
        return uploadType;
    }

//    public static JsonObject uploadFile(String fileBufferName, String originalFileName, String uploadPath, String fileName) throws Exception {
//        JsonObject uploadType = new JsonObject();
//        Path directory = Path.of(uploadPath);
//
//        // Check if the directory exists; if not, create it
//        if (!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
//            Files.createDirectories(directory);
//        }
//
//        // Extract the file extension from the original file name
//        String fileExtension = "";
//        int lastDotIndex = originalFileName.lastIndexOf('.');
//        if (lastDotIndex > 0) {
//            fileExtension = originalFileName.substring(lastDotIndex);
//        }
//
//        Path source = Paths.get(fileBufferName);
//        String newFileName = fileName + fileExtension;
//        String uploadedFileName = Paths.get(uploadPath, newFileName).toString();
//        Path target = Paths.get(uploadedFileName);
//
//        try {
//            if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
//                Files.delete(target);
//            }
//
//            // Write the file data to the target location
//            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
//
//            uploadType.put(fileName, uploadedFileName);
//        } catch (Exception e) {
//            throw e;
//        }
//
//        return uploadType;
//    }

    public static JsonObject uploadFile(String fileBufferName, String originalFileName, String uploadPath, String fileName) throws Exception {
        JsonObject uploadType = new JsonObject();
        Path directory = Path.of(uploadPath);

        if (!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectories(directory);
        }

        String fileExtension = "";
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileExtension = originalFileName.substring(lastDotIndex);
        }

        Path source = Paths.get(fileBufferName);
        String newFileName = fileName + fileExtension;
        String uploadedFileName = Paths.get(uploadPath, newFileName).toString();
        Path target = Paths.get(uploadedFileName);

        try {
            if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(target);
            }

            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            uploadType.put(fileName, uploadedFileName);
            return uploadType;
        } catch (Exception e) {
            // Log the error and rethrow
            throw e;
        } finally {
            // Cleanup buffer file if it still exists
            if (Files.exists(source)) {
                Files.delete(source);
            }
        }
    }


    private static boolean isValidFileExtension(String fileName) {
        return fileName.toLowerCase().endsWith(PDF_EXTENSION);
    }

    private static boolean isValidFileExtensionImg(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return IMG_EXTENSIONS.stream().anyMatch(lowerCaseFileName::endsWith);
    }

    public static String validateFileSizeAndExtension(String fileName, long fileSize) {
        if (!isValidFileExtension(fileName)) {
            return "File extension must be .pdf";
        }
        double fileSizeInKB = fileSize / 1024.0;
        if (fileSizeInKB > pdfUploadMaximumSize) {
            return "Uploaded PDF exceeds maximum file size limit of " + pdfUploadMaximumSize + " KB";
        }
        return null;
    }

    public static String validateFileSizeAndExtensionImg(String fileName, long fileSize) {
        if (!isValidFileExtensionImg(fileName)) {
            return "File extension must be .jpg";
        }
        double fileSizeInKB = fileSize / 1024.0;
        if (fileSizeInKB > imgUploadMaximumSize) {
            return "Uploaded Image exceeds maximum file size limit of " + imgUploadMaximumSize + " KB";
        }
        return null;
    }


    public static String validateFileSizeAndExtensionImg(String fileName, long fileSize, long maxSize) {
        if (!isValidFileExtensionImg(fileName)) {
            return "File extension must be .jpg";
        }
        double fileSizeInKB = fileSize / 1024.0;
        if (fileSizeInKB > maxSize) {
            return "Uploaded Image exceeds maximum file size limit of " + imgUploadMaximumSize + " KB";
        }
        return null;
    }


//    public static String validateFileSizeAndExtensionImgPdf(String fileName, long fileSize) {
//        if (!isValidFileExtension(fileName)) {
//            return "Invalid file format. Allowed formats are: " +
//                    String.join(", ", ALLOWED_EXTENSIONS).toUpperCase().replace(".", "");
//        }
//
//        double fileSizeInKB = (double)fileSize / 1024.0;
//        double fileSizeInMB = fileSizeInKB / 1024.0;
//
//        if (fileSizeInKB > maxUploadSizeKB) {
//            return String.format("Uploaded file exceeds maximum file size limit of 5 MB (current size: %.2f MB)", fileSizeInMB);
//        }
//
//        return null; // Validation passed
//    }
public static String validateFileSizeAndExtensionImgPdf(String fileName, long fileSize) {
    // Convert filename to lowercase for case-insensitive comparison
    String lowerFileName = fileName.toLowerCase();

    // Check if any allowed extension matches
    boolean isValidExtension = ALLOWED_EXTENSIONS.stream()
            .anyMatch(lowerFileName::endsWith);

    if (!isValidExtension) {
        return "Invalid file format. Allowed formats are: " +
                String.join(", ", ALLOWED_EXTENSIONS).toUpperCase().replace(".", "");
    }

    // Calculate file size in KB
    double fileSizeInKB = (double)fileSize / 1024.0;

    // Different size limits for PDF and images
    if (lowerFileName.endsWith(".pdf")) {
        if (fileSizeInKB > maxUploadSizeKB) {
            return String.format("Uploaded PDF exceeds maximum file size limit of %d KB", maxUploadSizeKB);
        }
    } else {
        if (fileSizeInKB > maxUploadSizeKB) {
            return String.format("Uploaded image exceeds maximum file size limit of %d KB", maxUploadSizeKB);
        }
    }

    return null; // Validation passed
}

    public static JsonObject validateFileSizeAndExtensionImgAndPdf(String fileName, long fileSize) {
        JsonObject result = new JsonObject();

        if (!isValidFileExtensionForImgAndPdf(fileName)) {
            result.put("status", "error");
            result.put("message", "Invalid file format. Allowed formats are: " +
                    String.join(", ", ALLOWED_EXTENSIONS).toUpperCase().replace(".", ""));
            return result;
        }

        double fileSizeInKB = (double)fileSize / 1024.0;

        if (fileSizeInKB > imgUploadMaximumSize) {
            result.put("status", "error");
            result.put("message", String.format("Uploaded file exceeds maximum file size limit of %d KB (current size: %.2f KB)",
                    imgUploadMaximumSize, fileSizeInKB));
            return result;
        }

        result.put("status", "success");
        return result;
    }

    private static boolean isValidFileExtensionForImgAndPdf(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return ALLOWED_EXTENSIONS.stream()
                .anyMatch(lowerCaseFileName::endsWith);
    }


}