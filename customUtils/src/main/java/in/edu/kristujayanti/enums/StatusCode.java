package in.edu.kristujayanti.enums;

public enum StatusCode {
    TWOHUNDRED(200),
    BAD_REQUEST(400),
    INTERNAL_SERVER_ERROR(500),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    CONFLICT(409),
    TOKEN_EXPIRED_OR_AUTHENTICATION_FAILED(498),
    FILE_NOT_FOUND(404)
    ;

    private final int statusCode;

    private StatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    // Static method to find StatusCode by its integer value
    public static StatusCode fromStatusCode(int statusCode) {
        for (StatusCode status : StatusCode.values()) {
            if (status.getStatusCode() == statusCode) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching StatusCode for " + statusCode);
    }

    // New method to find StatusCode by its name as a String
    public static int fromStatusString(String statusString) {
        try {
            // Convert the input string to uppercase and replace underscores with empty strings
            String formattedString = statusString.toUpperCase().replace("_", "");
            // Use Enum.valueOf() to find the matching enum value
            StatusCode status = StatusCode.valueOf(formattedString);
            // Return the status code of the found enum value
            return status.getStatusCode();
        } catch (IllegalArgumentException e) {
            // Handle the case where no matching enum value is found
            throw new IllegalArgumentException("No matching StatusCode for " + statusString);
        }
    }

    // Function to validate and return the statusCode if it exists
    public static Integer validateStatusCode(int statusCode) {
        for (StatusCode status : StatusCode.values()) {
            if (status.getStatusCode() == statusCode) {
                return statusCode; // Return the input statusCode if it's valid
            }
        }
        return null; // Or handle differently if you prefer not to return null
    }
}
