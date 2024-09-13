package org.apache.coyote.http;

public interface HttpComponent {

    String LINE_FEED = "\r\n";
    String SPACE = " ";
    String KEY_VALUE_SEPARATOR = "=";

    String asString();
}
