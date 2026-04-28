package in.edu.kristujayanti.propertyBinder;

public interface KJUSYSPropertyBinder {
    String getPropertyName();

    String getDisplayName();

    Class<?> getDataType();

    Integer getMinLength();

    Integer getMaxLength();
}