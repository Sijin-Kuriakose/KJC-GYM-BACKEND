package in.edu.kristujayanti.propertyBinder.QueueManager;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

import java.util.List;

public enum QueueManagerKeysPBinder implements KJUSYSPropertyBinder {

    TOKEN_SEQUENCE_START("tokenSequenceStart_QueueManager_Int", "Token Number Start", Integer.class),
    TOKEN_LATEST_COUNT("tokenLatestCount_QueueManager_Int", "Latest Token Number", Integer.class),
    CUSTOMER_ROLL_NO("customerRollNo_QueueManager_Text", "Customer Roll No", String.class),
    CUSTOMER_EMAIL("customerEmail_QueueManager_Text", "Customer Email", String.class),
    TOKEN_STATUS("tokenStatus_QueueManager_Text", "Token Status", String.class),
    COUNTER_NO("counterNo_QueueManager_Int", "Counter No", Integer.class),
    CUSTOMER_PURPOSE("customerPurpose_QueueManager_Text", "Purpose of Visit", String.class),
    CUSTOMER_TOKEN_NUMBER("customerTokenNumber_QueueManager_Int", "Token Number", Integer.class),
    GENERATED_ON("generatedOn_QueueManager_DateTime", "Token Generated On", Long.class),
    IS_TOKEN_GENERATABLE("isTokenGeneratable_QueueManager_Bool", "Is Token Generatable", Boolean.class),
    COUNTER_ASSIGNED("counterAssigned_QueueManager_Int", "Counter Assigned", Integer.class),
    IS_COUNTER_AVAILABLE("isCounterAvailable_QueueManager_Bool", "Is Counter Available", Boolean.class),
    COUNTER_ASSIGNED_TO("counterAssignedTo_QueueManager_Text", "Counter Assigned To", String.class),
    COUNTER_NUMBERS("counterNumbers_QueueManager_IntArray", "Counter Numbers", Integer.class),
    TOKEN_SEQUENCE_STATUS("tokenSequenceStatus_QueueManager_Text", "Token Sequence Start", String.class)
    ;

    ;



    private final String property;
    private final String label;
    private final Class dataType;
    private final int minLength;
    private final int maxLength;

    private QueueManagerKeysPBinder(String property, String label, Class dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private QueueManagerKeysPBinder(String property, String label, Class dataType) {
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

    // $FF: synthetic method
    private static in.edu.kristujayanti.propertyBinder.QueueManager.QueueManagerKeysPBinder[] $values() {
        return in.edu.kristujayanti.propertyBinder.QueueManager.QueueManagerKeysPBinder.class.getEnumConstants();
    }
}

