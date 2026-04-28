package in.edu.kristujayanti.propertyBinder.RazorpayPayment;


import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import org.w3c.dom.Document;

public enum RazorpayKeysPBinder implements KJUSYSPropertyBinder {

    CURRENCY_TYPE("currencyType_RazorpayKeys_Text","Currency Type", String.class),
    RECEIPT_NUMBER("receiptNumber_RazorpayKeys_Text","receipt Number", String.class),
    PAYMENT_ORDER_NOTES("paymentOrderNotes_RazorpayKeys_Document","Order Notes", Document.class),
    PAYMENT_CAPTURE_STATUS("paymentCaptureStatusRazorpayKeys_Int","Payment Capture", Integer.class),
    ORDER_STATUS("orderStatus_RazorpayKeys_Text","Order status", String.class),
    PAYMENT_MODE_NAME("paymentModeName_RazorpayKeys_Text","Payment Mode Name",String.class),
    RAZORPAY_PAYMENT_ID("razorpayPaymentId_RazorpayKeys_Text","Razorpay Payment",String.class)
    ;


// new changes

    private final String property;
    private final String label;
    private final Class dataType;
    private final int minLength;
    private final int maxLength;

    private RazorpayKeysPBinder(String property, String label, Class dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private RazorpayKeysPBinder(String property, String label, Class dataType) {
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

    public Class getDataType() {
        return this.dataType;
    }

    public Integer getMinLength() {
        return this.minLength;
    }

    public Integer getMaxLength() {
        return this.maxLength;
    }

    // New method to get label by property name
    public static String getLabelByProperty(String propertyName) {
        for (RazorpayKeysPBinder binder : values()) {
            if (binder.property.equals(propertyName)) {
                return binder.label;
            }
        }
        return null;
    }

    // New method to get property name by label
    public static String getPropertyByLabel(String label) {
        for (RazorpayKeysPBinder binder : values()) {
            if (binder.label.equals(label)) {
                return binder.property;
            }
        }
        return null;
    }

    // $FF: synthetic method
    private static RazorpayKeysPBinder[] $values() {
        return RazorpayKeysPBinder.class.getEnumConstants();
    }
}



