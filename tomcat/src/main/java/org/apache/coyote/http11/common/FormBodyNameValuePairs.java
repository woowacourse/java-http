package org.apache.coyote.http11.common;

public class FormBodyNameValuePairs extends NameValuePairs {

    private static final String PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    public FormBodyNameValuePairs(String nameAndValue) {
        super(nameAndValue, PAIR_DELIMITER, KEY_VALUE_DELIMITER);
    }
}
