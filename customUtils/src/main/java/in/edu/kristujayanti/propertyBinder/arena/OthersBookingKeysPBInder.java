package in.edu.kristujayanti.propertyBinder.arena;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import in.edu.kristujayanti.propertyBinder.Library.LibraryCommonKeysPBinder;
import org.bson.types.ObjectId;

public enum OthersBookingKeysPBInder implements KJUSYSPropertyBinder {
    ARENA_OTHERS_BOOKING_VENUE_OBJECT_ID("arenaOthersBookingVenueObjectId_OthersBooking_ObjectId", "arena OthersBookingObjectId", ObjectId.class),
    ARENA_OTHERS_BOOKING_USER_ID("arenaOthersBookingUserId_OthersBooking_Text", "arena OthersBookingUserId", String.class),
    ARENA_OTHERS_BOOKING_DATE("arenaOthersBookingDate_OthersBooking_Date", "arena OthersBookingDate", Long.class);

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private OthersBookingKeysPBInder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private OthersBookingKeysPBInder(String property, String label, Class<?> dataType) {
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

