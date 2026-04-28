package in.edu.kristujayanti.propertyBinder.Ticketing;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

public enum TicketingKeysPBinder implements KJUSYSPropertyBinder {

    CHOOSE_SERVICE_FOR_TICKET("chooseServiceForTicket_Ticketing_Text","Choose Service For Ticket", String.class),
    LOCATION_FOR_TICKET("locationForTicket_Ticketing_Text","Ticket Location", String.class),
    TICKET_PRIORITY("ticketPriority_Ticketing_Text","Ticket Priority",String.class),
    TICKET_FILE_UPLOAD("ticketFileUpload_Ticketing_Text","Ticket File", String.class),
    TICKET_ISSUE_DESCRIPTION("ticketIssueDescription_Ticketing_Text","Ticket Description", String.class),
    TICKET_ID("ticketId_Ticketing_Text","Ticket ID",String.class),
    TICKET_STATUS("ticketStatus_Ticketing_Text","Ticket Status", String.class),
    TICKET_MODIFIED_BY("ticketModifiedBy_Ticketing_Text","Ticket Mofigied By", String.class),
    TICKET_LAST_MODIFIED_AT("ticketLastModifiedAt_Ticketing_Date","Ticket Modified At",Long.class),
    TICKET_COMMENT("ticketComment_Ticketing_Text","Ticket Comment",String.class),
    TICKET_RAISED_BY("ticketRaisedBy_Ticketing_Text","Ticket Raised By", String.class),
    TICKET_RAISED_AT("ticketRaisedAt_Ticketing_Date","Ticket Rasied Time", Long.class),
    TICKET_FILE_UPLOADED_AT("ticketFileUploadedAt_Ticketing_Date","Ticket File Upload Time", Long.class),
    TICKET_DOC_CREATED_AT("ticketDocCreatedAt_Ticketing_Date","Ticket Doc Created Time", Long.class),
    TICKET_FILE_NAME("ticketFileName_Ticketing_Text","Ticket File Name", String.class),
    TICKET_FROM_DATE("ticketFromDate_Ticketing_Date","Ticket From Date", Long.class),
    TICKET_TO_DATE("ticketToDate_Ticketing_Date","Ticket to Date",Long.class)
    ;



    private final String property;
    private final String label;
    private final Class dataType;
    private final int minLength;
    private final int maxLength;

    private TicketingKeysPBinder(String property, String label, Class dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private TicketingKeysPBinder(String property, String label, Class dataType) {
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
    private static in.edu.kristujayanti.propertyBinder.Ticketing.TicketingKeysPBinder[] $values() {
        return in.edu.kristujayanti.propertyBinder.Ticketing.TicketingKeysPBinder.class.getEnumConstants();
    }
}
