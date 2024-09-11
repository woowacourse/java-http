package org.apache.coyote.http11.common;

public class CookieNameValuePairs extends NameValuePairs {

    private static final String PAIR_SEPARATOR = "; ";
    private static final String NAME_VALUE_SEPARATOR = "=";

    public CookieNameValuePairs(String nameAndValue) {
        super(nameAndValue, PAIR_SEPARATOR, NAME_VALUE_SEPARATOR);
    }
}
