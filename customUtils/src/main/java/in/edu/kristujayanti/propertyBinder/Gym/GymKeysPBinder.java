package in.edu.kristujayanti.propertyBinder.Gym;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import org.bson.Document;
import org.bson.types.ObjectId;

public enum GymKeysPBinder implements KJUSYSPropertyBinder{
    //Users
    USER_OID("userOid_Gym_ObjectId","User ObjectId", ObjectId.class),
    NAME("name_Gym_Text","User Name", String.class),
    USER_TYPE("userType_Gym_Text","User Type", String.class),
    USER_ID("userId_Gym_Text","User Id", String.class),
    CONTACT_NUMBER("contactNumber_Gym_Long","Contact Number", Long.class),
    EMAIL_ID("emailId_Gym_Text","Email Id", String.class),
    SUBSCRIPTION_SERVICE_LIST("subscriptionService_Gym_DocumentArray","Subscription Service List", Document.class),
    SUBSCRIPTION_ADDON_LIST("subscriptionAddonList_G    ym_DocumentArray","Subscription Addon List", Document.class),
    SUBSCRIPTIONS_LIST("subscriptionList_Gym_DocumentArray","Subscription List", Document.class),
    //Service
    SERVICE_OID("serviceOid_Gym_ObjectId","Service Id", ObjectId.class),
    SERVICE_NAME("serviceName_Gym_Text","Service Name", String.class),
    DESCRIPTION_SERVICE("descriptionService_Gym_Text"," Description Service", String.class),
    DURATION_SERVICE("durationService_Gym_Int","Duration Service", Integer.class),
    MEMBER_TYPE("memberType_Gym_Text","Member Type", String.class),
    AMOUNT_SERVICE("amountService_Gym_Double","Amount Service", Double.class),
    SERVICE_STATUS("service_Status_Bool","Service Status",Boolean.class),
    //Addon
    ADDON_OID("addonOid_Gym_ObjectId","Addon Id", ObjectId.class),
    ADDON_NAME("addonName_Gym_Text","Addon Name", String.class),
    DESCRIPTION_ADDON("descriptionAddon_Gym_Text"," Description Addon", String.class),
    DURATION_ADDON("durationAddon_Gym_Int","Duration Addon", Integer.class),
    AMOUNT_ADDON("amountAddon_Gym_Double","Amount Addon", Double.class),
    ADDON_START_DATE("addonStartDate_Gym_Date","Addon Start Date", Long.class),
    ADDON_END_DATE("addonEndDate_Gym_Date","Addon End Date", Long.class),
    ADDON_STATUS("addon_Status_Bool","Addon Status",Boolean.class),


    SUB_START_DATE("subStartDate_Gym_Date","Subscription Start Date", Long.class),
    SUB_END_DATE("subEndDate_Gym_Date","Subscription End Date", Long.class),
    PREFERRED_START_DATE("preferredStartDate_Gym_Date","Preferred Start Date", Long.class),
    TOTAL_COST("total_Cost_Gym_Double","Total Cost", Double.class),
    PAYMENT_DATE("paymentDate_Gym_Date","Payment Date", Long.class),
    METHOD("method_Gym_Text","Method", String.class),
    STATUS("status_Gym_Bool","Status", Boolean.class),
    AMOUNT_PAYABLE("amount_Payable_Gym_Double","Amount Payable",Double.class),

    //Attendance
    ATTENDANCE_OID ("attendanceOid_Gym_Text","Attendance Oid", String.class),
    CHECK_IN("checkIn_Gym_DateTime","Check In", Long.class),
    CHECK_OUT("checkOut_Gym_DateTime","Check Out", Long.class);

//    ATTENDANCE_DATE




    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;

    private final int maxLength;

    private GymKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private GymKeysPBinder(String property, String label, Class<?> dataType) {
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

    private static GymKeysPBinder[] $values() {
        return GymKeysPBinder.class.getEnumConstants();
    }

}
