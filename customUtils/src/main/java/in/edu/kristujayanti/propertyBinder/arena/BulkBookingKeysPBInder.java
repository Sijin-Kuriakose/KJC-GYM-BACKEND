package in.edu.kristujayanti.propertyBinder.arena;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import in.edu.kristujayanti.propertyBinder.Library.LibraryCommonKeysPBinder;
import org.bson.types.ObjectId;

public enum BulkBookingKeysPBInder implements KJUSYSPropertyBinder {
    ARENA_BULK_BOOKING_FROM_DATE("arenaBulkBookingFromDate_BulkBooking_Date", "arena BulkBookingFromdate", Long.class),
    ARENA_BULK_BOOKING_TO_DATE("arenaBulkBookingToDate_BulkBooking_Date", "arena BulkBookingToDate", Long.class),
    ARENA_BULK_BOOKING_VENUE_OBJECT_ID("arenaBulkBookingVenueObjectId_BulkBooking_ObjectId", "arena BulkBookingObjectId", ObjectId.class);

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private BulkBookingKeysPBInder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private BulkBookingKeysPBInder(String property, String label, Class<?> dataType) {
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
