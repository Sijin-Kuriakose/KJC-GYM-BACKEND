package in.edu.kristujayanti.propertyBinder;

import org.bson.types.ObjectId;

public enum EmailSenderCommonKeysPBinder implements KJUSYSPropertyBinder {

    RECIPIENT_EMAIL("recipientEmail_EmailSenderCommon_Text" , "Recipient Email" , String.class),
    SENDER_EMAIL("senderEmail_EmailSenderCommon_Text" , "Sender Email" , String.class),
    EMAIL_SUBJECT("emailSubject_EmailSenderCommon_Text" , "Email Subject" , String.class),
    EMAIL_BODY("emailBody_EmailSenderCommon_Text" , "Email Body" , String.class),
    EMAIL_ATTACHMENT("emailAttachment_EmailSenderCommon_File" , "Email Attachment" , String.class)

    ;

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private EmailSenderCommonKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private EmailSenderCommonKeysPBinder(String property, String label, Class<?> dataType) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = -1;
        this.maxLength = -1;
    }

    public String getPropertyName() {
        return this.property;
    }

    public String getDisplayName() {
        return this.label;
    }

    public Class<?> getDataType() {
        return this.dataType;
    }

    public Integer getMinLength() {
        return this.minLength;
    }

    public Integer getMaxLength() {
        return this.maxLength;
    }

    public static String getLabelByPropertyName(String propertyName) {
        for (EmailSenderCommonKeysPBinder binder : EmailSenderCommonKeysPBinder.values()) {
            if (binder.getPropertyName().equals(propertyName)) {
                return binder.getDisplayName();
            }
        }
        return null; // Or throw an exception if needed
    }

    // $FF: synthetic method
    private static EmailSenderCommonKeysPBinder[] $values() {
        return EmailSenderCommonKeysPBinder.class.getEnumConstants();
    }
}