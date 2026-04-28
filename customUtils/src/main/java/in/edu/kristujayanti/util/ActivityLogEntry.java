package in.edu.kristujayanti.util;

import in.edu.kristujayanti.propertyBinder.KJUSYSCommonKeysPBinder;
import org.bson.Document;

/**
 * Represents an entry in the activity log with fields for performed activity details.
 * Extends Document to utilize MongoDB document capabilities.
 */
public class ActivityLogEntry extends Document {

    // Constants representing keys for fields in the activity log entry
    private static final String TIMESTAMP = KJUSYSCommonKeysPBinder.ACTIVITY_TIMESTAMP.getPropertyName();
    private static final String ACTIVITY_PERFORMED_BY = KJUSYSCommonKeysPBinder.ACTIVITY_PERFORMED_BY.getPropertyName();
    private static final String UPDATED_ENTITY = KJUSYSCommonKeysPBinder.ACTIVITY_UPDATED_ENTITY.getPropertyName();
    private static final String UPDATED_FIELD = KJUSYSCommonKeysPBinder.ACTIVITY_UPDATED_FIELD.getPropertyName();
    private static final String OLD_VALUE = KJUSYSCommonKeysPBinder.ACTIVITY_OLD_VALUE.getPropertyName();
    private static final String NEW_VALUE = KJUSYSCommonKeysPBinder.ACTIVITY_NEW_VALUE.getPropertyName();
    private static final String SECTION_NAME = KJUSYSCommonKeysPBinder.SECTION_NAME.getPropertyName();

    /**
     * Default constructor initializes an empty activity log entry with the current timestamp.
     */
    public ActivityLogEntry() {
        super();
        append(TIMESTAMP, DateUtils.getCurrentTimeInMillis());
    }

    /**
     * Constructs an activity log entry with specified details and the current timestamp.
     *
     * @param activityPerformedBy The user or entity that performed the activity.
     * @param updatedEntity       The entity or object that was updated.
     * @param updatedField        The specific field within the entity that was updated.
     * @param oldValue            The previous value of the updated field.
     * @param newValue            The new value assigned to the updated field.
     */
    public ActivityLogEntry(String activityPerformedBy, String updatedEntity, String updatedField, Object oldValue, Object newValue) {
        super();
        append(TIMESTAMP, DateUtils.getCurrentTimeInMillis());
        setActivityPerformedBy(activityPerformedBy);
        setUpdatedEntity(updatedEntity);
        setUpdatedField(updatedField);
        setOldValue(oldValue);
        setNewValue(newValue);
    }

    /**
     * Constructs an activity log entry.
     *
     * @param activityPerformedBy The user who performed the activity.
     * @param updatedEntity The entity being updated.
     * @param updatedSection The section being updated.
     * @param updatedField The field being updated.
     * @param oldValue The previous value.
     * @param newValue The new value.
     */
    public ActivityLogEntry(String activityPerformedBy, String updatedEntity, String updatedSection,String updatedField, Object oldValue, Object newValue) {
        super();
        append(TIMESTAMP, DateUtils.getCurrentTimeInMillis());
        setActivityPerformedBy(activityPerformedBy);
        setUpdatedEntity(updatedEntity);
        setUpdatedField(updatedField);
        setOldValue(oldValue);
        setNewValue(newValue);
        setSectionValue(updatedSection);
    }

    /**
     * Converts the ActivityLogEntry instance to a MongoDB Document.
     *
     * @return Document representing the ActivityLogEntry.
     */
    public Document toDocument() {
        Document doc = new Document();
        doc.append(TIMESTAMP, getTimestamp())
                .append(ACTIVITY_PERFORMED_BY, getActivityPerformedBy())
                .append(UPDATED_ENTITY, getUpdatedEntity())
                .append(UPDATED_FIELD, getUpdatedField())
                .append(OLD_VALUE, getOldValue())
                .append(NEW_VALUE, getNewValue());

        if(getSectionName() != null){
            doc.append(SECTION_NAME, getSectionName());
        }

        return doc;
    }

    /**
     * Retrieves the timestamp when the activity was logged.
     *
     * @return The timestamp.
     */
    public long getTimestamp() {
        return getLong(TIMESTAMP);
    }

    /**
     * Retrieves the user or entity that performed the activity.
     *
     * @return The activity performer.
     */
    public String getActivityPerformedBy() {
        return getString(ACTIVITY_PERFORMED_BY);
    }

    /**
     * Sets the user or entity that performed the activity.
     *
     * @param activityPerformedBy The activity performer to set.
     */
    public void setActivityPerformedBy(String activityPerformedBy) {
        append(ACTIVITY_PERFORMED_BY, activityPerformedBy);
    }

    /**
     * Retrieves the entity or object that was updated.
     *
     * @return The updated entity or object.
     */
    public String getUpdatedEntity() {
        return getString(UPDATED_ENTITY);
    }

    /**
     * Sets the entity or object that was updated.
     *
     * @param updatedEntity The updated entity or object to set.
     */
    public void setUpdatedEntity(String updatedEntity) {
        append(UPDATED_ENTITY, updatedEntity);
    }

    /**
     * Retrieves the Section that was updated.
     *
     * @return The updated section name.
     */

    public String getSectionName(){
        return  getString(SECTION_NAME);
    }

    /**
     * Sets the specific section within the entity that was updated.
     *
     * @param updatedSection The section value being updated.
     */

    public void setSectionValue(String updatedSection) {
        append(SECTION_NAME, updatedSection);
    }

    /**
     * Retrieves the specific field within the entity that was updated.
     *
     * @return The updated field.
     */
    public String getUpdatedField() {
        return getString(UPDATED_FIELD);
    }

    /**
     * Sets the specific field within the entity that was updated.
     *
     * @param updatedField The updated field to set.
     */
    public void setUpdatedField(String updatedField) {
        append(UPDATED_FIELD, updatedField);
    }

    /**
     * Retrieves the previous value of the updated field.
     *
     * @return The old value.
     */
    public Object getOldValue() {
        return get(OLD_VALUE);
    }

    /**
     * Sets the previous value of the updated field.
     *
     * @param oldValue The old value to set.
     */
    public void setOldValue(Object oldValue) {
        append(OLD_VALUE, oldValue);
    }

    /**
     * Retrieves the new value assigned to the updated field.
     *
     * @return The new value.
     */
    public Object getNewValue() {
        return get(NEW_VALUE);
    }

    /**
     * Sets the new value assigned to the updated field.
     *
     * @param newValue The new value to set.
     */
    public void setNewValue(Object newValue) {
        append(NEW_VALUE, newValue);
    }

    /**
     * Returns a string representation of the activity log entry.
     *
     * @return String representation of the activity log entry.
     */
    @Override
    public String toString() {
        return "ActivityLogEntry {" +
                "\n  timestamp=" + getTimestamp() +
                ",\n  activityPerformedBy='" + getActivityPerformedBy() + '\'' +
                ",\n  updatedEntity='" + getUpdatedEntity() + '\'' +
                ",\n  updatedField='" + getUpdatedField() + '\'' +
                ",\n  oldValue=" + getOldValue() +
                ",\n  newValue=" + getNewValue() +
                "\n}";
    }

}
