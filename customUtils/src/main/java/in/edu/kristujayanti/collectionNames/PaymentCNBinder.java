package in.edu.kristujayanti.collectionNames;


public enum PaymentCNBinder implements KJUSYSCNBinder {

    ORDER_DETAILS_COLLECTION("order_details"),
    PAYMENT_DETAILS_COLLECTION("payment_details"),
    FAILED_PAYMENT_COLLECTION("failedPaymentCollection"),
    TRANSFER_COLLECTION("transferCollection"),
    PAYMENT_MODES_COLLECTION("paymentModeDetails")

            ;


    private final String collectionName;

    private PaymentCNBinder(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    // $FF: synthetic method
    private static AuthCNBinder[] $values() {
        return AuthCNBinder.class.getEnumConstants();
    }


}

