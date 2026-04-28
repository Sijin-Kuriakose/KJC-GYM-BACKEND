package in.edu.kristujayanti.collectionNames;





public enum TicketCNBinder implements KJUSYSCNBinder {
    TICKET_COLLECTION("ticketCollection"),
    ;

    private final String collectionName;

    private TicketCNBinder(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    // $FF: synthetic method
    private static TicketCNBinder[] $values() {
        return TicketCNBinder.class.getEnumConstants();
    }




}
