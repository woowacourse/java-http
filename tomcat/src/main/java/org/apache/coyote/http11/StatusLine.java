package org.apache.coyote.http11;

public class StatusLine {

    private static final String VERSION = "HTTP/1.1";

    public static String from(Status status) {
        return VERSION + " " + status.getCode() + " " + status.getName() + " ";
    }

}
