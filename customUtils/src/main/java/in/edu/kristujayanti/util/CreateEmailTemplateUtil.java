package in.edu.kristujayanti.util;

import io.vertx.core.buffer.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.services.sesv2.model.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CreateEmailTemplateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateEmailTemplateUtil.class);
    protected static final String REGION = "AP_SOUTH_1";

    private final Properties emailProperties;

    public CreateEmailTemplateUtil() {
        this.emailProperties = loadEmailProperties();
    }

    private Properties loadEmailProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("aws_config.properties")) {
            if (input == null) {
                LOGGER.error("Unable to find aws_config.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.error("Error loading aws properties", ex);
        }
        return properties;
    }

    protected AwsBasicCredentials loadAwsCredentials() throws IOException {
        return AwsBasicCredentials.create(this.emailProperties.getProperty("aws.accessKeyId"), this.emailProperties.getProperty("aws.secretKey"));
    }

    protected static SesClient initializeSesClient(AwsBasicCredentials awsCreds, String region) {
        return SesClient.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public boolean createEmailTemplate(String templateName, Buffer fileData, String subjectPart) {
        try {
            AwsBasicCredentials awsCreds = loadAwsCredentials();
            SesClient client = initializeSesClient(awsCreds, REGION);

            createTemplate(templateName, fileData, subjectPart, client);
            return true;
        } catch (IOException e) {
            LOGGER.error("Error initializing AWS credentials", e);
            return false;
        }
    }

    public void createTemplate(String templateName, Buffer fileData, String subjectPart, SesClient client) {
        try {
            String htmlContent = fileData.toString();
            CreateTemplateRequest createTemplateRequest = CreateTemplateRequest.builder()
                    .template(Template.builder()
                            .templateName(templateName)
                            .subjectPart(subjectPart)
                            .htmlPart(htmlContent)
                            .build())
                    .build();
            CreateTemplateResponse createTemplateResponse = client.createTemplate(createTemplateRequest);
            LOGGER.info("Template created successfully: {}", templateName);
        } catch (Exception e) {
            LOGGER.error("Error creating template: {}", e.getMessage());
        }
    }

    public boolean deleteEmailTemplate(String templateName) {
        try {
            AwsBasicCredentials awsCreds = loadAwsCredentials();
            SesClient client = initializeSesClient(awsCreds, REGION);
            DeleteTemplateRequest deleteTemplateRequest = DeleteTemplateRequest.builder()
                    .templateName(templateName)
                    .build();
            DeleteTemplateResponse deleteTemplateResponse = client.deleteTemplate(deleteTemplateRequest);
            LOGGER.info("Template deleted successfully: {}", templateName);
            return true;
        } catch (IOException e) {
            LOGGER.error("Error initializing AWS credentials", e);
            return false;
        } catch (NotFoundException e) {
            LOGGER.error("Template not found: {}", templateName);
            return false;
        } catch (Exception e) {
            LOGGER.error("Error deleting template: {}", e.getMessage());
            return false;
        }
    }
}