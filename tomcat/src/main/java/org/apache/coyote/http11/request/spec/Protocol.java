package org.apache.coyote.http11.request.spec;

public class Protocol {

    private static final String CARRIAGE_RETURN = "\r";
    private static final String LINE_FEED = "\n";

    private final String value;

    public Protocol(String value) {
        this.value = value.replace(CARRIAGE_RETURN, "").replace(LINE_FEED, "");
    }

    public String value() {
        return value;
    }
}
