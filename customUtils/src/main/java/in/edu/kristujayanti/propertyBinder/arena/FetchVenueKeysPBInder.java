package in.edu.kristujayanti.propertyBinder.arena;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;
import in.edu.kristujayanti.propertyBinder.Library.LibraryCommonKeysPBinder;

public enum FetchVenueKeysPBInder implements KJUSYSPropertyBinder {
    ARENA_FETCH_VENUE_TYPE("arenaFetchVenueType_FetchVenue_Text", "arena VenueId", String.class),
    ARENA_FETCH_DATE("arenaFetchDate_FetchVenue_Date", "arena VenueName", Long.class);

    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private FetchVenueKeysPBInder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private FetchVenueKeysPBInder(String property, String label, Class<?> dataType) {
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


