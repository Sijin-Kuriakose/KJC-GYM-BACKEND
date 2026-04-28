package in.edu.kristujayanti.propertyBinder.arena;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import in.edu.kristujayanti.propertyBinder.Library.LibraryCommonKeysPBinder;
import org.bson.types.ObjectId;

import java.util.List;

public enum UserBookingKeysPBInder implements KJUSYSPropertyBinder {
    ARENA_EVENT_NAME("arenaEventName_UserBooking_Text", "arena EventName", String.class),
    ARENA_RESOURCE_PERSON("arenaResourcePerson_UserBooking_Text", "arena ResourcePerson", String.class),
    ARENA_DEPARTMENT_NAME("arenaDepartmentName_UserBooking_Text", "arena DepartmentName", String.class),
    ARENA_EVENT_TYPE("arenaEventType_UserBooking_Text", "arena EventType", String.class),
    ARENA_ADDITIONAL_REQUIREMENTS("arenaAdditionalRequirements_UserBooking_TextArray", "arena AdditionalRequirements", String.class),
    ARENA_EXTRA_REQUIREMENTS("arenaExtraRequirements_UserBooking_Text", "arena ExtraRequirements", String.class),
    ARENA_IS_SCHEDULED_AS_PER_ACADEMIC_CALENDAR("arenaIsScheduledAsPerAcademicCalendar_UserBooking_Bool", "arena IsScheduledAsPerAcademicCalendar", Boolean.class),
    ARENA_MODE_OF_EVENT("arenaModeOfEvent_UserBooking_Text", "arena ModeOfEvent", String.class),
    ARENA_EVENT_DATE("arenaEventDate_UserBooking_Date", "arena EventDate", Long.class),
    ARENA_MEMENTO_QUANTITY("arenaMementoQuantity_UserBooking_Int", "arena Memento Quantity", Integer.class),
    ARENA_LAPTOP_QUANTITY("arenaLaptopQuantity_UserBooking_Int", "arena LaptopQuantity", Integer.class),
    ARENA_SAPLINGS_QUANTITY("arenaSaplingsQuantity_UserBooking_Int", "arena SaplingsQuantity", Integer.class),
    ARENA_BOOKED_SLOTS("arenaBookedSlots_UserBooking_TextArray", "arena BookedSlots", String.class),
    ARENA_BOOKING_ID("arenaBookingId_UserBooking_Text", "arena BookingId", String.class),
    ARENA_STATUS("arenaStatus_UserBoooking_Text", "arena Status", String.class),
    ARENA_BOOKING_DATE_AND_TIME("arenaBookingDateAndTime_UserBooking_DateTime", "arena BookingDateAndTime", Long.class),


    ARENA_BOOKED_BY_USER_NAME("arenaBookedByUserName_UserBooking_Text", "arena Username", String.class),
    ARENA_BOOKED_BY_USER_ID("arenaBookedByUserId_UserBooking_ObjectId", "arena UserId", ObjectId.class),
    ARENA_BOOKED_TO_USER_NAME("arenaBookedToUserName_UserBooking_Text", "arena Username", String.class),
    ARENA_BOOKED_TO_USER_ID("arenaBookedToUserId_UserBooking_ObjectId", "arena UserId", ObjectId.class),



    ARENA_BOOKING_COUNTER_VARIABLE("arenaBookingCounterVariable_UserBooking_Int", "arena BookingCounterVariable", Integer.class),
    ARENA_BOOKING_COUNTER_VARIABLE_CONSTANT("arenaBookingCounterVariableConstant_UserBooking_Int", "arena BookingCounterVariableConstant", Integer.class),
    ARENA_SPOT_NAME("arenaSpotName_UserBooking_Text", "arena VenueSpotName", String.class),
    ARENA_VENUE_SPOT("arenaVenueSpot_UserBooking_Text", "arena VenueSpot", String.class),
    ARENA_VENUE_ID_COUNTER("arenaVenueIdCounter_UserBooking_Text", "arena VenueIdCounter", String.class),
    ARENA_VENUE_TYPE("arenaVenueType_UserBooking_Text", "arena VenueType", String.class),
    ARENA_OBJECT_ID("arenaObjectId_UserBooking_ObjectId", "arena ObjectId", ObjectId.class),


    ARENA_CANCELLED_BY("arenaCancelledBy_UserBooking_Text", "arena CancelledBy", String.class),
    ARENA_CANCELLED_BY_ID("arenaCancelledById_UserBooking_ObjectId", "arena CancelledById", ObjectId.class),
    ARENA_CANCELLED_BOOKING_REASON("arenaCancelledBookingReason_UserBooking_Text", "arena CancelledBookingReason", String.class),
    ARENA_CANCELLED_BOOKING_TIMESTAMP("arenaCancelledBookingTimestamp_UserBooking_DateTime", "arena CancelledBookingTimestamp", Long.class),
    ARENA_CANCELLED_SLOTS("arenaCancelledSlots_UserBooking_TextArray", "arena CancelledSlots", String.class),
    ARENA_BOOKING_ID_FOR_CANCELLATION("arenaBookingIdForCancellation_UserBooking_Text", "arena BookingIdForCancellation", String.class),
    ARENA_USER_EMAIL("arenaUserEmail_UserBooking_Text", "arena UserEmail", String.class),
    ARENA_USER_PHONE_NUMBER("arenaUserPhoneNumber_UserBooking_Long", "arena UserPhoneNumber", Long.class);










    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private UserBookingKeysPBInder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private UserBookingKeysPBInder(String property, String label, Class<?> dataType) {
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

    private static LibraryCommonKeysPBinder[] $values() {
        return (LibraryCommonKeysPBinder[])LibraryCommonKeysPBinder.class.getEnumConstants();
    }
}

