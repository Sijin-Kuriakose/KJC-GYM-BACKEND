package in.edu.kristujayanti.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import static in.edu.kristujayanti.propertyBinder.Library.LibraryCommonKeysPBinder.LIBRARY_MEMBER_PROFILE_IMAGE_UPLOAD;

//imageFileUploadUtils
public final class ImageFileUploadUtils {
    private static final Integer imageUploadMaximumSize = 100;
    private static final String IMAGE_FILE_EXTENSION = "data:image/jpeg;base64";

    public ImageFileUploadUtils() {
    }

    public static String uploadImage(String imageUploadPath, RoutingContext routingContext) throws Exception {
        Iterator var2 = routingContext.fileUploads().iterator();

        FileUpload fileUpload;
        do {
            if (!var2.hasNext()) {
                return "";
            }

            fileUpload = (FileUpload)var2.next();
        } while(!fileUpload.name().equals(LIBRARY_MEMBER_PROFILE_IMAGE_UPLOAD.getPropertyName()));

        String uploadedFile = fileUpload.uploadedFileName();
        Path source = Paths.get(uploadedFile);
        String uploadedFileName = imageUploadPath + DateUtils.getCurrentTimeInMillis() + fileUpload.fileName();
        Path target = Paths.get(uploadedFileName);

        try {
            Files.move(source, target);
            return uploadedFileName;
        } catch (Exception var9) {
            Exception e = var9;
            throw e;
        }
    }

    public static void deleteImage(String imagePath) throws Exception {
        try {
            Path target = Paths.get(imagePath);
            Files.delete(target);
        } catch (Exception var2) {
            Exception e = var2;
            throw e;
        }
    }

    public static JsonObject uploadImageFile(List<String> propertyNames, String imageUploadPath, RoutingContext routingContext, String fileName) throws Exception {
        JsonObject uploadType = new JsonObject();
        Path directory = Path.of(imageUploadPath);
        if (directory != null && !Files.exists(directory, new LinkOption[0])) {
            Files.createDirectories(directory);
        }

        Iterator var6 = routingContext.fileUploads().iterator();

        while(var6.hasNext()) {
            FileUpload fileUpload = (FileUpload)var6.next();
            Iterator var8 = propertyNames.iterator();

            while(var8.hasNext()) {
                String uploadProperty = (String)var8.next();
                if (fileUpload.name().equals(uploadProperty)) {
                    String uploadedFile = fileUpload.uploadedFileName();
                    Path source = Paths.get(uploadedFile);
                    String uploadedFileName = imageUploadPath + fileName;
                    Path target = Paths.get(uploadedFileName);

                    try {
                        if (Files.exists(Path.of(uploadedFileName), new LinkOption[0])) {
                            Files.delete(target);
                        }

                        Files.move(source, target);
                        uploadType.put(uploadProperty, uploadedFileName);
                    } catch (FileAlreadyExistsException var15) {
                        FileAlreadyExistsException fae = var15;
                        throw fae;
                    } catch (Exception var16) {
                        Exception e = var16;
                        throw e;
                    }
                }
            }
        }

        return uploadType;
    }

    public static JsonObject uploadImage(List<String> propertyNames, String imageUploadPath, RoutingContext routingContext) throws Exception {
        JsonObject uploadType = new JsonObject();
        Iterator var4 = routingContext.fileUploads().iterator();

        while(var4.hasNext()) {
            FileUpload fileUpload = (FileUpload)var4.next();
            Iterator var6 = propertyNames.iterator();

            while(var6.hasNext()) {
                String uploadProperty = (String)var6.next();
                if (fileUpload.name().equals(uploadProperty)) {
                    String uploadedFile = fileUpload.uploadedFileName();
                    Path source = Paths.get(uploadedFile);
                    String uploadedFileName = imageUploadPath + DateUtils.getCurrentTimeInMillis() + fileUpload.fileName();
                    Path target = Paths.get(uploadedFileName);

                    try {
                        Files.move(source, target);
                        uploadType.put(uploadProperty, uploadedFileName);
                    } catch (FileAlreadyExistsException var13) {
                        FileAlreadyExistsException fae = var13;
                        throw fae;
                    } catch (Exception var14) {
                        Exception e = var14;
                        throw e;
                    }
                }
            }
        }

        return uploadType;
    }

    private static boolean isValidImageFileExtension(String uploadedFile) {
        String[] strings = uploadedFile.split(",");
        return strings[0].equals("data:image/jpeg;base64");
    }

    public static String validateImageSizeAndExtension(String uploadedFile) {
        if (!isValidImageFileExtension(uploadedFile)) {
            return "Image file extension must be .jpeg";
        } else {
            double length = (double)uploadedFile.replaceAll("=*", "").length() * 0.75;
            double fileUploadSizeInKB = length * 9.765625E-4;
            return fileUploadSizeInKB > (double)imageUploadMaximumSize ? "Uploaded File exceeds maximum file size limit of " + imageUploadMaximumSize + " KB" : null;
        }
    }
}

