package org.apache.coyote.http11.common;

public class HeaderNameValuePairs extends NameValuePairs {

    private static final String PAIR_SEPARATOR = Constants.CRLF;
    private static final String NAME_VALUE_SEPARATOR = ": ";

    public HeaderNameValuePairs(String nameAndValue) {
        super(nameAndValue, PAIR_SEPARATOR, NAME_VALUE_SEPARATOR);
    }
}
