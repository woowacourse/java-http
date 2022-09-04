package org.apache.coyote.http11;

public class RequestParameter {

    private static final String PARAMETER_SPLITTER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String key;
    private final String value;

    public RequestParameter(final String parameter) {
        String[] splitParameter = parameter.split(PARAMETER_SPLITTER);
        this.key = splitParameter[KEY_INDEX];
        this.value = splitParameter[VALUE_INDEX];
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
