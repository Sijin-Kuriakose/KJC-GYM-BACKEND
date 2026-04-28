package in.edu.kristujayanti.constants;

import java.util.Arrays;
import java.util.List;

public interface CommonKeys {
    String PORT = "port";
    List<String> BUILD_ENVIRONMENT = Arrays.asList("development", "CI", "demo", "production");
    String API_NAME = "api_name";
    String X_REQUEST_PATH = "X-REQUEST-PATH";
    String X_REQUEST_METHOD = "X-REQUEST-METHOD";
    String X_AUTH_CORRELATION_ID = "x-auth-correlation-id";
    String IP = "ip";
    String CONTENT_TYPE = "Content-Type";
    String AES_SECRET_KEY = "aes_secret_key";
    String AES_IV_VALUE = "aes_iv_value";
    String SHA_256_SECRET_KEY = "sha_256_secret_key";
    String BCC_EMAIL_NOTIFIER = "bcc_email_notifier";
    String NO_PARAMETERS = "No Parameters";
}
