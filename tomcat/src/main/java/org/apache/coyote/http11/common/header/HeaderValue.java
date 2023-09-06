package org.apache.coyote.http11.common.header;

public abstract class HeaderValue {

    protected static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    protected static final String SPACE = " ";

    private final HeaderProperty headerProperty;

    protected HeaderValue(final HeaderProperty headerProperty) {
        this.headerProperty = headerProperty;
    }

    public abstract String convertToString();

    public HeaderProperty getHeaderProperty() {
        return headerProperty;
    }

    public String getHeaderPropertyName() {
        return headerProperty.getPropertyName();
    }
}
